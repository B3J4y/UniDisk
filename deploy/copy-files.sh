#!/bin/bash

# Copies local files that are needed to run application 
# that are not tracked via Git to destination machine.

while read -r line; 
do 
    if [ ! -f "$line" ]; then
        echo "File $line does not exist."
        exit 1
    fi
    scp $line root@stud-01.lab.cs.uni-potsdam.de:/root/UniDisk/$line
done < deploy/.files