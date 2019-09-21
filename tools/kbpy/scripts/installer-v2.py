from urllib.request import urlopen, urlretrieve
from urllib.parse import urlparse
from typing import Iterable
from subprocess import Popen, PIPE
from threading import Thread
from traceback import print_exc
import json
import sys
import platform
import tarfile
import zipfile
import os
import os.path
import venv
import time
import datetime
import argparse
import shutil


# See https://docs.python.org/3/library/venv.html
class ExtendedEnvBuilder(venv.EnvBuilder):
    """
    This builder installs setuptools and pip so that you can pip or
    easy_install other packages into the created virtual environment.

    param nodist: If True, setuptools and pip are not installed into the
                   created virtual environment.
    param nopip: If True, pip is not installed into the created
                  virtual environment.
    param progress: If setuptools or pip are installed, the progress of the
                     installation can be monitored by passing a progress
                     callable. If specified, it is called with two
                     arguments: a string indicating some progress, and a
                     context indicating where the string is coming from.
                     The context argument can have one of three values:
                     'main', indicating that it is called from virtualize()
                     itself, and 'stdout' and 'stderr', which are obtained
                     by reading lines from the output streams of a subprocess
                     which is used to install the app.

                     If a callable is not specified, default progress
                     information is output to sys.stderr.
    """

    def __init__(self, *args, **kwargs):
        self.nodist = kwargs.pop('nodist', False)
        self.nopip = kwargs.pop('nopip', False)
        self.progress = kwargs.pop('progress', None)
        self.verbose = kwargs.pop('verbose', False)
        super().__init__(*args, **kwargs)

    def post_setup(self, context):
        """
        Set up any packages which need to be pre-installed into the
        virtual environment being created.

        param context: The information for the virtual environment
                        creation request being processed.
        """
        os.environ['VIRTUAL_ENV'] = context.env_dir
        if not self.nodist:
            self.install_setuptools(context)
        # Can't install pip without setuptools
        if not self.nopip and not self.nodist:
            self.install_pip(context)

    def reader(self, stream, context):
        """
        Read lines from a subprocess' output stream and either pass to a progress
        callable (if specified) or write progress information to sys.stderr.
        """
        progress = self.progress
        while True:
            s = stream.readline()
            if not s:
                break
            if progress is not None:
                progress(s, context)
            else:
                if not self.verbose:
                    sys.stderr.write('.')
                else:
                    sys.stderr.write(s.decode('utf-8'))
                sys.stderr.flush()
        stream.close()

    def install_script(self, context, name, url):
        _, _, path, _, _, _ = urlparse(url)
        fn = os.path.split(path)[-1]
        binpath = context.bin_path
        distpath = os.path.join(binpath, fn)
        # Download script into the virtual environment's binaries folder
        urlretrieve(url, distpath)
        progress = self.progress
        if self.verbose:
            term = '\n'
        else:
            term = ''
        if progress is not None:
            progress('Installing %s ...%s' % (name, term), 'main')
        else:
            sys.stderr.write('Installing %s ...%s' % (name, term))
            sys.stderr.flush()
        # Install in the virtual environment
        args = [context.env_exe, fn]
        p = Popen(args, stdout=PIPE, stderr=PIPE, cwd=binpath)
        t1 = Thread(target=self.reader, args=(p.stdout, 'stdout'))
        t1.start()
        t2 = Thread(target=self.reader, args=(p.stderr, 'stderr'))
        t2.start()
        p.wait()
        t1.join()
        t2.join()
        if progress is not None:
            progress('done.', 'main')
        else:
            sys.stderr.write('done.\n')
        # Clean up - no longer needed
        os.unlink(distpath)

    def install_setuptools(self, context):
        """
        Install setuptools in the virtual environment.

        param context: The information for the virtual environment
                        creation request being processed.
        """
        url = 'https://bitbucket.org/pypa/setuptools/downloads/ez_setup.py'
        self.install_script(context, 'setuptools', url)
        # clear up the setuptools archive which gets downloaded
        pred = lambda o: o.startswith('setuptools-') and o.endswith('.tar.gz')
        files = filter(pred, os.listdir(context.bin_path))
        for f in files:
            f = os.path.join(context.bin_path, f)
            os.unlink(f)

    def install_pip(self, context):
        """
        Install pip in the virtual environment.

        param context: The information for the virtual environment
                        creation request being processed.
        """
        url = 'https://raw.github.com/pypa/pip/master/contrib/get-pip.py'
        self.install_script(context, 'pip', url)


