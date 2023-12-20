FROM maven:3.9.5-amazoncorretto-17-debian-bookworm

RUN apt update && apt upgrade -y

# Install project dependencies and keep sources
WORKDIR /usr/src/app

# install maven dependency packages (keep in image)
COPY . .
RUN mvn clean package && \
    mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium"
