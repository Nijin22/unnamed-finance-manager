package info.dennis_weber.unfima.api.services

import groovy.sql.Sql

class DatabaseService {
  private String databaseJdbcUrl
  private String databaseUsername
  private String databasePassword

  DatabaseService(String databaseJdbcUrl, String databaseUsername, String databasePassword) {
    this.databaseJdbcUrl = databaseJdbcUrl
    this.databaseUsername = databaseUsername
    this.databasePassword = databasePassword
  }

  Sql getGroovySql() {
    return Sql.newInstance(databaseJdbcUrl, databaseUsername, databasePassword)
  }
}
