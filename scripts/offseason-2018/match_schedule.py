import os

import pandas as pd

TITLE_NAME = "MATCH SCHEDULE"
SOURCE_NAME = "match_schedule"
LABELS = ["Red 1",
          "Red 2",
          "Red 3",
          "Blue 1",
          "Blue 2",
          "Blue 3"
          ]


def compute_table(_):
    if os.path.exists("schedule.csv"):
        return pd.read_csv("schedule.csv", index_col=0)
    return pd.DataFrame(columns=LABELS)
