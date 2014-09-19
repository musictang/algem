#!/bin/sh
# exemple compil 2.8.x
J_HOME=/opt/jdk1.7.0_51/bin
CLASSES_PATH=/home/jm/algem
CP=.:$CLASSES_PATH/lib/*

$J_HOME/javac -cp $CP -d $CLASSES_PATH -encoding utf8 net/algem/Algem.java net/algem/util/module/DesktopDispatcher.java
cp net/algem/messages.properties $CLASSES_PATH/net/algem/
cd $CLASSES_PATH

$J_HOME/jar -cfm build/Algem_2.8.x.2.jar algem.mf net/
#
rm -fr net
