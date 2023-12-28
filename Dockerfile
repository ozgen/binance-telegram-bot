# Use an official Java runtime as a parent image
FROM openjdk:11-jdk

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the current directory contents into the container at /usr/src/app
COPY . .

# Build the application
RUN mvn clean install -DskipTests

# Run the jar file
ENTRYPOINT ["java", "-jar", "target/binance-telegram-bot-0.0.1-SNAPSHOT.jar"]