def env_main(args=None):
    parser = argparse.ArgumentParser(prog=__name__,
                                     description='Creates virtual Python '
                                                 'environments in one or '
                                                 'more target '
                                                 'directories.')
    parser.add_argument('dirs', metavar='ENV_DIR', nargs='+',
                        help='A directory in which to create the'
                             'virtual environment.')
    parser.add_argument('--no-setuptools', default=False,
                        action='store_true', dest='nodist',
                        help="Don't install setuptools or pip in the "
                             "virtual environment.")
    parser.add_argument('--no-pip', default=False,
                        action='store_true', dest='nopip',
                        help="Don't install pip in the virtual "
                             "environment.")
    parser.add_argument('--system-site-packages', default=False,
                        action='store_true', dest='system_site',
                        help='Give the virtual environment access to the '
                             'system site-packages dir.')
    if os.name == 'nt':
        use_symlinks = False
    else:
        use_symlinks = True
    parser.add_argument('--symlinks', default=use_symlinks,
                        action='store_true', dest='symlinks',
                        help='Try to use symlinks rather than copies, '
                             'when symlinks are not the default for '
                             'the platform.')
    parser.add_argument('--clear', default=False, action='store_true',
                        dest='clear', help='Delete the contents of the '
                                           'virtual environment '
                                           'directory if it already '
                                           'exists, before virtual '
                                           'environment creation.')
    parser.add_argument('--upgrade', default=False, action='store_true',
                        dest='upgrade', help='Upgrade the virtual '
                                             'environment directory to '
                                             'use this version of '
                                             'Python, assuming Python '
                                             'has been upgraded '
                                             'in-place.')
    parser.add_argument('--verbose', default=False, action='store_true',
                        dest='verbose', help='Display the output '
                                             'from the scripts which '
                                             'install setuptools and pip.')
    options = parser.parse_args(args)
    if options.upgrade and options.clear:
        raise ValueError('you cannot supply --upgrade and --clear together.')
    builder = ExtendedEnvBuilder(system_site_packages=options.system_site,
                                 clear=options.clear,
                                 symlinks=options.symlinks,
                                 upgrade=options.upgrade,
                                 nodist=options.nodist,
                                 nopip=options.nopip,
                                 verbose=options.verbose)
    for d in options.dirs:
        builder.create(d)


def make_venv():
    venv.create(env_dir="venv/", with_pip=True, clear=True)


def parse_build_id(build_id: str):
    a, b = build_id.split(".")
    return a, b


def download_files(target: "str"):
    api = "https://dev.azure.com/yuliu2016/knotbook/_apis/build"
    build_url = f"{api}/builds?branchName=refs/heads/master&$top=1&api-version=5.1"

    try:
        build_id = json.loads(urlopen(build_url).read())["value"][0]["id"]
    except Exception:
        print_exc()
        print("Failed to get latest build")
        input()
        sys.exit(1)

    print(f"Using latest build ID: #{build_id}")
    artifact_url = f"{api}/builds/{build_id}/artifacts?&api-version=5.1&artifactName={target}"

    try:
        download_url = json.loads(urlopen(artifact_url).read())["resource"]["downloadUrl"]
    except Exception:
        print_exc()
        print("Failed to get artifact url")
        input()
        sys.exit()

    print("Found download url!!")

    try:
        u = urlopen(download_url)
        block_size = 2 ** 18  # 4 blocks per Mb
        with open("tempfile", 'wb') as f:
            file_size_downloaded = 0
            while True:
                buffer = u.read(block_size)
                if not buffer:
                    break
                file_size_downloaded += len(buffer)
                f.write(buffer)

                mb = file_size_downloaded / 1000000
                print(f"Application Image: Downloaded {mb:.2f}MB")

    except Exception:
        print_exc()
        print("Failed to download files")
        input()
        sys.exit(1)


# See https://stackoverflow.com/questions/3667865/python-tarfile-progress-output
def track_progress(members: Iterable[tarfile.TarInfo]):
    for member in members:
        yield member
        print(f"Extracted (size:{member.size // 1000:>5} KB) {member.name}")


def extract_files(target: "str"):
    try:
        with zipfile.ZipFile("tempfile", "r") as z:
            z.extractall()
    except Exception:
        print_exc()
        print_exc("Failed to extract target")
        input()
        sys.exit(1)

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

        print(f"Done Extracting in {time.time() - s:.3f} seconds")

    else:
        print("Target file is not found")


def main():
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

    if hasattr(sys, 'base_prefix'):
        print("Cannot install a virtual environment "
              "because the script is currently running in one")
        pass

    system = platform.system()

    if system == "Windows":
        artifact_target = "windows"
    elif system == "Linux":
        artifact_target = "ubuntu"
    elif system == "Darwin":
        artifact_target = "macOS"
    else:
        print(f"The system {system} is unsupported")
        input()
        sys.exit(1)

    if os.path.exists("tempfile"):
        print("File already downloaded. Continuing...")
    else:
        download_files(artifact_target)
    extract_files(artifact_target)


if __name__ == '__main__':
    main()
