#!/bin/bash
## pre-package-libs.sh
TARGET=./target/libs
CUSTOM_DIR=./clients
## DON'T FORGET TO CHANGE MANIFEST FILE DEPENDING ON CONTEXT SIGNATURE
## algem-sec-srv.mf | algem-backup.mf | algem-demo.mf
MANIFEST=algem-backup.mf

if [ -d $TARGET ] && [ -n "$(ls -A $TARGET)" ]
then
  # CLIENT CUSTOMIZATION
  if [ -d $CUSTOM_DIR ] && [ -n "$(ls -A $CUSTOM_DIR)" ]
    then
      # CREATE CUSTOM PROPERTIES
      for propscript in $CUSTOM_DIR/properties_*.sh
        do
          [[ $propscript =~ properties_(.*).sh ]];
          CLIENT_ID=${BASH_REMATCH[1]}
          #echo "$CLIENT_ID";
          if ! $propscript algem.properties > $TARGET/algem.properties_"$CLIENT_ID"; then
            printf "Abandon :\nUne erreur s'est produite dans le traitement du fichier client algem_%s.properties\n" "$CLIENT_ID"
            exit 1
          fi
      done
      cp $CUSTOM_DIR/*.jar $TARGET/
  else
    echo "Custom clients directory $CUSTOM_DIR not found"
  fi
  cp algem.properties local.properties $TARGET/
  cd $TARGET || { echo "Moving to directory $TARGET failed"; exit 2; }
  # CREATE CLIENT JARS
  for propfile in algem.properties_*
    do
      [[ $propfile =~ algem.properties_(.*) ]];
      CID=${BASH_REMATCH[1]}
      jar cf algem.properties_"$CID".jar $propfile
  done
  # CREATE COMMON JARS
  jar cf algem.properties.jar algem.properties
  jar cf local.properties.jar local.properties
  # UPDATE MANIFEST
  for j in *.jar
  do
    if jar -ufm "$j" ../../$MANIFEST
    then
      echo "Manifest of $j updated"
    fi
  done
  # REMOVE COPIED PROPERTIES FILES
  rm algem.properties local.properties
else
  echo "Directory $TARGET not found"
fi
