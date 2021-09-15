#!/bin/bash


dir_path=$(dirname $0)

source $dir_path/.create.sh
source $dir_path/.env.sh

python $dir_path/create.py