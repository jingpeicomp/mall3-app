FROM registry.cn-zhangjiakou.aliyuncs.com/dacb/oracle-java8

ENV TZ=Asia/Shanghai
ENV LANG C.UTF-8
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
VOLUME /tmp
COPY target/mall3-app-1.0.0-SNAPSHOT.jar mall3-app.jar
EXPOSE 8290
EXPOSE 9290
ENTRYPOINT ["sh", "-c", "java $JAVA_TOOL_OPTIONS -jar mall3-app.jar"]