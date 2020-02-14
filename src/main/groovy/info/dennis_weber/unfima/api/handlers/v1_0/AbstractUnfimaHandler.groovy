package info.dennis_weber.unfima.api.handlers.v1_0

import info.dennis_weber.unfima.api.errors.BadRequestException
import info.dennis_weber.unfima.api.helpers.AbstractDto
import ratpack.groovy.handling.GroovyHandler

abstract class AbstractUnfimaHandler extends GroovyHandler {

  /**
   * Given a DTO and a list of required parameters, check if all parameters are filled in this DTO, or throw a
   * BadRequestException with details about the missing parameter.
   *
   * @param dto
   * @param requiredAttributes
   * @throws BadRequestException
   */
  protected static void checkRequiredAttributesArePresent(AbstractDto dto, List<String> requiredAttributes) throws BadRequestException {
    requiredAttributes.each {
      if (dto.getProperty(it) == null) {
        throw new BadRequestException("Required parameter '$it' is missing.", "REQUIRE_PARAMETER_MISSING")
      }
    }
  }
}
