import requests
import pandas as pd
import contextlib
from io import StringIO
import sys

class ScriptContext:

    def __init__(self, host, port, redirect_out):
        self._host = host
        self._port = port
        self._redirect_out = redirect_out
        self._out = StringIO()
        if redirect_print:
            self._stdout = sys.stdout
            sys.stdout = self._out
        else:
            self._stdout = None

    def get_table(self, table_name: "str", default_file=None) -> "pd.DataFrame":
        r = requests.get(f"http://{self._host}:{self._port}/get/{table_name}", timeout=5)
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

    def log(self, *obj):
        print(*obj, file=self._out)


def script_context(host="localhost", port="8080", redirect_print=True):
    return ScriptContext(host, port, redirect_print)
