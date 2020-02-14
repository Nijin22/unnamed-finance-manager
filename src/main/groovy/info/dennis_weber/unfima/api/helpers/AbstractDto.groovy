package info.dennis_weber.unfima.api.helpers

import info.dennis_weber.unfima.api.errors.BadFormatException

class AbstractDto {

  // See: https://codereview.stackexchange.com/a/139436/110626
  Map toMap() {
    this.metaClass.properties.findAll { 'class' != it.name }.collectEntries {
      if (AbstractDto.isAssignableFrom(it.type)) {
        [(it.name): this."$it.name"?.toMap()]
      } else {
        [(it.name): this."$it.name"]
      }
    }
  }

  Map toMapWithoutNull() {
    Map result = [:]
    this.toMap().each {
      if (it.value != null) {
        result.put(it.key, it.value)
      }
    }

    return result
  }

  /**
   * Ensures a user-submitted parameter is of valid length, or throws a BadFormatException if it is not
   *
   * @param attributeName
   * @param attributeValue
   * @param maxLength
   *
   * @return attributeValue if everything is okay
   */
  protected static String doAttributeLengthCheck(String attributeName, String attributeValue, int maxLength) {
    if (attributeValue != null && attributeValue.length() > maxLength) {
      throw new BadFormatException(
          "Supported maximum length for '$attributeName' is $maxLength " +
          "but the provided value is ${attributeValue.length()} charcters long. " +
          "The value you provided is '$attributeValue'",
          "ATTRIBUTE_TOO_LONG")
    } else {
      return attributeValue
    }
  }
}
