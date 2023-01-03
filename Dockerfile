FROM navikt/java:15
COPY build/libs/*.jar app.jar
COPY .initscript /init-scripts
