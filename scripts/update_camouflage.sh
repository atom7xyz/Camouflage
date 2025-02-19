#!/bin/bash

plugin_name="Camouflage"
plugin_version="-1.0-SNAPSHOT"
folder=$(pwd)
newfolder="${PWD%/*}/$plugin_name"
screen_name="dev-farmpvp"

echo RESTARTING MINECRAFT SERVER $screen_name
screen -S $screen_name -X stuff "restart\n"

echo DELETING OLD DIR
rm -r /home/farmpvp/maven/$plugin_name

echo RENAMING $folder TO $newfolder
mv $folder $newfolder

echo "COPYING OUTPUT TO MINECRAFT SERVER (~/plugins/)"
cp /home/farmpvp/maven/$plugin_name/target/$plugin_name$plugin_version.jar ~/plugins/

echo DONE