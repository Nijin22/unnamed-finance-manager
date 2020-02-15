package info.dennis_weber.unfima.api

import info.dennis_weber.unfima.api.helpers.AbstractUnfimaSpecification

class RootSpecification extends AbstractUnfimaSpecification {

  def "Reaching the root site"() {
    when:
    client.get("/")

    then:
    client.response.status.code == 200
  }
}
