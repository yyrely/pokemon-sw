FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 复制你的jar包到镜像中
COPY ./sw-app-0.0.1-SNAPSHOT.jar app.jar

# 使用 ENTRYPOINT 来设置固定的JVM参数和执行命令
ENTRYPOINT ["java", "-Xms512m", "-Xmx512m", "-Duser.timezone=Asia/Shanghai", "-Dspring.profiles.active=prod", "-jar", "app.jar"]

