# Only needs to be run on machine once
apt install docker.io

if [ ! -d "UniDisk" ]; then
    # Make sure to add SSH key to Github account
    git clone git@github.com:B3J4y/UniDisk.git
fi