#!/bin/bash
NUM=`cat $1`
echo $(($NUM+1)) > $1

