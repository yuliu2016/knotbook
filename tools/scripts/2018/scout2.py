

"""
Library for an utility command terminal based on the Warp7 Scouting App

Functions:
- Parse data received from the app
- Generate Excel sheets for analysis

Authors: Yu Liu, Ben Lemke
Dependencies: python 2.7+, xlwt

Last App Version: 0.2.2
"""


from __future__ import print_function, division, unicode_literals


from sys import stderr, stdout
from os import walk, path, system
from datetime import datetime
from time import time
from cmd import Cmd
from re import compile as regexp
from json import load as get_json
from json import dump as write_json
from xlwt import Workbook as xl

import codecs

VERSION = __version__ = "0.2.2"

INTRO = "Warp7 Scouting Terminal v {}\nType '?' for help\n".format(__version__)
PROMPT = ">> "

DISPLAY_TIME_FORMAT = "%Y/%m/%d %H:%M:%S"
FILE_TIME_FORMAT = "%Y%m%d%H%M%S"
ENCODE_RE = "\d{1,3}_\d{1,4}_[^_]+_[0-9a-f]{8}_[0-9a-f]{8}_([0-9a-f]{4})*_.*"

DEFAULT_DIRS = {
    "boards" : "specs/",
    "xlout" : "xlout/",
    "csvscans" : "csvscans/",
    "logs" : "logs/",
    "internal" : "internal/"
    }

def read_csv_entries (file_name):
    """Returns a generator that contains entries read from a csv file"""
    
    read_file = open(file_name, "r")
    entries = read_file.readlines()
    read_file.close()

    for entry in entries:
        yield "".join(entry.split(",")[:-1])

def ftimestamp(timestamp):
    """Returns the formatted timestamp according to DISPLAY_TIME_FORMAT constant"""
    
    return datetime.fromtimestamp(timestamp).strftime(DISPLAY_TIME_FORMAT)

def ftimefile(timestamp):
    """Returns the formatted timestamp according to FILE_TIME_FORMAT constant"""
    
    return datetime.fromtimestamp(timestamp).strftime(FILE_TIME_FORMAT)


def check_encode_format(string):
    """Checks using regexp whether a string fits the encoding format"""
    
    return regexp(ENCODE_RE).match(string) is not None

def subfiles(parent_path):
    """Generates a list of all the files inside a directory"""

    for root, _, files in walk(parent_path):
        for f in files:
            yield path.join(root, f)

def scancsv(dirpath):
    """Scans the directory for board files and returns an array"""
    
    print("Scanning for CSV files...", end = "")
    
    files = [x for x in subfiles(dirpath) if x.endswith(".csv")]
    
    print("Found {} in \"{}\"".format(len(files), dirpath))
    
    return files

def scanboards(dirpath):
    """Scans the directory for board files and returns an array"""
    
    print("Scanning for JSON board data files...", end = "")
    
    files = [x for x in subfiles(dirpath) if x.endswith(".json") and not x.endswith("index.json")]
    
    print("Found {} in \"{}\"".format(len(files), dirpath))
    
    return files

