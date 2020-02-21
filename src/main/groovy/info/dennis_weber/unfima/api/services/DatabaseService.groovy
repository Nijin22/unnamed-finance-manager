package info.dennis_weber.unfima.api.services

import groovy.sql.Sql

import java.sql.Clob

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

  /**
   * Since Unfima supports both MySql as well as H2 (during tests) and both databases have different implementations for
   * 'MEDIUMTEXT', we need to convert 'Clob's from H2 to normal Strings.
   *
   * @param possibleClob the Clob / String to convert from
   * @return a String
   */
  static String convertPossibleClobToString(def possibleClob) {
    if (possibleClob instanceof String) {
      // DB Backend == MariaDB or MySql
      return possibleClob
    } else if (possibleClob instanceof Clob) {
      // DB Backend == H2
      return (possibleClob as Clob).characterStream.text
    } else {
      throw new IllegalArgumentException("Possible Clob was neither a Clob nor a String, it actually was a '${possibleClob.class}'")
    }
  }
}
