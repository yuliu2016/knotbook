import pandas as pd

TITLE_NAME = "Tba powerups"
SOURCE_NAME = "tba_powerups"
LABELS = ["Match",
          "Red force played", "Red force total",
          "Red levitate played", "Red levitate total",
          "Red boost played", "Red boost total",
          "Blue force played", "Blue force total",
          "Blue levitate played", "Blue levitate total",
          "Blue boost played", "Blue boost total"]


def row_data_generator(manager):
    tba = manager.tba
    event = tba.event_matches(manager.tba_event)
    for match in event:
        if match['score_breakdown'] is not None and match['comp_level'] == 'qm':
            row_data = {'Match': int(match['match_number'])}
            for alliance in ['red', 'blue']:
                for powerup in ['force', 'levitate', 'boost']:
                    row_data[alliance.capitalize() + " " + powerup + " played"] = match['score_breakdown'][alliance][
                        'vault' + powerup.capitalize() + 'Played']
                    row_data[alliance.capitalize() + " " + powerup + " total"] = match['score_breakdown'][alliance][
                        'vault' + powerup.capitalize() + 'Total']
            yield row_data

    yield {"Match": '',
           "Red force played": '',
           "Red force total": '',
           "Red levitate played": '',
           "Red levitate total": '',
           "Red boost played": '',
           "Red boost total": '',
           "Blue force played": '',
           "Blue force total": '',
           "Blue levitate played": '',
           "Blue levitate total": '',
           "Blue boost played": '',
           "Blue boost total": ''}


def compute_table(manager):
    if manager.tba_available:
        return pd.DataFrame(row_data_generator(manager))[LABELS].sort_values(by=["Match"]).reset_index(drop=True)
    return pd.DataFrame(columns=LABELS)
