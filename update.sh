#!/bin/bash
systemctl stop isb-bot
git pull
mvn clean install -Dmaven.test.skip
systemctl stop isb-bot