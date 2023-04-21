#!/bin/bash
# shellcheck disable=SC2154
echo "${bold}[INFO] RUN UPDATE -> SUCCESS";
systemctl stop isb-bot;
echo "${bold}[INFO] STOP - BOT -> SUCCESS";
git pull;
echo "${bold}[INFO] GIT PULL -> SUCCESS";
mvn clean install -Dmaven.test.skip;
echo "${bold}[INFO] MAVEN -> SUCCESS";
systemctl start isb-bot;
echo "${bold}[INFO] START BOT -> SUCCESS";