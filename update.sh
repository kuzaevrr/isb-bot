#!/bin/bash
echo "Start update isb-bot";
systemctl stop isb-bot;
echo "systemctl stop isb-bot -> success";
git pull;
echo "git pull -> success";
mvn clean install -Dmaven.test.skip;
echo "mvn clean install -Dmaven.test.skip -> success";
systemctl start isb-bot;
echo "systemctl start isb-bot -> success";