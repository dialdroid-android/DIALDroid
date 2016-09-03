#!/bin/bash

dbname="dialdroid_test1"
jarfile="dialdroid.jar"
classpath="/home/bosu/AndroidSDK/android-sdk-linux/platforms/"

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters. Use: directory category"
    exit 1
fi

category=$2
timelimit=14400


for file in $(find $1)
do
   if [[ $file == *.apk ]] 
   then
	echo $file	
	timeout $timelimit java -Xms16G -Xmx64G -jar $jarfile appanalysis $classpath $dbname $file $category
   fi
done

java -jar $jarfile computeicc $classpath $dbname 
