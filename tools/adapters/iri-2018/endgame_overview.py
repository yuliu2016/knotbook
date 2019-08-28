import pandas as pd

TITLE_NAME = "Endgame Overview"
SOURCE_NAME = "endgame_overview"
LABELS = ["Team",
          "Match",
          "Climbed",
          "Platform",
          "Total climb length",
          "Climb length",
          "Climb time",
          "Endgame Type",
          "Game Objective"
          ]


def get_rows(manager):
    for entry in manager.entries:
        if not entry.board.alliance() == "N":
            platform_times = entry.look("Platform timer")
            climbed_times = entry.look("Climbed timer")

            platform_starts = []
            platform_ends = []
            for i in range(len(platform_times)):
                if i % 2 == 1:
                    platform_starts.append(climbed_times[i - 1])
                    platform_ends.append(climbed_times[i])

            climbed_starts = []
            climbed_ends = []
            for i in range(len(climbed_times)):
                if i % 2 == 1:
                    climbed_starts.append(climbed_times[i - 1])
                    climbed_ends.append(climbed_times[i])

            climbed = False
            if len(climbed_times) % 2 == 1:
                climbed = True

            platform = False
            if len(climbed_times) % 2 == 1:
                platform = True

            endgame_type = [
                "Platform",
                "Failed Climb",
                "Single Climb",
                "Double Climb",
                "Single Climb, Lifting Another Robot",
                "Lifted by Another Robot"][entry.final_value("Endgame type")]

            game_objective = [
                "Select",
                "Scale",
                "Alliance Switch",
                "Opponent Switch",
                "Exchange",
                "Defense",
                "Support"][entry.final_value("Objective")]

            yield {
                "Team": entry.team,
                "Match": entry.match,
                "Climbed": climbed,
                "Platform": platform,
                "Total climb length": max(platform_starts) - max(platform_starts),
                "Climb length": max(climbed_starts) - max(platform_ends),
                "Climb time": max(climbed_starts),
                "Endgame Type": endgame_type,
                "Game Objective": game_objective
            }


def compute_table(manager):
    table = pd.DataFrame(get_rows(manager), columns=LABELS)[LABELS]
    return table
