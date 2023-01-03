FROM bellsoft/liberica-openjdk-alpine:17.0.5@sha256:6f29c4faa06597ef43977746bdcd7f43c49eb84eaf23e1d2fcc95e3ce9f1b3e2
RUN apk add --no-cache bash
EXPOSE 8080:8080
COPY build/libs/*.jar app.jar
COPY .initscripts vault-to-env.sh
RUN chmod +x vault-to-env.sh
CMD ["dumb-init", "--", "vault-to.env.sh"]
ENTRYPOINT ["java","-jar","app.jar"]
