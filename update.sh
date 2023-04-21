#!/bin/bash
systemctl stop isb-bot
send "systemctl stop isb-bot -> success"
git pull
send "git pull -> success"
mvn clean install -Dmaven.test.skip
send "mvn clean install -Dmaven.test.skip -> success"
systemctl start isb-bot
send "systemctl start isb-bot -> success"