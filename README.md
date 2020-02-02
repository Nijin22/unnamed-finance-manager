# REST-API for Unfima - the personal finance manager
[![codecov](https://codecov.io/gh/Nijin22/unnamed-finance-manager/branch/master/graph/badge.svg)](https://codecov.io/gh/Nijin22/unnamed-finance-manager)

> TODO: Add documentation about what this application does.

## Using this application

### I'm a end-user
This is the API backend for Unfima. That means, unless you are a hard-core programmer, you should try to find a
Unfima App instead. 

> TODO: Currently there are no such apps. Edit this section when there are any!

### I'm a developer

Great! This means you need to consume the API. Head over to the
[Unfima API documentation](http://nijin22.github.io/unnamed-finance-manager) to learn how.

You will also need a server that runs this API. Currently, there are none publicly available.
Therefore you need to clone this project and keep reading to learn how to run it on your local computer.

## Developing this application

This git project is setup as a [IntelliJ](https://www.jetbrains.com/idea/) project.
Therefore we suggest using this IDE and simply importing the entire repository.

### Run locally
Either by starting the `Application` class from within your IDE, or running it via gradle.

Note that you need to supply environmental variables for a MariaDb (or MySql) database connection
and ensure that JAVA_HOME is set to a 1.8 JRE.

If you set "UNFIMA_DATABASE_CLEAN_ON_START" to "true", all data will be wiped from the database and
the schema will be re-created. This might be helpful for developing but should NOT be set in productive environments.

```bash
# Set your JAVA_HOME to a JRE 1.8
export JAVA_HOME="/usr/lib/jvm/java-8-oracle"

# Set the database connection
# The example assumes a locally running MariaDb with a database called "unfima"
export UNFIMA_DATABASE_JDBC_URL="jdbc:mariadb://localhost:3306/unfima"
export UNFIMA_DATABASE_USERNAME="unfima"
export UNFIMA_DATABASE_PASSWORD="<your_super_secret_password_here>"
# export UNFIMA_DATABASE_CLEAN_ON_START="true"

# Run the application
./gradlew run
```

### Run tests
Either by running them through your IDE (IntelliJ: Right-click on the "test" folder and select "Run 'All Tests'"), or by running them via gradle.

When running tests, the application will use a build-in in-memory database. No need to worry about credentials!

```bash
# Set your JAVA_HOME to a JRE 1.8
export JAVA_HOME="/usr/lib/jvm/java-8-oracle"

# Run the application
./gradlew test
```
