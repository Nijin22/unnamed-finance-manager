# REST-API for Unfima - the personal finance manager
![Main](https://github.com/Nijin22/unnamed-finance-manager/workflows/Main/badge.svg)
[![codecov](https://codecov.io/gh/Nijin22/unnamed-finance-manager/branch/master/graph/badge.svg)](https://codecov.io/gh/Nijin22/unnamed-finance-manager)

Unfima is a self-hosted personal finance manager. I.e. the tool to choose if you want to get an overview over your
income, expenses and open bills. It supports you making smart and future-proof financial decisions

> **NOTE: Early in development**
>
> This project is still super early in development and currently **not** suited for end users. If you're interested in
> contributing just open a [GitHub issue](https://github.com/Nijin22/unnamed-finance-manager/issues) and let me know
> about your idea. I'll be sure to help you get anything working! :)

## Using this application

### I'm a end-user
This is the API backend for Unfima. That means, unless you are a hard-core programmer, you should try to find a
Unfima App instead. 

Unfortunately, there are no such apps available yet, as we are super early in development. Stay tuned!

### I'm a developer

Great to hear that! You will need a server that runs this API.

#### Using Docker
This repository's `master` branch is also available as a Docker image via GitHub packages. To setup Unfima as a Docker 
image, use these commands:

Pull a up-to-date version of Unfima's docker image from GitHub packages:
```bash
docker pull docker.pkg.github.com/nijin22/unnamed-finance-manager/unfima:master
``` 

Create a network so that your database image and Unfima can communicate with each other:
```bash
docker network create unfima
```

Run MariaDB as your database (you might want to consider changing the passwords):
```bash
docker run --rm -d --net=unfima --name=dockerized-mariadb \
  -e MYSQL_ROOT_PASSWORD=unfima -e MYSQL_USER=unfima -e MYSQL_PASSWORD=unfima -e MYSQL_DATABASE=unfima \
  mariadb:10
```

Run Unfima (you might want to change `80` to another port you'd like to use)
```bash
docker run --rm -d --net=unfima --name=app -p 80:5050 \
  -e UNFIMA_DATABASE_JDBC_URL=jdbc:mysql://dockerized-mariadb:3306/unfima -e UNFIMA_DATABASE_USERNAME=unfima -e UNFIMA_DATABASE_PASSWORD=unfima \
  docker.pkg.github.com/nijin22/unnamed-finance-manager/unfima:master
```

## Developing this application

This git repository is set up as a [IntelliJ](https://www.jetbrains.com/idea/) project.
Therefore we suggest using this IDE and simply importing the entire repository.

### Prerequisites
* A Java 1.8 JDK
* A [MariaDB](https://mariadb.org/) database (or MySql), user and password.
  In the next examples we assume they are both named "unfima"

### Run locally
There are two main ways to run this application as a developer: As a IntelliJ run config or from the terminal.
In both cases you need to set some environmental variables.

If you set `UNFIMA_DATABASE_CLEAN_ON_START` to `true`, all data will be wiped from the database and
the schema will be re-created. This might be helpful for developing but should NOT be set in productive environments.

Once your application is running, you can visit http://localhost:5050 to access it. On its root path, the API
documentation will be served.

#### With IntelliJ
The easiest way is to duplicate the IntelliJ "launch configuration" `TEMPLATE_EDIT_ENV_VARS` and simply updating
the environment variables for your system.

#### With the terminal / gradlew
If you prefer, you can also use the command line:

```bash
# Set your JAVA_HOME to a JRE 1.8
export JAVA_HOME="/usr/lib/jvm/java-8-oracle"

# Set the database connection
# The example assumes a locally running MariaDb with a database called "unfima"
export UNFIMA_DATABASE_JDBC_URL="jdbc:mysql://localhost:3306/unfima"
export UNFIMA_DATABASE_USERNAME="unfima"
export UNFIMA_DATABASE_PASSWORD="<your_super_secret_password_here>"
# export UNFIMA_DATABASE_CLEAN_ON_START="true"

# Run the application
./gradlew run
```

### Run tests
Either by running them through your IDE (IntelliJ: Use the `All Tests` run config), or by running them via gradle.

```bash
# Set your JAVA_HOME to a JRE 1.8
export JAVA_HOME="/usr/lib/jvm/java-8-oracle"

# Run the application
./gradlew test
```

When running tests, the application will use a build-in in-memory database. No need to worry about credentials!
