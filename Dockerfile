# See hseeberger/scala-sbt

# Use Java8
FROM openjdk:8-jdk

# Install Scala
RUN touch /usr/lib/jvm/java-8-openjdk-amd64/release
ENV SCALA_VERSION 2.10.3
RUN curl -fsL https://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz \
    | tar xfz - -C /usr/local --strip-components 1

# Install SBT 
ENV SBT_VERSION 0.13.15

RUN curl -fsL https://piccolo.link/sbt-${SBT_VERSION}.tgz | \
    tar xfz - -C /usr/local --strip-components 1

# Install omp4j
RUN git clone --recursive https://github.com/omp4j/omp4j.git /omp4j
WORKDIR /omp4j
RUN sbt compile && \
    sbt test && \
    sbt assembly && \
    sbt doc

# Get back to root's home
WORKDIR /root
RUN echo "java -jar /omp4j/target/scala-2.10/omp4j-assembly-1.3.jar" > /usr/local/bin/omp4j && \
    chmod +x /usr/local/bin/omp4j

CMD ["omp4j"]
