from urllib.request import urlopen
import json
import sys
import platform
import tkinter
import tarfile
import zipfile
import os


def download_files(target: "str"):
    api = "https://dev.azure.com/yuliu2016/knotbook/_apis/build"
    build_url = f"{api}/builds?branchName=refs/heads/master&$top=1&api-version=5.1"

    try:
        build_id = json.loads(urlopen(build_url).read())["value"][0]["id"]
    except:
        print("Failed to get latest build")
        input()
        sys.exit(1)

    print(f"Using latest build ID {build_id}")
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
        with open("tempfile", 'wb') as f:
            file_size_downloaded = 0
            block_size = 65536
            while True:
                buffer = u.read(block_size)
                if not buffer:
                    break
                file_size_downloaded += len(buffer)
                f.write(buffer)
                print(f"Downloaded {file_size_downloaded / 1000000 :.2f}Mb")
    except Exception:
        import traceback

        traceback.print_exc()
        print("Failed to download files")
        input()
        sys.exit()


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
        print(data_file)
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
