package info.dennis_weber.unfima.api.errors

class BadFormatException extends AbstractUserException {

  BadFormatException(String errorMsg, String errorId) {
    super(400, errorMsg, errorId)
  }

  BadFormatException(String errorMsg) {
    this(errorMsg, null)
  }
}
