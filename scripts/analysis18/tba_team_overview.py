import numpy as np
import pandas as pd

TITLE_NAME = "TBA team overview"
SOURCE_NAME = "tba_team_overview"
LABELS = ["Team",
          "Matches",
          "Wins",
          "Ties",
          "Losses",
          "Percent"]


def get_rows(manager):
    tba = manager.tba

    for team in tba.event_teams(event=manager.tba_event):
        wins = 0
        ties = 0
        losses = 0
        for match in tba.team_matches(event=manager.tba_event, team=team['key']):
            if match['comp_level'] != 'qm':
                continue

            if "frc" + str(team['team_number']) in match['alliances']['red']["team_keys"]:
                alliance = 'red'
            else:
                alliance = 'blue'

            if match['winning_alliance'] == alliance:
                wins += 1
            else:
                losses += 1

        if wins + ties + losses > 0:
            yield {"Team": int(team['team_number']),
                   "Matches": wins + ties + losses,
                   "Wins": wins,
                   "Ties": ties,
                   "Losses": losses,
                   "Percent": (wins + ties / 2) / (wins + ties + losses)}
        else:
            yield {"Team": int(team['team_number']),
                   "Matches": wins + ties + losses,
                   "Wins": wins,
                   "Ties": ties,
                   "Losses": losses,
                   "Percent": np.nan}


def compute_table(manager):
    if manager.tba_available:
        table = pd.DataFrame(get_rows(manager))[LABELS]
    else:
        table = pd.DataFrame({})
    return table
