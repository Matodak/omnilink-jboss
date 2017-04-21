#!/bin/sh

AEGIS_HOME=`dirname $0`

CLASSPATH=$AEGIS_HOME/lib/aegisconsole.jar:$AEGIS_HOME/lib/omnilink.jar

$JAVA_HOME/bin/java -classpath $CLASSPATH AegisClientConsole $1 $2
