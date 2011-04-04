#!/bin/bash
BUILD=`cat build`
VERSION=`cat version`.$BUILD
sed -i 's/^version:.*$/version: '$VERSION'/g' src/main/resources/plugin.yml
echo "Update version number to $VERSION."
