import numpy as np
import pandas as pd

TITLE_NAME = "Team Averages NonZero"
SOURCE_NAME = "team_averages_nonzero"
LABELS = ["Team",
          "Average Scale",
          "Average Alliance Switch",
          "Average Opponent Switch",
          "Average Exchange"]


def get_team_data(manager):
    teams_data = {}
    for entry in manager.entries:
        if not entry.board.alliance() == "N":  # Check for Power ups

            if entry.team not in teams_data.keys():  # Make new list if team doesn't exist
                teams_data[entry.team] = []

            teams_data[entry.team].append((entry.count("Tele scale"),
                                           entry.count("Tele alliance switch"),
                                           entry.count("Tele opponent switch"),
                                           entry.count("Tele exchange")))

    return teams_data


def avg(x):
    return (lambda s, l: s / l if s > 0 and l > 0 else np.nan)(sum(x), len(x))


def get_rows(manager):
    for team, counts in get_team_data(manager).items():
        scale, a_switch, o_switch, exchange = map(lambda x: list(filter(bool, x)), zip(*counts))

        yield {"Team": team,
               "Average Scale": avg(scale),
               "Average Alliance Switch": avg(a_switch),
               "Average Opponent Switch": avg(o_switch),
               "Average Exchange": avg(exchange)
               }


def compute_table(manager):
    return pd.DataFrame(get_rows(manager))[LABELS]
