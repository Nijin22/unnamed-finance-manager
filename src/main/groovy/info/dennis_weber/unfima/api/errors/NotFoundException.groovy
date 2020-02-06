package info.dennis_weber.unfima.api.errors

class NotFoundException extends AbstractUserException {

  NotFoundException(String errorMsg, String errorId) {
    super(404, errorMsg, errorId)
  }

  NotFoundException(String errorMsg) {
    this(errorMsg, null)
  }
}
