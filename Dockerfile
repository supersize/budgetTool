# Step 1: Use a lightweight JRE (Runtime only) to save RAM
FROM eclipse-temurin:17-jre-alpine

# Step 2: Set the working directory inside the container
# you can see "/app" dir when you access to the container by "docker exec it ~"
WORKDIR /app


# Step 3: Copy the JAR built by GitHub Actions into the container
# Note: GitHub Actions usually puts the JAR in build/libs/
COPY build/libs/budgetTool-0.0.1-SNAPSHOT.jar budgetTool.jar

# Step 4: Run the application with memory-efficient flags for t3.micro
# -XX:MaxRAMPercentage=75.0 helps Java stay within Docker's memory limits
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "budgetTool.jar"]
