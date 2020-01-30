package info.dennis_weber.unfima.api

import spock.lang.Specification

abstract class UnfimaSpecification extends Specification {
    Application application

    def setup() {
        final String databaseJdbcUrl = "jdbc:h2:~/unfimaInMemoryTestDb;MODE=MySQL;DATABASE_TO_LOWER=TRUE"
        final String databaseUsername = "sa"
        final String databasePassword = null

        application = new Application(databaseJdbcUrl, databaseUsername, databasePassword)
        application.cleanupDatabase()
        application.startServer()
    }
}
