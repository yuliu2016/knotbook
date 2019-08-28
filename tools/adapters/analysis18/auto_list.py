import pandas as pd

TITLE_NAME = "Auto List"  # The name to display
SOURCE_NAME = "auto_list"  # The name to be accessed by other code
LABELS = ["Team",
          "Match",
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
          ]  # Column labels for table, and row labels for lookup (later thing)


def get_rows(manager):
    auto_data_points = ["Auto scale", "Auto switch", "Auto scale attempt", "Auto switch attempt"]
    for entry in manager.entries:
        if not entry.board.alliance() == "N":
            times = {}
            for i in auto_data_points:
                times[i] = []

            actions = []
            for data_point in auto_data_points:
                for occurrence_time in entry.look(data_point):
                    times[data_point].append(occurrence_time)
                    actions.append((occurrence_time, data_point))

            if not actions:
                continue

            actions = sorted(actions, key=lambda x: (x[0]))

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
            row_data = {
                "Team": entry.team,
                "Match": entry.match,
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
    table = pd.DataFrame(get_rows(manager))[LABELS]
    return table
