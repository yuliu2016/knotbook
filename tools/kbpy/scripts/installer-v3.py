"""
KnotBook Installer Script Version 3 for Python 3.7+

v3.0:
  - Using 2 repositories for separate part of the app
  - Auto-install and auto-update functions
  - Windows only for now
  - Removed checks for virtualenv
  - Program jars are now in libs/ folder

v3.1:
  - Organize code into functions

v3.2:
  - Change version store format from pickle to JSON
  - More helpful prints
  - Version store now excludes build number
  - Remove parse_build_id

v3.3:
  - Remove shebang so that script is launchable
  - Improve version parsing
  - Improve extraction message
  - Fix image updating not triggering build update

v3.4:
  - Change the default installation folder
  - Renamed some variables
"""

from urllib.request import urlopen, urlretrieve
from urllib.parse import urlparse
from typing import Iterable
from traceback import print_exc
import json
import sys
import platform
import tarfile
import zipfile
import os
import shutil
import pathlib

def get_version_data():
    try:
        with open("KnotBook/app/version-info.json", "r") as f:
            data =  json.load(f)
            print("Found a previous installation.")
            return data
    except FileNotFoundError:
        print("Starting a fresh install.")
        return {}

def build_number_to_version(build_number: str):
    try:
        a, b = build_number.split(".")
        year = int(a[0:4])
        month = int(a[4:6])
        day = int(a[6:8])
        if year == 2019:
            minor = month - 8
        else:
            minor = 4 + month + (year - 2020) * 12
        return f"3.{minor}.{day}+{b}"
    except Exception:
        return "None"

def exit_error():
    input("Press Enter to Exit")
    sys.exit(1)

# See https://stackoverflow.com/questions/3667865/python-tarfile-progress-output
def track_progress(members: Iterable[tarfile.TarInfo]):
    for member in members:
        print(f"Extract ({member.size // 1000:>6} K) {member.name[8:]}")
        yield member

def api_base(repo: str):
    return f"https://dev.azure.com/yuliu2016/{repo}/_apis/build"

def get_build_id(repo: str, check_success=True):
    build_url = f"{api_base(repo)}/builds?branchName=refs/heads/master&$top=1&api-version=5.1"
    try:
        build_data = json.loads(urlopen(build_url).read())["value"][0]
        build_id = build_data["id"]
        if check_success and build_data["result"] != "succeeded":
            print("The latest build on master has failed. Retry when it's fixed")
            exit_error()
        build_number = build_data["buildNumber"]
    except Exception:
        print_exc()
        print(f"Failed to get latest build ID for {repo}")
        exit_error()
    return build_id, build_number

def get_artifact_url(repo: str, target:str, build_id):
    artifact_url = f"{api_base(repo)}/builds/{build_id}/artifacts?&api-version=5.1"
    try:
        artifact_data =  json.loads(urlopen(artifact_url).read())
        target_artifact_data = None
        for single_artifact_data in artifact_data["value"]:
            if single_artifact_data["name"].startswith(target):
                target_artifact_data = single_artifact_data["resource"]
        if not target_artifact_data:
            print("No supported artifact found")
            exit_error()
        download_url = target_artifact_data["downloadUrl"]
        download_size = int(target_artifact_data["properties"]["artifactsize"]) / 1000000
        print("Found download url!!")
        return download_url, download_size
    except Exception:
        print_exc()
        print("Failed to get artifact url")
        exit_error()

def download_into(name, download_url, download_size):
    if not os.path.exists(name):
        try:
            u = urlopen(download_url)
            block_size = 2 ** 18  # 4 blocks per Mb
            with open(name, 'wb') as f:
                file_size_downloaded = 0
                while True:
                    buffer = u.read(block_size)
                    if not buffer:
                        break
                    file_size_downloaded += len(buffer)
                    f.write(buffer)

                    mb = file_size_downloaded / 1000000
                    print(f"{name}: Downloaded {mb:.2f}MB of {download_size:.2f}MB total")
        except Exception:
            print_exc()
            print(f"Failed to download {name}")
            exit_error()
    else:
        print(f"{name} already downloaded. Continuing to next step.")

def download_image(target: str, version_data):
    build_id, build_number = get_build_id(repo="knotbook-image", check_success=False)
    if version_data["image"] == build_number:
        print(f"Image {build_number} is up-to-date. Continuing to next step.")
        return
    print(f"Updating Image from {version_data['image']} to {build_number}")
    version_data["image"] = build_number
    version_data["build"] = "None"
    download_url, download_size = get_artifact_url(repo="knotbook-image", target=target, build_id=build_id)
    download_into("image.zip", download_url, download_size)
    try:
        with zipfile.ZipFile("image.zip", "r") as z:
            z.extractall()
    except Exception:
        print_exc()
        print_exc("Failed to extract target")
        exit_error()
    print("Extracting Image")
    if os.path.isdir("KnotBook/"):
        shutil.rmtree("KnotBook/")
    try:
        with tarfile.open(f"{target}-image/{target}-{build_number}.tar.xz", "r:xz") as z:
            z.extractall(members=track_progress(z))
    except Exception:
        print_exc()
        print_exc("Failed to extract target")
        exit_error()
    os.remove("image.zip")
    shutil.rmtree(f"{target}-image/")
    print("Done Extracting Image")


def download_build(target: str, version_data):
    build_id, build_number = get_build_id(repo="knotbook", check_success=True)
    build_version = build_number_to_version(build_number)
    if version_data["build"] == build_number:
        print(f"Build {build_version} is up-to-date. Continuing to next step.")
        return
    old_version = build_number_to_version(version_data["build"])
    print(f"Updating Build from {old_version} to {build_version}")
    version_data["build"] = build_number
    download_url, download_size = get_artifact_url(repo="knotbook", target=target, build_id=build_id)
    download_into("build.zip", download_url, download_size)
    try:
        with zipfile.ZipFile("build.zip", "r") as z:
            z.extractall()
    except Exception:
        print_exc()
        print_exc("Failed to extract target")
        exit_error()
    if os.path.isdir("KnotBook/app/libs"):
        shutil.rmtree("KnotBook/app/libs")
    shutil.copytree(f"{target}-{build_number}/", "KnotBook/app/libs/")
    os.remove("build.zip")
    shutil.rmtree(f"{target}-{build_number}/")


def install_knotbook():
    system = platform.system()
    if system == "Windows":
        target = "windows"
    elif system == "Linux":
        target = "ubuntu"
    elif system == "Darwin":
        target = "macOS"
    else:
        print(f"The system {system} is unsupported")
        exit_error()
    home = str(pathlib.Path.home())
    sys_path = os.path.join(home, ".knotbook")
    if not os.path.isdir(sys_path):
        os.makedirs(sys_path)
    os.chdir(sys_path)
    version_data = get_version_data()
    if not "image" in version_data:
        version_data["image"] = "None"
    if not "build" in version_data:
        version_data["build"] = "None"
    download_image(target, version_data)
    download_build(target, version_data)
    with open("KnotBook/app/version-info.json", "w") as f:
        json.dump(version_data, f)
    print("Done")


if __name__ == "__main__":
    install_knotbook()