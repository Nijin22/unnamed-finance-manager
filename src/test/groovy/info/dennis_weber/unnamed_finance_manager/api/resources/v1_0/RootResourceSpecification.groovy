package info.dennis_weber.unnamed_finance_manager.api.resources.v1_0

import info.dennis_weber.unnamed_finance_manager.api.UnfimaSpecification

class RootResourceSpecification extends UnfimaSpecification {
    def "Calling the start page"() {
        given:
        URLConnection con = getConnection("")

        expect:
        con.getResponseCode() == 200
        con.getInputStream().getText().contains("Hello World!")
    }
}
