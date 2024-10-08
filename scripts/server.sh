#!/bin/bash
cd ..
CURRENT_PATH=`pwd`
JAR_NAME=uts-seckill.jar
JAVA_PROCESS_ID=0
NAME=uts-seckill


#########################################################################
#                           启动程序
########################################################################
start(){
  checkPid
  echo -e "\n***************************************************************"
  if [ "$JAVA_PROCESS_ID" -ne 0 ]; then
        echo "[警告]：程序已经在运行,进程id为：$JAVA_PROCESS_ID"
  else
        echo -n "开始启动程序...."
        echo -e '\n当前路径：'$CURRENT_PATH
        nohup java -jar -Duser.timezone=Asia/Shanghai $CURRENT_PATH/$JAR_NAME >/dev/null 2>&1 &
        checkPid
        if [ $JAVA_PROCESS_ID -ne 0 ] ; then
              echo "[成功]：程序启动成功,进程号为：$JAVA_PROCESS_ID"
        else
              echo "[失败]：程序启动失败..."
        fi
  fi
  echo -e "***************************************************************\n"
}


#########################################################################
#                           终止程序
########################################################################
stop(){
  checkPid
  echo -e "\n***************************************************************"
  if [ "$JAVA_PROCESS_ID" -ne 0 ]; then
        echo "开始停止程序(进程id=${JAVA_PROCESS_ID})"
        kill -9 $JAVA_PROCESS_ID
        if [ $? -eq 0 ] ; then
            echo "[成功]：程序已经成功停止！"
        else
            echo "[失败]：程序终止失败！"
        fi
  else
        echo "[警告]：程序不在运行！"
  fi
  echo -e "***************************************************************\n"
}


########################################################################
#                          查看服务状态
#######################################################################
status(){
  checkPid
  echo -e "\n***************************************************************"
  if [ $JAVA_PROCESS_ID -ne 0 ] ; then
      echo "[成功]：程序在运行中，进程号为：$JAVA_PROCESS_ID"
  else
      echo "[成功]：程序未运行..."
  fi
  echo -e "***************************************************************\n"
  #help
}


###########################################################################
#                           判断服务是否已经启动
###########################################################################
checkPid(){
  temp1=`jps -l | grep $NAME`
  temp2=(${temp1// / })
  java_ps=${temp2[0]}
  #echo 'java_ps='${java_ps}
  if [ -n "$java_ps" ] ;then
      JAVA_PROCESS_ID=`echo $java_ps`
  else
      JAVA_PROCESS_ID=0
  fi
}

###########################################################################
#                           使用帮助
##########################################################################
help(){
  echo "*********************** 使用帮助 *****************************"
  echo "**           启动命令：sh server.sh start                   **"
  echo "**           终止命令：sh server.sh stop                    **"
  echo "**           状态命令：sh server.sh status                  **"
  echo "**           帮助命令：sh server.sh help                    **"
  echo "**************************************************************"
}


########################### 开始执行程序 ##################################
case "$1" in
    'start')
        start
    ;;
    'stop')
        stop
    ;;
    'status')
        status
    ;;
    'help')
        help
    ;;
esac