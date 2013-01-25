#!/bin/sh
# exemple compil 2.7.a
J_HOME=/opt/jdk1.6.0_24/bin
CLASSES_PATH=/home/jm/Algem/
CP=.:$CLASSES_PATH/lib/postgresql-8.4-701.jdbc3.jar

$J_HOME/javac -cp $CP -d $CLASSES_PATH -encoding utf8 net/algem/Algem.java net/algem/util/module/DesktopDispatcher.java
cp net/algem/messages.properties $CLASSES_PATH/net/algem/
cd $CLASSES_PATH

$J_HOME/jar -cfm Algem_2.7.d.jar algem.mf net/
#
rm -fr net
