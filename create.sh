#!/bin/bash
NAME=$1
cp -r default_setup/ $NAME'/'
echo "Files copied."
cd $NAME
./project_setup.sh $NAME

