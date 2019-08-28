import numpy as np
import pandas as pd

TITLE_NAME = "Missing Entries"
SOURCE_NAME = "missing_entries"
LABELS = ["Match #",
          "Red 1",
          "Red 2",
          "Red 3",
          "Blue 1",
          "Blue 2",
          "Blue 3"
          ]


def compute_table(manager):
    # ms = manager["match_schedule"].data
    # se = manager["scouted_entries"].data
    # return ms[ms != se]
    ms = manager["match_schedule"].data
    scouted_entries = pd.DataFrame(index=ms.index, columns=LABELS[1:])

    for entry in manager.entries:
        if entry.board.alliance() != "N":
            m = "Quals {}".format(entry.match)
            b = entry.board.name()
            t = np.int32(entry.team)
            if m in scouted_entries.index:
                if int(ms.at[m, b]) == entry.team:
                    scouted_entries.at[m, b] = t
    return ms[ms != scouted_entries]
