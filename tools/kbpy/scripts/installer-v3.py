#! usr/bin/env python3

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
import pickle


def parse_build_id(build_id: str):
    a, b = build_id.split(".")
    return a, b
    
def get_version_data():
    try:
        with open("KnotBook/app/version-info", "rb") as f:
            return pickle.load(f)
    except FileNotFoundError:
        return {}

# See https://stackoverflow.com/questions/3667865/python-tarfile-progress-output
def track_progress(members: Iterable[tarfile.TarInfo]):
    for member in members:
        yield member
        print(f"Extracted (size:{member.size // 1000:>6} KB) {member.name}")

def api_base(repo: str):
    return f"https://dev.azure.com/yuliu2016/{repo}/_apis/build"

def get_build_id(repo: str, check_success=True):
    build_url = f"{api_base(repo)}/builds?branchName=refs/heads/master&$top=1&api-version=5.1"

    try:
        build_data = json.loads(urlopen(build_url).read())["value"][0]
        build_id = build_data["id"]
        if check_success and build_data["result"] != "succeeded":
            print("The latest build on master has failed. Retry when it's fixed")
            sys.exit(1)
        build_number = build_data["buildNumber"]
    except Exception:
        print_exc()
        print("Failed to get latest build ID")
        sys.exit(1)
    
    print(f"Found latest build ID from {repo}: {build_number}+{build_id}")
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
            sys.exit(1)
        download_url = target_artifact_data["downloadUrl"]
        download_size = int(target_artifact_data["properties"]["artifactsize"]) / 1000000
        print("Found download url!!")
        return download_url, download_size
    except Exception:
        print_exc()
        print("Failed to get artifact url")
        sys.exit(1)

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
            sys.exit(1)
    else:
        print(f"{name} already downloaded. Continuing to next step.")

def download_image(target: str, version_data):
    build_id, build_number = get_build_id(repo="knotbook-image", check_success=False)
    image_version = f"{build_number}+{build_id}"
    if version_data["image"] == image_version:
        print(f"Image {image_version} is up-to-date. Continuing to next step.")
        return
    print(f"Updating Image from {version_data['image']} to {image_version}")
    download_url, download_size = get_artifact_url(repo="knotbook-image", target=target, build_id=build_id)
    version_data["image"] = image_version
    download_into("image.zip", download_url, download_size)
    
    try:
        with zipfile.ZipFile("image.zip", "r") as z:
            z.extractall()
    except Exception:
        print_exc()
        print_exc("Failed to extract target")
        sys.exit(1)

    print("Extracting Image")

    try:
        with tarfile.open(f"{target}-image/{target}-{build_number}.tar.xz", "r:xz") as z:
            z.extractall(members=track_progress(z))
    except Exception:
        print_exc()
        print_exc("Failed to extract target")
        sys.exit(1)

    os.remove("image.zip")
    shutil.rmtree(f"{target}-image/")
    
    print("Done Extracting Image")


def download_build(target: str, version_data):
    build_id, build_number = get_build_id(repo="knotbook", check_success=True)
    build_version = f"{build_number}+{build_id}"
    if version_data["build"] == build_version:
        print(f"Build {build_version} is up-to-date. Continuing to next step.")
        return
    print(f"Updating Build from {version_data['build']} to {build_version}")
    version_data["build"] = build_version
    download_url, download_size = get_artifact_url(repo="knotbook", target=target, build_id=build_id)
    download_into("build.zip", download_url, download_size)

    try:
        with zipfile.ZipFile("build.zip", "r") as z:
            z.extractall()
    except Exception:
        print_exc()
        print_exc("Failed to extract target")
        sys.exit(1)

    if os.path.isdir("KnotBook/app/libs"):
        shutil.rmtree("KnotBook/app/libs")
    shutil.copytree(f"{target}-{build_number}/", "KnotBook/app/libs/")

    os.remove("build.zip")
    shutil.rmtree(f"{target}-{build_number}/")


def main():

    system = platform.system()

    if system == "Windows":
        artifact_target = "windows"
    elif system == "Linux":
        artifact_target = "ubuntu"
    elif system == "Darwin":
        artifact_target = "macOS"
    else:
        print(f"The system {system} is unsupported")
        sys.exit(1)

    version_data = get_version_data()
    if not "image" in version_data:
        version_data["image"] = "None"
    if not "build" in version_data:
        version_data["build"] = "+0"

    download_image(artifact_target, version_data)
    download_build(artifact_target, version_data)

    with open("KnotBook/app/version-info", "wb") as f:
        pickle.dump(version_data, f)

    print("Done")


if __name__ == '__main__':
    main()