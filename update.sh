#!/bin/bash
# https://mb4.ru/programming/bash/853-colors-for-bash.html
echo "${BOLD}[INFO] ${RED}RUN UPDATE -> ${GREEN}SUCCESS";
systemctl stop isb-bot;
echo "${BOLD}[INFO] ${RED}STOP - BOT -> ${GREEN}SUCCESS";
git pull;
echo "${BOLD}[INFO] ${RED}GIT PULL -> ${GREEN}SUCCESS";
mvn clean install -Dmaven.test.skip;
echo "${BOLD}[INFO] ${RED}MAVEN -> ${GREEN}SUCCESS";
systemctl start isb-bot;
echo "${BOLD}[INFO] ${RED}START BOT -> ${GREEN}SUCCESS";