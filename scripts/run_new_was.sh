# run_new_was.sh
# !/bin/bash

CURRENT_PORT=$(cat /home/ec2-user/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> Current port of running WAS is ${CURRENT_PORT}."

source /home/ec2-user/app/export.sh

if [ ${CURRENT_PORT} -eq 8080 ]; then
  TARGET_PORT=8081
elif [ ${CURRENT_PORT} -eq 8081 ]; then
  TARGET_PORT=8080
else
  echo "> No WAS is connected to nginx"
fi

TARGET_PID=$(lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')

if [ ! -z ${TARGET_PID} ]; then
  echo "> Kill WAS running at ${TARGET_PORT}."
  sudo kill ${TARGET_PID}
fi

nohup java -jar -Dserver.port=${TARGET_PORT} /home/ec2-user/app/target/*.jar --spring.profiles.active=prod --spring.datasource.url=${DATABASE_URL} --spring.datasource.username=${DATABASE_USERNAME} --spring.datasource.password=${DATABASE_PASSWORD} --server.tomcat.basedir=${LOG_DIR} --my-app.client-id=${APP_CLIENT_ID} --my-app.client-secret=${APP_CLIENT_SECRET} --my-app.get-token-endpoint-url=${GET_TOKEN_ENDPOINT_URL} --my-app.check-token-endpoint-url=${CHECK_TOKEN_ENDPOINT_URL} > /home/ec2-user/nohup.out 2>&1 &
echo "> Now new WAS runs at ${TARGET_PORT}."
exit 0

