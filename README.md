# REST-API for Unfima - the personal finance manager
![Run tests with Gradle](https://github.com/Nijin22/unnamed-finance-manager/workflows/Run%20tests%20with%20Gradle/badge.svg)
[![codecov](https://codecov.io/gh/Nijin22/unnamed-finance-manager/branch/master/graph/badge.svg)](https://codecov.io/gh/Nijin22/unnamed-finance-manager)

Unfima is a self-hosted personal finance manager. I.e. the tool to choose if you want to get an overview over your
income, expenses and open bills. It supports you making smart and future-proof financial decisions

> **NOTE: Early in development**
>
> This project is still super early into devlopment and currently suited for end users. If you're interested in
> contributing just open a [GitHub issue](https://github.com/Nijin22/unnamed-finance-manager/issues) and let me know
> about your idea. I'll be sure to help you get anything working! :)

## Using this application

### I'm a end-user
This is the API backend for Unfima. That means, unless you are a hard-core programmer, you should try to find a
Unfima App instead. 

Unfortunately, there are no such apps available yet, as we are super early in development. Stay tuned!

### I'm a developer

Great to hear that! You will need a server that runs this API.

This GitHub Repository's `master` branch is deployed on Google Cloud Run, reachable at
[https://gcr.api.unfima.com](https://gcr.api.unfima.com/), which should be enough to get you started.
Not that this provides NO guarantees regarding availability at all. Also all submitted data is handled by Google.
If you care about availability or privacy, we strongly suggest hosting on your own.

You can also use a Docker image:
```bash
docker pull docker.pkg.github.com/nijin22/unnamed-finance-manager/unfima:master
```

Or you could grab a `*.jar` file from the [releases](https://github.com/Nijin22/unnamed-finance-manager/releases). 

## Developing this application

This git repository is set up as a [IntelliJ](https://www.jetbrains.com/idea/) project.
Therefore we suggest using this IDE and simply importing the entire repository.

### Prerequisites
* A Java 1.8 JDK
* A [MariaDB](https://mariadb.org/) database, user and password.
  In the next examples we assume they are both named "unfima"

### Run locally
The easiest way is to duplicate the IntelliJ "launch configuration" `TEMPLATE_EDIT_ENV_VARS` and changing
the environment variables.

If you set "UNFIMA_DATABASE_CLEAN_ON_START" to "true", all data will be wiped from the database and
the schema will be re-created. This might be helpful for developing but should NOT be set in productive environments.

If you prefer, you can also simply use the command line:

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

Once your application is running, you can visit http://localhost:5050 to access it. On its root path, the API
documentation will be served.

### Run tests
Either by running them through your IDE (IntelliJ: Use the `All Tests` run config"), or by running them via gradle.

```bash
# Set your JAVA_HOME to a JRE 1.8
export JAVA_HOME="/usr/lib/jvm/java-8-oracle"

# Run the application
./gradlew test
```

When running tests, the application will use a build-in in-memory database. No need to worry about credentials!
