import numpy as np
import pandas as pd

TITLE_NAME = "Team Average Ratings"
SOURCE_NAME = "team_average_ratings"
LABELS = ["Team",
          "Attachment Speed",
          "Climb Speed",
          "Intake Speed",
          "Intake Consistency",
          "Exchange Speed",
          "Switch Speed",
          "Scale Speed",
          "Driver Skill"]


def get_team_data(manager):
    teams_data = {}
    for entry in manager.entries:
        if not entry.board.alliance() == "N":

            if entry.team not in teams_data.keys():
                teams_data[entry.team] = []

            ratings = ["Attachment speed",
                       "Climb speed",
                       "Intake speed",
                       "Intake consistency",
                       "Exchange speed",
                       "Switch speed",
                       "Scale speed",
                       "Driver skill"]

            teams_data[entry.team].append(tuple(entry.final_value(t, 0) for t in ratings))
    return teams_data


def avg(x):
    return (lambda s, l: s / l if s > 0 and l > 0 else np.nan)(sum(x), len(x))


def get_rows(manager):
    for team, ratings_list in get_team_data(manager).items():
        (attachment_speed,
         climb_speed,
         intake_speed,
         intake_consistency,
         exchange_speed,
         switch_speed,
         scale_speed,
         driver_skill) = zip(*ratings_list)

        yield {"Team": team,
               "Attachment Speed": avg(attachment_speed),
               "Climb Speed": avg(climb_speed),
               "Intake Speed": avg(intake_speed),
               "Intake Consistency": avg(intake_consistency),
               "Exchange Speed": avg(exchange_speed),
               "Switch Speed": avg(switch_speed),
               "Scale Speed": avg(scale_speed),
               "Driver Skill": avg(driver_skill)}


def compute_table(manager):
    return pd.DataFrame(get_rows(manager))[LABELS]
