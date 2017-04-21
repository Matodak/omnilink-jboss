@ECHO OFF

SET CLASSPATH=lib\aegisconsole.jar;lib\omnilink.jar

%JAVA_HOME%\bin\java -classpath %CLASSPATH% AegisCommandReader %1 %2 %3
