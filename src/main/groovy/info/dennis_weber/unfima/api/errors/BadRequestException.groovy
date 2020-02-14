package info.dennis_weber.unfima.api.errors

class BadRequestException extends AbstractUserException {

  BadRequestException(String errorMsg, String errorId) {
    super(400, errorMsg, errorId)
  }

  BadRequestException(String errorMsg) {
    this(errorMsg, null)
  }
}
