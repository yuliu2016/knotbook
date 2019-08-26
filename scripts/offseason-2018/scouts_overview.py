import pandas as pd

TITLE_NAME = "Scouts Overview"
SOURCE_NAME = "scouts_overview"
LABELS = ["Scout Name", "# Scouted"]


def compile_scouts(manager):
    scouts = {}
    for entry in manager.entries:
        s = entry.name.strip().lower().capitalize()
        if s not in scouts.keys():
            scouts[s] = 0
        scouts[s] += 1

    return scouts


def get_rows(manager):
    for name, count in compile_scouts(manager).items():
        yield {
            "Scout Name": name,
            "# Scouted": count
        }


def compute_table(manager):
    return pd.DataFrame(get_rows(manager), columns=LABELS)[LABELS]
