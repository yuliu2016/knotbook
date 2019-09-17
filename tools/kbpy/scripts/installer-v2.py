from urllib.request import urlopen
from typing import Iterable
import json
import sys
import platform
import tarfile
import zipfile
import os
import venv
import time

MIN_VERSION = (3, 7)

is_python37_or_above = sys.version_info >= MIN_VERSION
if not is_python37_or_above:
    print("This script is only compatible with Python " + str(MIN_VERSION) +
          " and above, found version " + sys.version + " instead.")
    input()
    sys.exit(1)

is_64bit = sys.maxsize > 2 ** 32
if not is_64bit:
    print("This script must run on a 64-bit architecture")
    input()
    sys.exit(1)


def make_venv():
    venv.create(env_dir="venv/", with_pip=True, clear=True)


def download_files(target: "str"):
    api = "https://dev.azure.com/yuliu2016/knotbook/_apis/build"
    build_url = f"{api}/builds?branchName=refs/heads/master&$top=1&api-version=5.1"

    try:
        build_id = json.loads(urlopen(build_url).read())["value"][0]["id"]
    except:
        print("Failed to get latest build")
        input()
        sys.exit(1)

    print(f"Using latest build ID: #{build_id}")
    artifact_url = f"{api}/builds/{build_id}/artifacts?&api-version=5.1&artifactName={target}"

    try:
        download_url = json.loads(urlopen(artifact_url).read())["resource"]["downloadUrl"]
    except Exception:
        import traceback

        traceback.print_exc()
        print("Failed to get artifact url")
        input()
        sys.exit()

    print("Found download url!!")

    try:
        u = urlopen(download_url)
        block_size = 2 ** 20  # 1 block per Mb
        with open("tempfile", 'wb') as f:
            file_size_downloaded = 0
            while True:
                buffer = u.read(block_size)
                if not buffer:
                    break
                file_size_downloaded += len(buffer)
                f.write(buffer)

                mb = file_size_downloaded / 1000000
                print(f"Application File: Downloaded {mb:.2f}MB")
    except Exception:
        import traceback

        traceback.print_exc()
        print("Failed to download files")
        input()
        sys.exit()


# See https://stackoverflow.com/questions/3667865/python-tarfile-progress-output
def track_progress(members: Iterable[tarfile.TarInfo]):
    for member in members:
        yield member
        print(f"Extracted (size:{member.size:<8}) {member.name}")


def extract_files(target: "str"):
    with zipfile.ZipFile("tempfile", "r") as z:
        z.extractall()

    if os.path.isdir(target):
        files = os.listdir(target)
        if len(files) != 1:
            print("Invalid zip content")
            input()
            sys.exit(1)
        data_file = files[0]

        print("Found Data File: ", data_file)
        print("Extracting...")
        s = time.time()

        with tarfile.open(f"{target}/{data_file}", "r:xz") as tar:
            tar.extractall(members=track_progress(tar))

        print(f"Done Extracting in {time.time() - s} seconds")

    else:
        print("No files...")

    # tar = tarfile.open(fname, "r:gz")
    # tar.extractall()
    # tar.close()


if __name__ == '__main__':
    system = platform.system()
    if system == "Windows":
        artifact_target = "windows"
    elif system == "Linux":
        artifact_target = "ubuntu"
    elif system == "Darwin":
        artifact_target = "macOS"
    else:
        print("Unsupported System")
        input()
        sys.exit(1)
    if os.path.exists("tempfile"):
        print("File already downloaded. Continuing...")
    else:
        download_files(artifact_target)
    extract_files(artifact_target)
