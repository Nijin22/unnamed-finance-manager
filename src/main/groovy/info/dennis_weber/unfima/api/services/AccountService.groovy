package info.dennis_weber.unfima.api.services

import info.dennis_weber.unfima.api.helpers.AbstractDto

class AccountService {

}

final class AccountDto extends AbstractDto {
  Integer accountId
  Integer currencyId
  String accountName
  Boolean belongsToUser
  String notes

  //////////////////////
  // Checked Setters: //
  //////////////////////
  void setAccountName(String accountName) {
    this.accountName = doAttributeLengthCheck("accountName", accountName, 255)
  }

  void setNotes(String notes) {
    this.notes = doAttributeLengthCheck("notes", notes, 16777215)
  }

}
