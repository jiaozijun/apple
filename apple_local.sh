#!/bin/bash

echo -e "----- ----- start deploy production ----- -----"


##### ed851303e11
## change project info
#####


#####
## deal deploy
#####

REMOTE_JAR_DIR="/Users/nico/applicaiton"
REMOTE_JAR_FILE="apple-1.0-SNAPSHOT.jar"


echo -e "    maven clean package  -Dmaven.test.skip=true "
mvn clean package -Dmaven.test.skip=true

buildJarFile=`ls target/*.jar`
echo -e " buildJarFile is ${buildJarFile} "
if [ -f ${buildJarFile} ];
then
    echo -e " build jar success "
    cp  ${buildJarFile}  ${REMOTE_JAR_DIR}

     echo -e " scp jar file ${buildJarFile} to remote ${REMOTE_JAR_DIR}  "

     pid=`ps -ef | grep ${REMOTE_JAR_FILE} | grep -v grep | awk '{print $2}'`

     echo "pid:"${pid}

      if [ -n "${pid}" ];then

      echo "kill -9 的 pid:"${pid}

      kill -9 ${pid}

      fi

      sleep 5
      cd ${REMOTE_JAR_DIR}
    java -jar ${REMOTE_JAR_FILE}  &

    cd /Users/nico/code/apple

fi




echo -e "----- ----- finish deploy production ----- -----"