FROM adoptopenjdk/maven-openjdk11:latest

# Install project dependencies and keep sources
WORKDIR /usr/src/app

# install maven dependency packages (keep in image)
COPY . .
RUN mvn -T 1C install
