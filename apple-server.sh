#!/bin/bash

echo -e "----- ----- start deploy production ----- -----"


##### ed851303e11
## change project info
#####
LOCAL_CONF="apple-1.0-SNAPSHOT.conf"
SERVICE_NAME="apple"
SERVICE_NAME_CONF=apple-1.0-SNAPSHOT

REMOTE_USER="root"
REMOTE_HOST="39.105.105.104"



SERVICE_DIR="/etc/init.d"

#####
## deal deploy
#####

REMOTE_JAR_DIR="/root/application/"
REMOTE_JAR_BACKUP_DIR="/root/application/"
REMOTE_JAR_FILE="apple-1.0-SNAPSHOT.jar"

SERVICE_FULL_NAME=${SERVICE_DIR}/${SERVICE_NAME}
REMOTE_FULL_JAR_FILE=${REMOTE_JAR_DIR}/${REMOTE_JAR_FILE}


echo -e "    maven clean package  -Dmaven.test.skip=true "
mvn clean package -Dmaven.test.skip=true

buildJarFile=`ls target/*.jar`
echo -e " buildJarFile is ${buildJarFile} "
if [ -f ${buildJarFile} ];
then
    echo -e " build jar success "
    currentDayTime=`date "+%Y%m%d_%H%M%S"`
    backUpFile=${REMOTE_JAR_BACKUP_DIR}/${REMOTE_JAR_FILE}.${currentDayTime}

    ssh -l ${REMOTE_USER} ${REMOTE_HOST}  "
        if [ ! -d ${REMOTE_JAR_DIR} ]
        then
            mkdir -p ${REMOTE_JAR_DIR}
            echo -e \" create dir ${REMOTE_JAR_DIR} \"
        else
            echo -e \" exists dir ${REMOTE_JAR_DIR} \"
        fi
        "

    ssh -l ${REMOTE_USER} ${REMOTE_HOST} "
        if [ ! -d ${REMOTE_JAR_BACKUP_DIR} ]
        then
            mkdir -p ${REMOTE_JAR_BACKUP_DIR}
            echo -e \" create dir ${REMOTE_JAR_BACKUP_DIR}   \"
        else
             echo -e \" exists dir ${REMOTE_JAR_BACKUP_DIR}   \"
        fi
        "

    ssh -l ${REMOTE_USER} ${REMOTE_HOST} "
        if [ ! -f ${SERVICE_FULL_NAME} ]
        then
            ln -s ${REMOTE_FULL_JAR_FILE} ${SERVICE_FULL_NAME}
            echo -e \" create service ${SERVICE_FULL_NAME} link to ${SERVICE_FULL_JAR_FILE}  \"
        else
            echo -e \" exists service ${SERVICE_FULL_NAME}   \"
        fi
        "

    ssh -l ${REMOTE_USER} ${REMOTE_HOST} "
        if [ -f ${REMOTE_FULL_JAR_FILE} ]
        then
            echo -e \" currentDayTime is ${currentDayTime} backUpFile is ${backUpFile}  \"
            cp ${REMOTE_FULL_JAR_FILE} ${backUpFile}
            echo -e \" backup file ${REMOTE_FULL_JAR_FILE} ${backUpFile}  \"
        else
            echo -e \" not exists ${REMOTE_FULL_JAR_FILE} for backup.  \"
        fi
        "

     echo -e " copy file to remote server "
     remoteConfFile=${REMOTE_JAR_DIR}/${SERVICE_NAME_CONF}.conf
     scp ${LOCAL_CONF} ${REMOTE_USER}@${REMOTE_HOST}:${remoteConfFile}
     echo -e " scp conf file ${SERVICE_NAME_CONF}.conf to remote ${remoteConfFile} "

     scp ${buildJarFile} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_FULL_JAR_FILE}
     echo -e " scp jar file ${buildJarFile} to remote ${REMOTE_FULL_JAR_FILE}  "

     ssh -l ${REMOTE_USER} ${REMOTE_HOST} "service ${SERVICE_NAME} restart"

fi




echo -e "----- ----- finish deploy production ----- -----"




