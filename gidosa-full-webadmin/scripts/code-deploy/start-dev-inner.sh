#!/bin/bash

WEBAPP_BASE_FOLDER=/home/gapture/apps-gidosa
WEBAPP_BACKEND_FOLDER=gidosa-full-webadmin
WEBAPP_FOLDER="$WEBAPP_BASE_FOLDER/$WEBAPP_BACKEND_FOLDER"

WEBAPP_API_NAME=gidosa-full-webadmin
WEBAPP_API_FULL="$WEBAPP_FOLDER/$WEBAPP_API_NAME"

WEBAPP_API_LOGS_FOLDER=logs-fulls-all
WEBAPP_API_LOGS_FOLDER_FULL="$WEBAPP_BASE_FOLDER/$WEBAPP_API_LOGS_FOLDER"

DEPLOY_LOG_NAME="$WEBAPP_API_NAME-deploy.log"
DEPLOY_LOG_FULL="$WEBAPP_API_FULL-deploy.log"

PROFILE=$(cat ${WEBAPP_FOLDER}/profile)
#PINPOINT_AGENT_FILENAME=pinpoint-bootstrap-2.5.3.jar
#PINPOINT_AGENT=./agents-dependency/pinpoint/pinpoint-bootstrap-2.5.3.jar
#PINPOINT_CONFIG=./agents-dependency/pinpoint/pinpoint-root.config

cp -f ${WEBAPP_FOLDER}/gidosa-full-webadmin/build/libs/${WEBAPP_API_NAME}.jar ${WEBAPP_FOLDER}/${WEBAPP_API_NAME}.jar


APP_LOG="$WEBAPP_API_LOGS_FOLDER_FULL/$WEBAPP_API_NAME.log"
ERROR_LOG="$WEBAPP_API_LOGS_FOLDER_FULL/$WEBAPP_API_NAME-error.log"

cd ${WEBAPP_FOLDER}

echo "gidosa-full-webadmin starting"
#nohup java -jar -javaagent:${PINPOINT_AGENT} -Dpinpoint.applicationName=${PINPOINT_APPLICATION_NAME} -Dpinpoint.agentId=${PINPOINT_AGENT_ID} -Dpinpoint.config=${PINPOINT_CONFIG} -Dspring.profiles.active=${PROFILE} -Duser.timezone=Asia/Seoul ${WEBAPP_API_NAME}.jar > $APP_LOG 2> $ERROR_LOG &
nohup /home/gapture/Downloads/jdk-21/bin/java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8120 -jar gidosa-full-webadmin.jar > /home/gapture/apps-gidosa/logs-fulls-all/gidosa-full-webadmin.log 2> /home/gapture/apps-gidosa/logs-fulls-all/gidosa-full-webadmin-error.log &
sleep 10       # pid파일이 만들어지기까지 기다리는 예상시간

WEBAPP_API_PID=$(cat $WEBAPP_API_NAME.pid)
TIME_NOW=$(date +%c)

echo "$TIME_NOW > 실행된 프로세스 아이디는 $WEBAPP_API_PID 입니다." >> $DEPLOY_LOG_FULL
