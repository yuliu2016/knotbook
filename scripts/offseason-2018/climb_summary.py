import numpy as np
import pandas as pd

TITLE_NAME = "Climb summary"  # The name to display
SOURCE_NAME = "climb_summary"  # The name to be accessed by other code
LABELS = ["Team",
          "Match",
          "Climbed",
          "Climb time",
          "Relative climb time",
          "Climb failed",
          "Lifted",
          "Lifting"
          ]  # Column labels for table, and row labels for lookup (later thing)

"""
"climb_type=[
        "None",
        "Platform",
        "Failed Climb",
        "Single Climb",
        "Double Climb",
        "Single Climb, Lifting Another Robot",
        "Lifted by Another Robot"
      ]
"""


def get_rows(manager):
    for entry in manager.entries:
        row_data = {}

        climbed_look = entry.look("Climbed timer")
        platform_look = entry.look("Platform timer")

        if len(climbed_look) % 2 == 1:
            if platform_look:
                row_data["Relative climb time"] = max(climbed_look) - max(entry.look("Platform timer"))
            else:
                row_data["Relative climb time"] = np.nan
            row_data["Climb time"] = max(climbed_look)
        else:
            row_data["Relative climb time"] = np.nan
            row_data["Climb time"] = np.nan

        yield {"Team": entry.team,
               "Match": entry.match,
               "Climbed": len(climbed_look) % 2,
               "Climb time": row_data["Climb time"],
               "Relative climb time": row_data["Relative climb time"],
               "Climb failed": int(entry.final_value("Endgame type") == 2),
               "Lifted": int(entry.final_value("Endgame type") == 6),
               "Lifting": int(entry.final_value("Endgame type") == 5)}


def compute_table(manager):
    table = pd.DataFrame(get_rows(manager), columns=LABELS)[LABELS]
    return table
