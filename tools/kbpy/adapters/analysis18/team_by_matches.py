import pandas as pd

TITLE_NAME = "Team By Matches"
SOURCE_NAME = "team_by_matches"
LABELS = ["Team", "Matches"]


def get_team_data(manager):
    teams_data = {}
    for entry in manager.entries:
        if not entry.board.alliance() == "N":  # Check for Power ups

            if entry.team not in teams_data.keys():  # Make new list if team doesn't exist
                teams_data[entry.team] = []

            teams_data[entry.team].append(entry.match)

    return teams_data


def get_rows(manager):
    for team, matches in get_team_data(manager).items():
        m_str = ""
        for m in matches:
            m_str += "--{}    ".format(m)
        yield {
            "Team": team,
            "Matches": m_str
        }


def compute_table(manager):
    return pd.DataFrame(get_rows(manager))[LABELS]
