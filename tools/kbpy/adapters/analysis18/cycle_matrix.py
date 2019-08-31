import numpy as np
import pandas as pd

TITLE_NAME = "Cycle Matrix (experimental)"
SOURCE_NAME = "cycle_matrix"
LABELS = ["Team", "Exchange Speed", "Switch Speed", "Scale Speed"]


def calculate_prs(data, times=None):
    """
    :param data: 2d list where d1 separates by match and d2 separates by Scale Switch Exchange respectively
    :param times:
    :return: List of Scale Switch Exchange cycle speed approximations respectively
    """
    if times is None:
        times = tuple([135] * len(data))
    prs = np.linalg.lstsq(data, times, rcond=1)
    return prs[0]


def outtake_counts_by_team(manager):
    counts_by_team = {}

    for entry in manager.entries:
        if not entry.board.alliance() == "N":  # Check for Power ups

            if entry.team not in counts_by_team.keys():  # Make new list if team doesn't exist
                counts_by_team[entry.team] = []

            counts_by_team[entry.team].append((entry.count("Tele exchange"),
                                               entry.count("Tele alliance switch") + entry.count(
                                                   "Tele opponent switch"),
                                               entry.count("Tele scale")))
    return counts_by_team


def calc_speeds(manager):
    counted_data = outtake_counts_by_team(manager)
    for team in counted_data.keys():
        team_data = calculate_prs(counted_data[team])

        yield {
            "Team": team,
            "Exchange Speed": team_data[0],
            "Switch Speed": team_data[1],
            "Scale Speed": team_data[2]
        }


def compute_table(manager):
    return pd.DataFrame(calc_speeds(manager))[LABELS]
