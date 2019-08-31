import pandas as pd

TITLE_NAME = "Scouted Entries by Person"
SOURCE_NAME = "scouted_entries_persons"
LABELS = [
    "Red 1",
    "Red 2",
    "Red 3",
    "Blue 1",
    "Blue 2",
    "Blue 3"
]


def compute_table(manager):
    ms = manager["match_schedule"].data
    scouted_entries = pd.DataFrame(index=ms.index, columns=LABELS)

    for entry in manager.entries:
        if entry.board.alliance() != "N":
            m = "Quals {}".format(entry.match)
            b = entry.board.name()
            if m in scouted_entries.index:
                if int(ms.at[m, b]) == entry.team:
                    scouted_entries.at[m, b] = entry.name
    return scouted_entries
