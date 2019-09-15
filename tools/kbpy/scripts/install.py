import sys


def info_alert(msg):
    try:
        from tkinter.messagebox import showinfo
        showinfo("Knotbook Installer", msg)
    except ModuleNotFoundError:
        print(msg)
        print("Press Enter to Continue: ", end="")
        input()


MIN_VERSION = (3, 7)

is_python37_or_above = sys.version_info >= MIN_VERSION
if not is_python37_or_above:
    info_alert("This script is only compatible with Python " + str(MIN_VERSION) +
               " and above, found version " + sys.version + " instead.")
    sys.exit(1)

is_64bit = sys.maxsize > 2 ** 32
if not is_64bit:
    info_alert("This script must run on a 64-bit architecture")
    sys.exit(1)

import json
import platform
import os

import pathlib

try:
    with open("install.json") as info_file:
        info = json.load(info_file)
except FileNotFoundError:
    info_alert("The install configuration file 'install.json' does not exist")
    sys.exit(1)
except ValueError as e:
    info_alert(str(e))
    sys.exit(1)

expected_system = info["platform"]
actual_system = platform.system()

if expected_system != actual_system:
    info_alert("Platform system" + actual_system +
               "does not match with distribution spec of " + expected_system)
    sys.exit(1)

install_file_name = info["install-file"]
install_path = pathlib.Path(install_file_name)

if not (install_path.exists() and install_path.is_file()):
    info_alert("The install data file " + install_file_name + " does not exist or is not a file")
    sys.exit(1)



import lzma
import zlib
import zipfile

import urllib

import argparse
import curses
import time
import pprint

import contextlib
import typing

import venv
import pip

import tempfile
import shutil

import subprocess
import json

import hashlib

# todo
#   check if script is in virtualenv


is_windows = system == "Windows"
is_mac = system == "Darwin"
is_linux = system == "Linux"


class Install:
    pass


welcome = "====Knotbook Installer===="

if __name__ == '__main__':
    print(welcome)
    install = Install()
