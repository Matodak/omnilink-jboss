@ECHO OFF

SET CLASSPATH=lib\aegisconsole.jar;lib\omnilink.jar

%JAVA_HOME%\bin\java -classpath %CLASSPATH% NetworkConsole %1 %2 %3 %4 %5
