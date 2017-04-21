@ECHO OFF

SET CLASSPATH=lib\aegisconsole.jar;lib\omnilink.jar;%JAVA_HOME%\lib\comm.jar

%JAVA_HOME%\bin\java -classpath %CLASSPATH% TestDriver
