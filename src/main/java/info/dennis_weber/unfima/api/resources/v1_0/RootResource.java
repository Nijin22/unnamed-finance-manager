package info.dennis_weber.unfima.api.resources.v1_0;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
public class RootResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RootResourceResult getRoot() {
        RootResourceResult result = new RootResourceResult();
        result.setApiVersion("1.0-SNAPSHOT");
        return result;
    }

    private static class RootResourceResult {
        private String apiVersion;

        public String getApiVersion() {
            return apiVersion;
        }

        public void setApiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
        }
    }
}