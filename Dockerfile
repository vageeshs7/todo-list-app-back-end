FROM openjdk:8u252-slim
COPY ./target/todolist-backend-0.0.1-SNAPSHOT.jar /opt/todolist-backend.jar
CMD ["java", "-jar", "/opt/todolist-backend.jar"]
EXPOSE 8081