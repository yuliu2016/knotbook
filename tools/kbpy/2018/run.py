
from scout2 import Terminal

def time_format(data):

    stuff=['Scale',
           'Scale Attempt',
           'Exchange Attempt',
           'Exchange',
           'End Defense',
           'Opponent switch',
           'Start Defense',
           'Intake',
           'Robot Crossed Line Time',
           'Scale Success',
           'Alliance Switch',
           'Switch Attempt']

    # new stuff
    
    new_stuff=['Tele scale',
           'Auto scale attempt',
           'Auto exchange attempt',
           'Tele exchange',
           'Defense:Off',
           'Tele opponent switch',
           'Defense',
           'Tele intake',
           'Auto Scale',
           'Tele alliance switch',
           'Auto switch attempt']

    if not data['Alliance Colour'] == "N":
        l = [""] * 153

        l[0] = data['Team Number']
        l[1] = data['Alliance Colour']
        l[2] = data['Match Number']

        for i in stuff:
            if i in data:
                for j in data[i]:
                    l[j+2]=i
        return l
    
    return []

def raw_data_format(dic):
    
    def ctch_blnk(name, intended_blank_value):
        value = look(name)
        if value == "":
            value = intended_blank_value
        return value
    def look (name):
        try:
            return dic[name]
        except:
            return ""
        
    def get_len (key):
        try:
            return len(dic[key])
        except:
            return 0
        
    def get_packed_lengths (keys):
        values = []
        for key in keys:
            values.append(get_len(key))
        return tuple(values)

    def success_attempt_to_single(success_key, attempt_key):
        if len(look(success_key)) == 0:
            if len(look(attempt_key)) == 0:
                value = 0
            else:
                value = 1
        else:
            value = 2
        return value
    
##    team = look("Team Number")
##    alliance = look("Alliance Colour")
##    match = look("Match Number")
##    auto = look("Robot Crossed Line")
##    exchange_auto = success_attempt_to_single("Exchange Attempt", "Exchange Success")
##    switch_auto = success_attempt_to_single("Switch Attempt", "Switch Success")
##    scale_auto = success_attempt_to_single("Scale Attempt", "Scale Success")
##    exchange = get_len("Exchange")
##    switch = get_len("Alliance Switch") + get_len("Opponent Switch")
##    scale = get_len("Scale")
##    exchange_placement = look("Exchange Speed") // 2    
##    switch_placement = look("Switch Speed") // 2
##    scale_placement = look("Scale Speed") // 2
##    intake_speed = look("Intake Speed") // 2
##    defence = get_len("Defence Start")
##    if defence != 0:
##        defence = 2
##    opponents_switch = get_len("Opponent Switch")
##    if opponents_switch != 0:
##        opponents_switch = 1
##        
##    platform = ctch_blnk("Platform",0)
##    climb = ctch_blnk("Climb",0)
##    climb_speed = look("Climb Speed") // 2
##    attachment_speed = look("Attachment Speed") // 2

    if not dic["Alliance Colour"] == "N":
        team = look("Team Number")
        alliance = look("Alliance Colour")
        match = look("Match Number")
        
        auto = look("Auto line")
        exchange_auto = success_attempt_to_single("Auto exchange attempt", "Auto exchange")
        switch_auto = success_attempt_to_single("Auto switch attempt", "Auto switch")
        scale_auto = success_attempt_to_single("Auto scale attempt", "Auto scale")
        
        exchange = get_len("Tele exchange")
        switch = get_len("Tele alliance switch") + get_len("Tele opponent switch")
        scale = get_len("Tele scale")
        
        exchange_placement = look("Exchange speed") // 2    
        switch_placement = look("Switch speed") // 2
        scale_placement = look("Scale speed") // 2
        intake_speed = look("Intake speed") // 2

        #################
        ## driver_skill = look("Driver skill")
        #################
        
        defence = get_len("Defense")
        if defence != 0:
            defence = 2
            
        opponents_switch = get_len("Tele opponent switch")
        if opponents_switch != 0:
            opponents_switch = 1
            
        platform = ctch_blnk("Platform",0)
        climb = ctch_blnk("Climb",0)
        climb_speed = look("Climb speed") // 2
        attachment_speed = look("Attachment speed") // 2

        l = [team,
             alliance,
             match,
             auto,
             exchange_auto,
             switch_auto,
             scale_auto,
             exchange,
             switch,
             scale,
             exchange_placement,
             switch_placement,
             scale_placement,
             intake_speed,
             defence,
             opponents_switch,
             platform,
             climb,
             climb_speed,
             attachment_speed]

        return l
    
    return []


header = ["team",
         "alliance",
         "match",
         "auto",
         "exchange_auto",
         "switch_auto",
         "scale_auto",
         "exchange",
         "switch",
         "scale",
         "exchange_placement",
         "switch_placement",
         "scale_placement",
         "intake_speed",
         "defence",
         "opponents_switch",
         "platform",
         "climb",
         "climb_speed",
         "attachment_speed"]

header2 = ["Team",
           "Alliance",
           "Match"] + ["Second {}".format(s + 1) for s in range(150)]

terminal = Terminal()
terminal.init_data()
terminal.dataset.add_format_generater("Raw Data", header, raw_data_format)
terminal.dataset.add_format_generater("Times", header2, time_format)
terminal.cmdloop()
