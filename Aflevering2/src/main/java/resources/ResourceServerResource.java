package resources;

import api.AccessToken;
import api.Saying;
import api.TokenValidation;
import api.UserData;
import db.ResourceDatabase;
import db.ResourceDatabaseMock;
import exceptions.AuthorizationDeniedException;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/resource")
@Produces(MediaType.APPLICATION_JSON)
public class ResourceServerResource {

    private ResourceDatabase resourceDatabase;

    public ResourceServerResource(ResourceDatabase resourceDatabase) {
        this.resourceDatabase = resourceDatabase;
    }

    @GET
    public Saying wutFace() {
        return new Saying(10, "hej");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/request_public_info")
    public UserData requestPublicInfo(@QueryParam("access_token") String accessToken) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080").path("authorization/validate_token").queryParam("access_token", accessToken);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
        TokenValidation validation = response.readEntity(TokenValidation.class);

        if (!validation.isValid()) throw new AuthorizationDeniedException();

        return resourceDatabase.getData(validation.getUserID());
    }

}
