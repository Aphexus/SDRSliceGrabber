# Alpine Linux with OpenJDK JRE
FROM openjdk:8-jre-alpine
# set spring profile and performance variables
ENV profile default
ENV chunkSize 1000
ENV threadPoolSize 4
ENV maxPoolSize 8
# copy JAR into image
COPY ./gtrslicegrabber-0.0.1-SNAPSHOT.jar /gtrslicegrabber.jar
# run java with optimum parameters for batch processing
ENTRYPOINT [ \
    "/usr/bin/java", \
    "-Dspring.profiles.active=${profile}", \
    "-Dslicegrabber.itemwriter.chunkSize=${chunkSize}", \
    "-Dslicegrabber.executors.threadPoolSize=${threadPoolSize}", \
    "-Dslicegrabber.datasource.maxPoolSize=${maxPoolSize}", \
    "-jar", \
    "/gtrslicegrabber.jar", \
    "BATCH" \
]
# specify asset class and date range via command line args or AWS Batch job parameters
CMD []

