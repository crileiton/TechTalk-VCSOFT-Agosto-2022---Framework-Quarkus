package org.tech.talk.GridFs;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/laboratorio")
public class GridFSApi {

    @Inject
    GridFsService gridFsService;

    @GET
    @Path("/gridfs-text")
    @Produces(MediaType.TEXT_PLAIN)
    public String gridfs_text() throws IOException {

        return gridFsService.saveFile();
    }

    @GET
    @Path("/gridfs-zip")
    @Produces(MediaType.TEXT_PLAIN)
    public String gridfs_zip() throws IOException {

        String idFile = gridFsService.saveZip();

        return "Ok :) IdFile load  "+idFile;
    }
}