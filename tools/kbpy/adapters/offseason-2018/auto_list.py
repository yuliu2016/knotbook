import pandas as pd

TITLE_NAME = "Auto List"
SOURCE_NAME = "auto_list"
LABELS = ["Team",
          "Match",
          "Starting position",
          "Plate Assignments",
          "Total Success",
          "Total Attempt and Success",
          "Scale Success",
          "Switch Success",
          "First Time",
          "Last Time",
          "Action 1",
          "Action 2",
          "Action 3",
          "Action 4",
          "Action 5"
          ]


def get_rows(manager):
    auto_data_points = ["Auto scale", "Auto switch", "Auto scale attempt", "Auto switch attempt"]
    for entry in manager.entries:
        if not entry.board.alliance() == "N":

            times = {i: [] for i in auto_data_points}

            actions = []
            for data_point in auto_data_points:
                for occurrence_time in entry.look(data_point):
                    times[data_point].append(occurrence_time)
                    actions.append((occurrence_time, data_point))

            if not actions:
                continue

            actions = sorted(actions, key=lambda x: x[0])  # sort by the first item in tuple

            num_actions = len(actions)
            action_list = []
            for i in range(5):
                if i < num_actions:
                    action_list.append(actions[i][1])
                else:
                    action_list.append("None")

            switch_auto_successes = entry.count("Auto switch")
            scale_auto_successes = entry.count("Auto scale")
            switch_auto_attempts = entry.count("Auto switch attempt")
            scale_auto_attempts = entry.count("Auto scale attempt")

            starting_pos = entry.final_value("Starting position", default=0)
            starting_pos_str = ["None", "Left", "Center", "Right"][starting_pos]

            if manager.tba_available:
                plate_assignments = manager.tba.match(key='2018dar_qm49')['score_breakdown']['red']['tba_gameData']
                if entry.board.alliance() == "R":
                    scale_assignment = plate_assignments[1]
                    switch_assignment = plate_assignments[0]
                else:
                    for i, v in enumerate(plate_assignments):
                        if v == "R":
                            plate_assignments[i] = "L"
                        elif v == "L":
                            plate_assignments[i] = "R"

                    plate_assignments = plate_assignments
                    scale_assignment = plate_assignments[1]
                    switch_assignment = plate_assignments[0]

                row_data = {
                    "Team": entry.team,
                    "Match": entry.match,
                    "Starting position": starting_pos_str,
                    "Scale assignment": scale_assignment,
                    "Switch assignment": switch_assignment,
                    "Total Success": switch_auto_successes + scale_auto_successes,
                    "Total Attempt and Success": (switch_auto_successes + switch_auto_attempts +
                                                  scale_auto_successes + scale_auto_attempts),
                    "Scale Success": scale_auto_successes,
                    "Switch Success": switch_auto_successes,
                    "First Time": actions[0][0] if num_actions > 0 else 0,
                    "Last Time": actions[-1][0] if num_actions > 0 else 0,
                    "Action 1": action_list[0],
                    "Action 2": action_list[1],
                    "Action 3": action_list[2],
                    "Action 4": action_list[3],
                    "Action 5": action_list[4]
                }
            else:
                row_data = {
                    "Team": entry.team,
                    "Match": entry.match,
                    "Starting position": starting_pos_str,
                    "Plate Assignments": "",
                    "Total Success": switch_auto_successes + scale_auto_successes,
                    "Total Attempt and Success": (switch_auto_successes + switch_auto_attempts +
                                                  scale_auto_successes + scale_auto_attempts),
                    "Scale Success": scale_auto_successes,
                    "Switch Success": switch_auto_successes,
                    "First Time": actions[0][0] if num_actions > 0 else 0,
                    "Last Time": actions[-1][0] if num_actions > 0 else 0,
                    "Action 1": action_list[0],
                    "Action 2": action_list[1],
                    "Action 3": action_list[2],
                    "Action 4": action_list[3],
                    "Action 5": action_list[4]
                }
            yield row_data


def compute_table(manager):
    table = pd.DataFrame(get_rows(manager), columns=LABELS)[LABELS]
    return table
