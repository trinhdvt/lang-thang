FROM openjdk:8-jdk-alpine as build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src src
RUN chmod u+x ./mvnw
RUN ./mvnw package -Dspring-boot.run.profiles=docker -DskipTests

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:8-jre-alpine

ARG DEPENDENCY=/app/target/dependecy

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

EXPOSE 587

ENTRYPOINT ["java","-cp","app:app/lib/*", "com.langthang.LangThangApplication"]
