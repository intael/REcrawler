FROM adoptopenjdk/maven-openjdk11:latest

RUN apt-get update && apt-get install mysql-client -y

# Install project dependencies and keep sources
WORKDIR /usr/src/app

# install maven dependency packages (keep in image)
COPY . .
RUN mvn clean package
