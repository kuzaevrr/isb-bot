#!/bin/bash
echo "[INFO] RUN UPDATE -> SUCCESS";
systemctl stop isb-bot;
echo "[INFO] STOP - BOT -> SUCCESS";
git pull;
echo "[INFO] GIT PULL -> SUCCESS";
mvn clean install -Dmaven.test.skip;
echo "[INFO] MAVEN -> SUCCESS";
systemctl start isb-bot;
echo "[INFO] START BOT -> SUCCESS";