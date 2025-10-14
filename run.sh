#!/bin/sh


cd $(dirname $0)

java -ea  -cp ~/javaee/glassfish5/glassfish/modules/javax.json.jar:./lib/opencv-4130.jar:. pr.backgammon.Test

