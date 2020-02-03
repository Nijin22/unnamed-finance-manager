package info.dennis_weber.unfima.api.errors

class ConflictException extends AbstractUserException {

  ConflictException(String errorMsg, String errorId) {
    super(409, errorMsg, errorId)
  }
}
