package info.dennis_weber.unnamed_finance_manager.api.resources.v1_0;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
public class RootResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getRoot() {
        return "Hello World!";
    }
}
