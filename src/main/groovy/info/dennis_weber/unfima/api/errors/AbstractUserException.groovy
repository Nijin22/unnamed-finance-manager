package info.dennis_weber.unfima.api.errors

class AbstractUserException extends Exception {
  private int statusCode
  private String errorMsg
  private String errorId

  /**
   * A error that was caused by the user.
   *
   * @param statusCode HTTP status code to show to user
   * @param errorMsg Error description to show to user
   * @param errorId (optional) error ID to allow clients to parse the error
   */
  AbstractUserException(int statusCode, String errorMsg, String errorId) {
    super(errorMsg)

    this.statusCode = statusCode
    this.errorMsg = errorMsg
    this.errorId = errorId
  }

  // Getters:

  int getStatusCode() {
    return statusCode
  }

  String getErrorMsg() {
    return errorMsg
  }

  String getErrorId() {
    return errorId
  }
}
