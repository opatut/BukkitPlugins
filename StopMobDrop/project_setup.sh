#!/bin/bash
NAME=$1
sed -i 's/^name:.*$/name: '$NAME'/g' src/main/resources/plugin.yml
sed -i 's/^main:.*$/main: me.opatut.bukkit.'$NAME'.'$NAME'/g' src/main/resources/plugin.yml
sed -i 's/%PROJECT_NAME%/'$NAME'/g' build.xml

mkdir -p 'src/main/java/me/opatut/bukkit/'$NAME'/'
echo "class "$NAME" {}" >> 'src/main/java/me/opatut/bukkit/'$NAME'/'$NAME'.java'
echo "Project setup done."
