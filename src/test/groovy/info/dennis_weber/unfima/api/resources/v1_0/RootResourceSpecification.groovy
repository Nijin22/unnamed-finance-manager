package info.dennis_weber.unfima.api.resources.v1_0

import groovy.json.JsonSlurper
import info.dennis_weber.unfima.api.UnfimaSpecification

class RootResourceSpecification extends UnfimaSpecification {
    def "Calling the start page"() {
        given:
        URLConnection con = getConnection("")
        def resultObject = new JsonSlurper().parse(con.getInputStream())

        expect:
        con.getResponseCode() == 200
        resultObject.apiVersion == "1.0-SNAPSHOT"
    }
}
