from itertools import chain

import numpy as np
import pandas as pd

TITLE_NAME = "Scale Autos"
SOURCE_NAME = "scale_autos"
LABELS = ["Team",
          "Max coded blocks",
          "Maximum Success #",
          "Fastest Success Time",
          "Success/Attempt Ratio"
          ]


def get_autos_data(manager):
    scale_autos = {}
    for entry in manager.entries:
        if not entry.board.alliance() == "N":  # Check for Power ups

            if entry.team not in scale_autos.keys():  # Make new list if team doesn't exist
                scale_autos[entry.team] = []

            scale_autos[entry.team].append((entry.look("Auto scale attempt"), entry.look("Auto scale")))

    return scale_autos


def get_rows(manager):
    for team, auto_data in get_autos_data(manager).items():
        attempt, success = zip(*auto_data)

        attempt_counts = list(filter(bool, map(len, attempt)))
        success_counts = list(filter(bool, map(len, success)))
        attempt_success_counts = list(filter(bool, map(lambda x: sum(map(len, x)), auto_data)))

        success_times = list(chain(*success))

        attempt_sum = sum(attempt_counts)
        success_sum = sum(success_counts)

        attempt_success_sum = attempt_sum + success_sum

        yield {
            "Team": team,
            "Max coded blocks": max(attempt_success_counts) if attempt_success_counts else np.nan,
            "Maximum Success #": max(success_counts) if success_counts else np.nan,
            "Fastest Success Time": min(success_times) if success_times else np.nan,
            "Success/Attempt Ratio": success_sum / attempt_success_sum if attempt_success_sum != 0 else np.nan
        }


def compute_table(manager):
    return pd.DataFrame(get_rows(manager))[LABELS]
