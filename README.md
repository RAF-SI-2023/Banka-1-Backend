# Backend Banka 1
test
## Running the application

### Locally

1. Clone the repository
2. Create application.properties file in the src/main/resources directory(see example/application.properties.example)
3. Setup your database locally
4. Run the application

### Docker

1. Clone the repository
2. Create application.properties file in the src/main/resources directory(see example/application.properties.example)
3. Use the commented datasource url in the application.properties file
4. Create init.sql file in root directory of the project(see init.sql.example)
5. Create docker-compose.yaml file in the root directory of the project(see docker-compose.yaml.example)
4. Run the following command to run the application
```bash
docker-compose up
```
5. Stop the application using the following command
```bash
docker-compose down
```

Note: If you are in development stages, you can use the following command to rebuild the image
```bash
docker-compose up --build
```

Troubleshooting: If you encounter any issues with creating the tables in the database, you can switch from create-drop to create strategy

## Maven setup for Windows

1. Go to https://maven.apache.org/download.cgi and download the binary zip archive version.
2. Create a new folder named "Maven" in the Program Files directory and extract the downloaded zip archive into this folder.
3. Open the Start menu and type "Edit the system environment variables."
4. Click on the "Environment Variables" button.
5. Under "System Variables" click the "New" button.
6. Enter "MAVEN_HOME" as the variable name and the path where the Maven folder is extracted as the variable value, for example, "C:\Program Files\Maven\apache-maven-3.9.6." 
7. In the "System Variables" section, locate the "Path" variable, select it, and click the "Edit" button.
8. Click the "New" button and add "%MAVEN_HOME%\bin" as a new entry.
9. Save everything and test the Maven installation by typing "mvn -version" in the command prompt.
10. If you encounter an error message stating "The JAVA_HOME environment variable is not defined correctly", proceed with the following steps:

### JAVA_HOME setup

1. Locate your JDK folder or download it from the site (it could be in the "C:\Users\your_name\\.jdks" folder).
2. Similar to the Maven setup, add a new system variable named "JAVA_HOME" with the path to the JDK folder as its value, for example, "C:\Users\your_name\\.jdks\openjdk-21.0.1"
3. Edit the "Path" variable and add "%JAVA_HOME%\bin" as a new entry.
4. Everything should be working now, and you should be able to run "mvn -version" in the command prompt.

Video tutorial: https://www.youtube.com/watch?v=3EfvEZ_wThc

## Running integration tests

1. If you are on windows, start docker engine (by running Docker Desktop).
2. Set application.properties on all services using the example file (application.properties.forinttest), in both test and main resources.
3. For the service you want to start integration tests, run the CucumberIntegrationTest class.

These instructions are subject to change as the project evolves.

Note: if you are running Docker desktop, make sure to close it after running the tests, and do wsl --shutdown in cmd to free up resources.

## Setting Up Pre-commit Hook for Linting and Testing in Git

After setting up the pre-commit hook, linting and tests will automatically run before each commit.

1. Open notepad or any text editor
2. Paste the following code into the editor:
```
#!/bin/bash

# Run Checkstyle
echo "Running Checkstyle..."
mvn checkstyle:check
lint_result=$?

# Run tests
echo "Running tests..."
mvn test
test_result=$?

# Check results
if [ $lint_result -ne 0 ] || [ $test_result -ne 0 ]; then
    echo "Linting or tests failed. Aborting commit."
    exit 1
fi

echo "Linting and tests passed. Committing..."
exit 0
```
3. Save the file and name it "pre-commit" (without any file extension) 
in the ".git/hooks" folder in your project root directory. For example:
(C:\Users\your-name\Desktop\Banka-1-Backend\.git\hooks)
4. Open a terminal and navigate to your project root directory.
5. Run the following command in the terminal:
```
icacls .git\hooks\pre-commit /inheritance:r /grant:r "$($env:UserName):(RX)"
```
6. Everything should be set now.
7. If you want to remove the pre-commit file, navigate to your project root directory in the terminal and execute the following command:
```
rm .git/hooks/pre-commit
```