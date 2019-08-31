import pandas as pd

TITLE_NAME = "Raw Data (App)"
SOURCE_NAME = "raw_data_app"
LABELS = ["Team Number",
          "Alliance",
          "Match Number",
          "Start Position",
          "Auto Line",
          "Exchange Auto Successes",
          "Switch Auto Successes",
          "Scale Auto Successes",
          "Exchange Auto Attempts",
          "Switch Auto Attempts",
          "Scale Auto Attempts",
          "Exchange",
          "Alliance Switch",
          "Opponent Switch",
          "Scale",
          "Times Cube Dropped",
          "Defense Time",
          "Endgame Type",
          "Total Platform Duration",
          "Last Platform Duration",
          "Platform Attempts",
          "Total Climbed Duration",
          "Last Climbed Duration",
          "Climb Attempts",
          "Relative Climb Time",
          "Objective",
          "Comments"
          ]

START_POSITIONS = [
    "None",
    "Left",
    "Center",
    "Right"
]

ENDGAME_TYPES = [
    "None",
    "Platform",
    "Failed Climb",
    "Single Climb",
    "Double Climb",
    "Single Climb, Lifting Another Robot",
    "Lifted by Another Robot"
]

OBJECTIVES = [
    "Select",
    "Scale",
    "Alliance Switch",
    "Opponent Switch",
    "Exchange",
    "Defense",
    "Support"
]


def row_data_generator(manager):
    for entry in manager.entries:
        if entry.board.alliance() != "N":
            defense_presses = entry.look("Defense")
            if len(defense_presses) == 0:
                defense_time = 0
            else:
                defense_pairs = []
                start = True
                for index, value in enumerate(defense_presses):
                    if start:
                        defense_pairs.append([value])
                    else:
                        defense_pairs[int((index - 1) / 2)].append(value)
                    start = not start
                if defense_pairs:
                    if len(defense_pairs[-1]) == 1:
                        defense_pairs[-1].append(150)
                    defence_values = []
                    for i in defense_pairs:
                        defence_values.append(i[1] - i[0])
                    defense_time = sum(defence_values)
                else:
                    defense_time = 0

            platform_presses = entry.look("Platform timer")
            platform_pairs = []
            start = True

            for index, value in enumerate(platform_presses):
                if start:
                    platform_pairs.append([value])
                else:
                    platform_pairs[int((index - 1) / 2)].append(value)
                start = not start

            platform_attempts = len(platform_pairs)

            if platform_pairs and len(platform_pairs[-1]) == 1:
                platform_pairs[-1].append(150)
                last_platform_duration = 150 - platform_pairs[-1][0]
                platform_attempts -= 1
            else:
                last_platform_duration = 0

            total_platform_duration = sum(map(lambda x: x[1] - x[0], platform_pairs))

            climbed_presses = entry.look("Climbed timer")
            climbed_pairs = []
            start = True

            for index, value in enumerate(climbed_presses):
                if start:
                    climbed_pairs.append([value])
                else:
                    climbed_pairs[int((index - 1) / 2)].append(value)
                start = not start

            climb_attempts = len(climbed_pairs)

            if climbed_pairs and len(climbed_pairs[-1]) == 1:
                climbed_pairs[-1].append(150)
                last_climbed_duration = 150 - climbed_pairs[-1][0]
                climb_attempts -= 1
            else:
                last_climbed_duration = 0  # No climb

            total_climbed_duration = sum(map(lambda x: x[1] - x[0], climbed_pairs))

            row_data = {
                "Team Number": entry.team,
                "Alliance": entry.board.alliance(),
                "Match Number": entry.match,

                "Start Position": START_POSITIONS[entry.final_value("Start position", 0)],
                "Auto Line": entry.final_value("Auto line", default=0),

                "Exchange Auto Successes": entry.count("Auto exchange"),
                "Switch Auto Successes": entry.count("Auto switch"),
                "Scale Auto Successes": entry.count("Auto scale"),

                "Exchange Auto Attempts": entry.count("Auto exchange attempt"),
                "Switch Auto Attempts": entry.count("Auto switch attempt"),
                "Scale Auto Attempts": entry.count("Auto scale attempt"),

                "Exchange": entry.count("Tele exchange"),
                "Alliance Switch": entry.count("Tele alliance switch"),
                "Opponent Switch": entry.count("Tele opponent switch"),
                "Scale": entry.count("Tele scale"),

                "Times Cube Dropped": (entry.count("Tele intake") -
                                       entry.count("Tele exchange") -
                                       entry.count("Tele alliance switch") -
                                       entry.count("Tele opponent switch") -
                                       entry.count("Tele scale")),

                "Defense Time": defense_time,
                "Endgame Type": ENDGAME_TYPES[entry.final_value("Endgame type", 0)],

                "Total Platform Duration": total_platform_duration,
                "Last Platform Duration": last_platform_duration,
                "Platform Attempts": platform_attempts,

                "Total Climbed Duration": total_climbed_duration,
                "Last Climbed Duration": last_climbed_duration,
                "Climb Attempts": climb_attempts,

                "Relative Climb Time": last_platform_duration - last_climbed_duration,

                "Objective": OBJECTIVES[entry.final_value("Objective", 0)],
                "Comments": entry.comments
            }

            # Fix times cube dropped

            total_auto = ("Exchange Auto Successes",
                          "Switch Auto Successes",
                          "Scale Auto Successes",
                          "Exchange Auto Attempts",
                          "Switch Auto Attempts",
                          "Scale Auto Attempts",)

            if sum(row_data[auto_data] for auto_data in total_auto) > 1:
                row_data["Times Cube Dropped"] += 1

            yield row_data


def compute_table(manager):
    return pd.DataFrame(row_data_generator(manager), columns=LABELS)[LABELS]
