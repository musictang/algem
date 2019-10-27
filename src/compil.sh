#!/bin/sh
# exemple compil 2.17.x
CLASSES_PATH=$HOME/algem
CP=.:$CLASSES_PATH/lib/*

javac -cp $CP -d $CLASSES_PATH -encoding utf8 net/algem/Algem.java net/algem/util/module/DesktopDispatcher.java
cp net/algem/messages.properties $CLASSES_PATH/net/algem/
cd $CLASSES_PATH

jar -cfm build/Algem_2.17.x.jar algem.mf net/
#
rm -fr net
