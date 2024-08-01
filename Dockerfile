FROM gradle:7.1 AS BUILD
RUN mkdir /app
COPY . /app/
WORKDIR /app
RUN chmod +x gradlew
RUN gradle installDist

FROM openjdk:11 AS RUN
RUN mkdir -p /app/
RUN useradd -u 1000 tajhotels
COPY --from=BUILD /app/build/install/order-service/ /app/
COPY ./agentlib/applicationinsights-agent-3.4.17.jar agent.jar
COPY ./agentlib/applicationinsights.json applicationinsights.json
ENV JAVA_OPTS="-javaagent:/agent.jar"
WORKDIR /app/bin
RUN chmod +x /app/bin/
EXPOSE 8086:8086
USER tajhotels
CMD ["./order-service"]
