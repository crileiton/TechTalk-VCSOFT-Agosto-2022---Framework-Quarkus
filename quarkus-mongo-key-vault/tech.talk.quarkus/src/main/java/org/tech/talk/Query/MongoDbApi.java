package org.tech.talk.Query;

import org.bson.Document;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.ArrayList;

@Path("/laboratorio")
public class MongoDbApi {

   // private static final Logger LOG = Logger.getLogger(GreetingResource.class);

    @Inject
    MongoService mongoService;

    @GET
    @Path("/conexion-airbnb")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Document> conexionNormal(){
            return mongoService.getDataAirBnb();
    }

    @GET
    @Path("/conexion-mflix")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Document> conexionDatamflix() throws ParseException {

        return mongoService.getDatamflix();
    }
}