def format_encode_value(value, state, constset):
    """Formats the encoded value based on the type"""
    
    if constset is None or "type" not in constset:
        return str(value)
    else:
        t = constset["type"]
        
        if t == "checkbox":
            return str(bool(value))
        
        elif t == "timestamp" or t == "duration":
            return str(value // 60) + "m " + str(value % 60) + "s "
        
        elif t == "rating":
            if "max" in constset:
                return str(value) + " out of " + str(constset["max"])
            else:
                return str(value)
            
        elif t == "choice":
            if "choices" in constset:
                if value >= 0 and value < len(constset["choices"]):
                    return "<" + str(constset["choices"][value]) + ">"
                else:
                    return str(value)
            else:
                return str(value)
            
        else:
            return str(value)

def default_format(e):
    """Returns all values in the dictionary"""

    for i in e.keys():
        yield str(e[i])


class Board:
    """Metadata about the robot scouted and the transmission constants"""

    def __init__(self, filename):
        """Tries to open the json file and read the data from it"""
        try:
            datafile = codecs.open(filename, "r", "utf-8")
            self.data = get_json(datafile)
            datafile.close()
            #__import__("pprint").pprint(self.data)
        except Exception as e:
            self.data = {}
            print(e)

    def __str__(self):
        return str(self.data)

    def __repr__(self):
        return str(self.data)

    def get_id(self):
        """Returns the numeral identifier of the board"""

        if "id" in self.data:
            return int(self.data["id"], 16)
        else:
            return 0

    def get_name(self):
        """Returns the name of the board"""

        if "board" in self.data:
            return self.data["board"]
        else:
            return "No board specified"

    def get_alliance(self):
        """Returns the letter of the alliance"""

        if "alliance" in self.data:
            return self.data["alliance"]
        else:
            return "Unknown"

    def get_index(self, str_id):
        """Returns the index from the string id"""

        if "data" in self.data:
            for i in range(len(self.data["data"])):
                # Note: this doesn't account for constants without id
                if self.data["data"][i]["id"] == str_id:
                    return i
            return -1
        return -1

    def get_constset(self, index):
        """Gets the data dictionary for a particular constant"""
        
        if "data" in self.data:
            d = self.data["data"]
            
            if index >= 0 and index < len(d):
                return d[index]
            else:
                return None
        else:
            return None

    def get_log(self, index):
        """Returns the log attribute from the constants index"""
    
        cs = self.get_constset(index)

        if cs is not None:
            if "log" in cs:
                return cs["log"]
            elif "id" in cs:
                return cs["id"]
            else:
                return str(index) + " (No names given)"
        else:
            return str(index) + " (No )"

class Entry:
    """Entry processor objects that give various output formats"""

    def __init__(self, encoded_string = None):

        if encoded_string is None:
            self.encoded = ""
            self.match = 0
            self.team = 0
            self.name = ""
            self.timestamp = 0
            self.board_id = 0
            self.data = []
            self.comments = ""
        else:
            self.load(encoded_string)

        self.board = None
        self.board_name = "No board specified"
        self.alliance = "Unknown"

        self.time_entered = int(time())

    def __repr__(self):
        return self.tablestr()

    def __str__(self):
        return self.tablestr()

    def __eq__(self, other):
        return self.match == other.match and \
               self.team == other.team and \
               self.board_id == other.board_id and \
               self.timestamp == other.timestamp
        
    def load(self, s):
        """Initializes the entry either from the encoded string"""
        
        self.encoded = s
        segment = self.encoded.split("_")

        self.match = int(segment[0])
        self.team = int(segment[1])
        self.name = segment[2]

        self.timestamp = int(segment[3], 16)
        self.board_id = int(segment[4], 16)

        self.data = []
        
        for i in range(len(segment[5]) // 4):
            dt = int(segment[5][i * 4 : (i + 1) * 4], 16)
            
            # Do bit extraction using AND operator and bitmasks
            datatype = (dt & (1 << 6) - 1 << 8) >> 8
            value = dt & ((1 << 8) - 1)
            undo_flag = bool(dt & 1 << 15)
            state_flag = bool(dt & 1 << 14)
            
            self.data.append((datatype, value, undo_flag, state_flag))

        self.comments = segment[6]

    def head(self):
        """Encodes the head data. Can be used for file names"""
        
        return str(self.match) + "_" + \
               str(self.team) + "_" + \
               self.name + "_" + \
               hex(self.timestamp)[2:].zfill(8)

    def tablestr(self):
        """Returns a human-readable table of info regarding the match"""
        
        s = "=" * 48 + "\n"
        
        
        col1 = ("Match Number:",
                "Team Number:",
                "Start Time:",
                "Scouter:",
                "Board ID:",
                "Board Name:",
                "Alliance:",
                "Time of Entry:")
        
        col2 = (str(self.match),
                str(self.team),
                ftimestamp(self.timestamp),
                self.name,
                hex(self.board_id),
                self.board_name,
                self.alliance,
                ftimestamp(self.time_entered))

        for i, j in zip(col1, col2):
            s += "{0:<16}{1}\n".format(i, j)
        
        s += "\n{0:<28}{1:<14}Undone\n".format("Data", "Value")
        s += "-" * 48

        for t, v, u, state in self.data:
            if self.board is not None:
                ft = "{:<5}".format("<{}>".format(t))
                ft += self.board.get_log(t)
                if not state:
                    ft += ":Off"
            else:
                ft = t
            s += "\n" + "{0:<28}".format(ft)

            fv = format_encode_value(v, state, self.board.get_constset(t))
            
            s += "{0:<14}".format(fv)
            s += "Yes" if u else "No"

        s += "\n\nComments\n"
        s += "-" * 48 + "\n"

        s += self.comments + "\n"
        
        s += "=" * 48 + "\n"
        
        return s

    def encode(self):
        """Returns an encoding string of the object"""
        
        s = self.head() + "_"
        s += hex(self.board_id)[2:].zfill(8) + "_"
        
        for t, v, u, state in self.data:
            dt = u << 15 | t << 8 | v 
            s += hex(dt)[2:].zfill(4)

        s += "_" + self.comments
        return s

    def set_board(self, board):
        """Assigns a Board object to the entry"""
        self.board = board
        self.board_name = board.get_name()
        self.alliance = board.get_alliance()

    def compile_data(self):
        """Returns a compiled dictionary"""
        
        cd = {}
        cd["Identity"] = self.head()
        cd["Team Number"] = self.team
        cd["Alliance Colour"] = self.alliance
        cd["Match Number"] = self.match
        cd["Comments"] = self.comments
        cd["Time Started"] = ftimestamp(self.timestamp)
        cd["Board"] = self.board_name

        for t, v, u, state in self.data:
            if not u:
                t_constset = self.board.get_constset(t)
                t_disp = t_constset["log"]

                if not state:
                    t_disp += ":Off"
                
                if t_constset["type"] == "timestamp" or \
                            t_constset["type"] == "duration":
                    if t_disp in cd:
                        cd[t_disp].append(v)
                    else:
                        cd[t_disp] = [v]
                else:
                    cd[t_disp] = v

        return cd

class Dataset:
    """A container for all data in the terminal"""

    def __init__(self, paths = DEFAULT_DIRS):
        """Sets up variables as well as preloading board files"""
        
        self.paths = paths
        
        self.boards = {}
        self.entries = []
        self.compiled = []
        self.format_generators = {}
        
        scancsv(self.paths["csvscans"])
        self.loadboards()

    def loadboards(self):
        """Loads all the board files under the specified path"""

        ## SHOULD ADD: IF ENTRIES THEN ASSIGN
        
        files = scanboards(self.paths["boards"])
        for f in files:
            print("Loading board data \"{}\" into memory...".format(f), end = "")
            board = Board(f)
            self.boards[board.get_id()] = board
            print("Done")

    def loadcsv(self):
        """Loads all the CSV scans under the specified path"""

        files = scancsv(self.paths["csvscans"])
        for f in files:
            print("Loading entry data from \"{}\"...".format(f), end = "")
            count = 1
            for e in read_csv_entries(f):
                print("Loading entry {}...".format(count), end = " ")
                self.add_entry(e)
                count += 1
            print("Done")

    def add_entry(self, encode_str):
        """Adds one entry to the list of entries"""
        
        if check_encode_format(encode_str):
            
            e = Entry(encode_str)
            
            if e.board_id in self.boards:
                e.set_board(self.boards[e.board_id])
                
                # Write to log files (move to another function?)

                print("Processing...", end = " ")
                try:
                    logpath = path.join(self.paths["logs"], "entrylog.txt")
                    logs = open(logpath, "a")
                    print(e, file = logs)
                    logs.close()

                    ecpath = path.join(self.paths["internal"], "_raw.sctpy")
                    eclogs = open(ecpath, "a")
                    print(encode_str, file = eclogs)
                    eclogs.close()
                    
                except Exception as e:
                    print(e)
                
                self.entries.append(e)
                print("Done")
            else:
                print("Board not found. Entry not added.", file=stderr)           
        else:
            print("Wrong encoding format. Maybe missing characters?",
                  file=stderr)

    def is_entry_compiled(self, identity):
        """Checks if a specific entry is already compiled"""

        for e in self.compiled:
            if e["Identity"] == identity:
                return True
            
        return False

    def read_compiled_file(self):
        """Attempts to read the compiled file for data"""

        p = path.join(self.paths["internal"], "_compiled.sctpy")

        print("Loading compiled data from \"{}\"...".format(p), end = "")
        
        if path.exists(p):
            try:
                cpfile = open(p, "r")
                self.compiled = get_json(cpfile)
                cpfile.close()
                print("Found {} entries".format(len(self.compiled)))
            except Exception as e:
                self.compiled = []
                print("No data found")
        else:
            self.compiled = []
            print("No data found")

    def write_compiled_data(self):
        """Writes the compiled data to the file"""

        p = path.join(self.paths["internal"], "_compiled.sctpy")

        ftime = ftimefile(time())
        p2 = path.join(self.paths["internal"], "_compiled_{}.sctpy".format(ftime)) 

        print("Saving compiled data to \"{}\"...".format(p), end = "")

        try:
            cpfile = open(p, "w")
            write_json(self.compiled, cpfile)
            cpfile.close()

            cp2file = open(p2, "w")
            write_json(self.compiled, cp2file)
            cp2file.close()
            
            print("Done")
        except:
            print("Unable to write to file")
            

    def compile_all(self):
        """Compiles the dataset into an array of dictionaries"""

        self.read_compiled_file()

        total = len(self.entries)

        for i in range(total):
            print("Compiling {} out of {} entries".format(i + 1, total))

            data = self.entries[i].compile_data()

            if not self.is_entry_compiled(data["Identity"]):
                self.compiled.append(data)

        self.write_compiled_data()

        for e in self.entries:
            del e

        self.entries = []

        print("Finished compiling {} entries".format(len(self.compiled)))


    def add_format_generater(self, name, header, function):
        """Sets a format generator to be used for excel"""
        
        self.format_generators[name] = (header, function)

    def xlwrite(self):
        """Writes to the Excel file with a preset generator"""

        self.compile_all()

        ftime = ftimefile(time())
        fp = path.join(self.paths["xlout"], "{}.xls".format(ftime))

        print("Writing to Excel file \"{}\"...".format(fp), end = "")


        if not self.format_generators:
            fg = {"Default Format" : ((), default_format)}
        else:
            fg = self.format_generators

        xlout = xl()
        for preset_name in fg.keys():
            
            pheader, pformat = fg[preset_name]
            sheet = xlout.add_sheet(preset_name)

            c = 0

            for i in pheader:
                sheet.write(0, c, i)
                c += 1

            r = 0
            for j in self.compiled:
                c = 0
                row = pformat(j)
                if row:
                    for k in row:
                        sheet.write(r + 1, c, k)
                        c += 1
                    r += 1

        xlout.save(fp)
        system("start " + fp)
        
        print("Done")


class Terminal(Cmd):
    """Wrapper around cmd.Cmd to create a terminal the scouting data model"""

    intro = "\n" + "=" * 60 + "\n" + INTRO
    prompt = PROMPT

    def init_data(self):
        """Sets a linked Dataset object to the terminal. Must be called"""
        
        self.dataset = Dataset()

    def emptyline(self):
        """Does nothing instead of repeating the last command"""
        pass

    def default(self, line):
        print("*** Undefined command: {}".format(line), file = stderr)
        

    def do_add(self, arg):
        """Parses the argument as an entry and add it to the data set"""
        
        if len(arg) > 0:
            print("Loading entry...", end = "")
            self.dataset.add_entry(arg)
        else:
            self.onecmd("help add")

    def do_cpall(self, arg):
        """Compiles everything to get ready to write in Excel"""

        self.dataset.compile_all()

    def do_cprint(self, arg):
        """Prints out the compiled dataset"""

        print(self.dataset.compiled)

    def do_p(self, arg):
        """Prints out the last entry in the data set (Same as 'print')"""
        
        self.do_print(arg)

    def do_printall(self, arg):
        """Prints all entries in the data set (Same as 'pall')"""

        for entry in self.dataset.entries:
            print(entry)

    def do_print(self, arg):
        """Prints out the last entry in the data set (Same as 'p')"""

        if len(arg) > 0:
            index = int(arg)
        else:
            index = len(self.dataset.entries) - 1

        if index >= 0 and index < len(self.dataset.entries):
            print(self.dataset.entries[index])
        else:
            print("There are no entries are found to be printed")
    
    def do_quit(self, arg):
        """Quits the terminal (Same as 'q')"""
        
        print("Quit the terminal?('Y' to continue)")
        if input().strip().upper()[0] == "Y":
            return True
        else:
            return False

    def do_q(self, arg):
        """Quits the terminal (Same as 'quit')"""
        
        return self.do_quit(arg)

    def do_loadboards(self, arg):
        """Wrapper call to Dataset.loadboards"""

        self.dataset.loadboards()

    def do_loadcsv(self, arg):
        """Wrapper call to Dataset.loadcsv"""

        self.dataset.loadcsv()

    def do_rawboards(self, arg):
        """Show the boards as raw dict data"""
        print(self.dataset.boards)

    def do_xlwrite(self, arg):
        """Writes data to excel"""

        self.dataset.xlwrite()


if __name__ == "__main__":
    #1_865_Ben_5a98e235_e3bb3f98_4001420346044d004e014f0150025100520353035400550256034b054706_
    terminal = Terminal()
    terminal.init_data()
    terminal.cmdloop()
