package info.dennis_weber.unfima.api.resources.v1_0

import info.dennis_weber.unfima.api.UnfimaSpecification

class RootResourceSpecification extends UnfimaSpecification {
    def "Calling the start page"() {
        given:
        URLConnection con = getConnection("")

        expect:
        con.getResponseCode() == 200
        con.getInputStream().getText().contains("Hello World!")
    }
}
