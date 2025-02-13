#!/bin/bash

WEBAPP_BASE_FOLDER=/home/gapture/apps-gidosa
WEBAPP_BACKEND_FOLDER=gidosa-full-webapp
WEBAPP_FOLDER="$WEBAPP_BASE_FOLDER/$WEBAPP_BACKEND_FOLDER"

WEBAPP_API_NAME=gidosa-full-webapp
WEBAPP_API_FULL="$WEBAPP_FOLDER/$WEBAPP_API_NAME"

# 현재 구동 중인 애플리케이션 pid 확인
WEBAPP_API_PID_FILENAME=${WEBAPP_API_FULL}.pid

#cd ${WEBAPP_FOLDER}

TIME_NOW=$(date +%c)
DEPLOY_LOG="$WEBAPP_API_FULL-deploy.log"

# 프로세스가 켜져 있으면 종료(pid파일이 있으면)
if [ -f "$WEBAPP_API_PID_FILENAME" ]; then
  WEBAPP_API_PID=$(cat $WEBAPP_API_PID_FILENAME)

  kill -15 $WEBAPP_API_PID
  echo "$TIME_NOW > 실행중인 PID $WEBAPP_API_PID $WEBAPP_API_NAME.jar 애플리케이션 종료 " >> $DEPLOY_LOG
else
  echo "$TIME_NOW > 현재 실행중인 애플리케이션이 없습니다" >> $DEPLOY_LOG
fi