import pandas as pd

TITLE_NAME = "Wrong data"
SOURCE_NAME = "wrong_data"
LABELS = ["Scout",
          "Team",
          "Match",
          "Alliance",
          "Double outtakes",
          "Wrong auto line",
          "Wrong climb"]


def get_rows(manager):
    tracked_data_types = ['Tele intake',
                          'Tele scale',
                          'Tele exchange',
                          'Tele opponent switch',
                          'Tele alliance switch']

    outtakes = ['Tele scale',
                'Tele exchange',
                'Tele opponent switch',
                'Tele alliance switch']
    if manager.tba_available:
        matches = manager.tba.event_matches(manager.tba_event)
    else:
        matches = None

    for entry in manager.entries:
        if not entry.board.alliance() == "N":

            time_series = [None for _ in range(150)]
            for data_type in tracked_data_types:
                for occurrence_time in entry.look(data_type):
                    time_series[occurrence_time - 1] = data_type

            has_cube = False
            first_outtake_ignored = False
            double_outtakes = 0

            for event in time_series:
                if not first_outtake_ignored:
                    if event in outtakes:
                        first_outtake_ignored = True
                else:
                    if event in outtakes:
                        if not has_cube:
                            double_outtakes += 1
                        has_cube = False
                    if event == "Tele intake":
                        has_cube = True

            if matches is not None:
                match_key = str(manager.tba_event) + "_qm" + str(entry.match)

                if entry.board.alliance().lower() == "r":
                    alliance = "red"
                elif entry.board.alliance().lower() == "b":
                    alliance = "blue"
                else:
                    alliance = "unknown"

                tba_match = None
                for match in matches:
                    if match['key'] == match_key:
                        tba_match = match

                alliance_teams = tba_match['alliances'][alliance]["team_keys"]
                if "frc" + str(entry.team) in alliance_teams:
                    tba_robot_number = alliance_teams.index("frc" + str(entry.team)) + 1
                else:
                    continue

                tba_climbed = tba_match['score_breakdown'][alliance][
                                  "endgameRobot" + str(tba_robot_number)] == "Climbing"
                tba_auto_line = tba_match['score_breakdown'][alliance][
                                    "autoRobot" + str(tba_robot_number)] == "AutoRun"

                climbed_times = entry.look("Climbed timer")
                climbed = False
                if len(climbed_times) % 2 == 1:
                    climbed = True

                yield {"Scout": entry.name,
                       "Team": entry.team,
                       "Match": entry.match,
                       "Alliance": entry.board.alliance(),
                       "Double outtakes": double_outtakes,
                       "Wrong auto line": not (climbed == tba_auto_line),
                       "Wrong climb": not climbed == tba_climbed
                       }
            else:
                yield {"Scout": entry.name,
                       "Team": entry.team,
                       "Match": entry.match,
                       "Alliance": entry.board.alliance(),
                       "Double outtakes": double_outtakes,
                       "Wrong auto line": "",
                       "Wrong climb": ""}


def compute_table(manager):
    table = pd.DataFrame(get_rows(manager), columns=LABELS)[LABELS]
    return table
