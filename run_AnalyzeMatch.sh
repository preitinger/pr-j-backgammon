#!/bin/sh


cd $(dirname $0)

java -ea -cp ~/javaee/glassfish5/glassfish/modules/javax.json.jar:. pr.backgammon.control.AnalyzeMatch $@

