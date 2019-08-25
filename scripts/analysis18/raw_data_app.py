import pandas as pd

TITLE_NAME = "Raw Data (App)"
SOURCE_NAME = "raw_data_app"
LABELS = ["Team Number",
          "Alliance",
          "Match Number",
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
          "Exchange Placement",
          "Switch Placement",
          "Scale Placement",
          "Intake Speed",
          "Intake Consistency",
          "Defense Time",
          "Levitate",
          "Force",
          "Boost",
          "Platform",
          "Climb",
          "Climb Speed",
          "Attachment Speed"]


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

                    if start:
                        start = False
                    else:
                        start = True
                if len(defense_pairs[-1]) == 1:
                    defense_pairs[-1].append(150)
                defence_values = []
                for i in defense_pairs:
                    defence_values.append(i[1] - i[0])
                defense_time = sum(defence_values)
            row_data = {
                "Team Number": entry.team,
                "Alliance": entry.board.alliance(),
                "Match Number": entry.match,

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

                "Exchange Placement": entry.final_value("Exchange speed", default=0),
                "Switch Placement": entry.final_value("Switch speed", default=0),
                "Scale Placement": entry.final_value("Scale speed", default=0),

                "Intake Speed": entry.final_value("Intake speed", default=0),
                "Intake Consistency": entry.final_value("Intake consistency", default=0),

                "Defense Time": defense_time,

                "Levitate": "",
                "Force": "",
                "Boost": "",

                "Platform": entry.final_value("Platform", default=0),
                "Climb": entry.final_value("Climb", default=0),
                "Climb Speed": entry.final_value("Climb speed", default=0) // 2,
                "Attachment Speed": entry.final_value("Attachment speed", default=0) // 2
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
    return pd.DataFrame(row_data_generator(manager))[LABELS]
