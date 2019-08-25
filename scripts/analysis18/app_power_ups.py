import pandas as pd

TITLE_NAME = "App Power Ups"
SOURCE_NAME = "app_power_ups"
LABELS = ["Match",
          "Red force level",
          "Red force activate",
          "Red levitate level",
          "Red levitate activate",
          "Red boost level",
          "Red boost activate",
          "Blue force level",
          "Blue force activate",
          "Blue levitate level",
          "Blue levitate activate",
          "Blue boost level",
          "Blue boost activate"]


def get_activated(arr):
    if arr:
        return "{} seconds".format(arr[0])
    return ""


def get_rows(manager):
    for entry in manager.entries:
        if entry.board.alliance() == "N":
            yield {
                "Match": entry.match,
                "Red force level": entry.count("Red force place"),
                "Red force activate": get_activated(entry.look("Red force")),
                "Red levitate level": entry.count("Red levitate place"),
                "Red levitate activate": get_activated(entry.look("Red levitate")),
                "Red boost level": entry.count("Red boost place"),
                "Red boost activate": get_activated(entry.look("Red boost")),
                "Blue force level": entry.count("Blue force place"),
                "Blue force activate": get_activated(entry.look("Blue force")),
                "Blue levitate level": entry.count("Blue levitate place"),
                "Blue levitate activate": get_activated(entry.look("Blue levitate")),
                "Blue boost level": entry.count("Blue boost place"),
                "Blue boost activate": get_activated(entry.look("Blue boost"))
            }


def compute_table(manager):
    return pd.DataFrame(get_rows(manager))[LABELS]
