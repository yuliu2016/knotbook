import requests
import pandas as pd
import contextlib
from io import StringIO
import sys

class ScriptContext:
    """
    Create a scripting context
    """

    def __init__(self, host="localhost", port="8080"):
        self._host = host
        self._port = port
        self._job = None

    def _get(self, path: "str"):
        return requests.get(f"http://{self._host}:{self._port}/get/{path}", timeout=5)

    def get_table(self, table_name: "str", default_file=None) -> "pd.DataFrame":
        r = _get(f"table/{table_name}")
        t = r.text
        if t is None or len(t) == 0:
            return pd.read_csv(default_file)
        return pd.read_csv(r.text)

    def set_table(self, table_name: "str", data: "pd.DataFrame"):
        requests.post(f"http://{self._host}:{self._port}/post/{table_name}", data=data.to_csv())

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        if self._redirect_out:
            sys.stdout = self._stdout
        requests.post(f"http://{self._host}:{self._port}/done", data=self._out.getvalue())

script_context = ScriptContext
