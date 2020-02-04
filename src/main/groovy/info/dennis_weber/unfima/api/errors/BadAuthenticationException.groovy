package info.dennis_weber.unfima.api.errors

class BadAuthenticationException extends AbstractUserException {

  BadAuthenticationException(String errorMsg, String errorId) {
    super(401, errorMsg, errorId)
  }

  BadAuthenticationException(String errorMsg) {
    this(errorMsg, null)
  }
}
