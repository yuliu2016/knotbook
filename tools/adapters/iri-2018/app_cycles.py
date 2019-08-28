import numpy as np
import pandas as pd

TITLE_NAME = "App Cycles"
SOURCE_NAME = "app_cycles"
LABELS = ["Team",
          "Match",
          "Alliance",
          "Average Scale",
          "Average Alliance Switch",
          "Average Opponent Switch",
          "Average Exchange",
          "Average Intake",
          "Average Outtake",
          "Std Scale",
          "Std Alliance Switch",
          "Std Opponent Switch",
          "Std Exchange",
          "Std Intake",
          "Std Outtake"
          ]


def n_avg(arr):
    if len(arr) > 0:
        return sum(arr) / len(arr)
    return np.nan


def n_std(arr):
    if len(arr) > 1:
        return np.std(arr, ddof=1)
    return np.nan


def get_rows(manager):
    for entry in manager.entries:
        if not entry.board.alliance() == "N":

            tracked_data_types = ['Tele scale',
                                  'Tele exchange',
                                  'Tele opponent switch',
                                  'Tele intake',
                                  'Tele alliance switch']

            time_series = [None for _ in range(150)]
            for data_type in tracked_data_types:
                for occurrence_time in entry.look(data_type):
                    time_series[occurrence_time - 1] = data_type

            first_intake_ignored = False
            robot_doing_outtake = True
            current_cycle_time = 1

            scale = []
            exchange = []
            alliance_switch = []
            opponent_switch = []
            intake = []
            outtake = []

            for data_at_second in time_series:

                if first_intake_ignored:

                    if data_at_second == "Tele intake" and robot_doing_outtake:
                        intake.append(current_cycle_time)
                        current_cycle_time = 1
                        robot_doing_outtake = False

                    elif data_at_second == 'Tele scale' and not robot_doing_outtake:
                        scale.append(current_cycle_time)
                        outtake.append(current_cycle_time)
                        current_cycle_time = 1
                        robot_doing_outtake = True

                    elif data_at_second == 'Tele exchange' and not robot_doing_outtake:
                        exchange.append(current_cycle_time)
                        outtake.append(current_cycle_time)
                        current_cycle_time = 1
                        robot_doing_outtake = True

                    elif data_at_second == 'Tele opponent switch' and not robot_doing_outtake:
                        opponent_switch.append(current_cycle_time)
                        outtake.append(current_cycle_time)
                        current_cycle_time = 1
                        robot_doing_outtake = True

                    elif data_at_second == 'Tele alliance switch' and not robot_doing_outtake:
                        alliance_switch.append(current_cycle_time)
                        outtake.append(current_cycle_time)
                        current_cycle_time = 1
                        robot_doing_outtake = True

                    else:
                        current_cycle_time += 1

                if data_at_second and data_at_second != "Tele intake" and not first_intake_ignored:
                    first_intake_ignored = True

            yield {
                "Team": entry.team,
                "Match": entry.match,
                "Alliance": entry.board.alliance(),
                "Average Scale": n_avg(scale),
                "Average Alliance Switch": n_avg(alliance_switch),
                "Average Opponent Switch": n_avg(opponent_switch),
                "Average Exchange": n_avg(exchange),
                "Average Intake": n_avg(intake),
                "Average Outtake": n_avg(outtake),
                "Std Scale": n_std(scale),
                "Std Alliance Switch": n_std(alliance_switch),
                "Std Opponent Switch": n_std(opponent_switch),
                "Std Exchange": n_std(exchange),
                "Std Intake": n_std(intake),
                "Std Outtake": n_std(outtake)
            }


def compute_table(manager):
    table = pd.DataFrame(get_rows(manager), columns=LABELS)[LABELS]
    return table
