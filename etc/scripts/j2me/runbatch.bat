@ECHO OFF

SET CLASSPATH=lib\aegisconsole.jar;lib\omnilink.jar
SET OLD_JAVA_HOME=%JAVA_HOME%
SET JAVA_HOME=d:\users\martin\apps\WECE\foundation10

%JAVA_HOME%\bin\j9.exe -jcl:foun10 -cp %CLASSPATH% AegisCommandReader %1 %2 %3

SET JAVA_HOME=%OLD_JAVA_HOME%
