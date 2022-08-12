package org.tech.talk.CifradoKeyVault;

import org.bson.Document;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/laboratorio-field-encrypt")
public class FieldEncryptApi {

    private static final Logger LOG = Logger.getLogger(FieldEncryptApi.class);

    @Inject
    UsersFieldLevelEncryption fieldEncyp;

    @GET
    @Path("/create-key-manager")
    @Produces(MediaType.APPLICATION_JSON)
    public Document createKeyManager(){

        LOG.infof("------------------------>>> Inicia creación de llave<<<<---------------------");
      ///  byte[] masterKey = fieldEncyp.getKeyMaster();

        byte[] masterKey = fieldEncyp.createNewKeyMaster();
        String text = new String(masterKey);
        LOG.infof("Master Key: " + text);
        return new Document().append("option", "@createKeyManager, Master Key: "+text);
    }

    @GET
    @Path("/get-key-manager")
    @Produces(MediaType.APPLICATION_JSON)
    public Document getKeyManager(){

        LOG.infof("------------------------>>> Inicia obtención de llave<<<<---------------------");
        byte[] masterKey = fieldEncyp.getKeyMaster();

       // byte[] masterKey = fieldEncyp.getKeyMasterMongo();

        String text = new String(masterKey);
        LOG.infof("Master Key: " + text);
        return new Document().append("option", "@getKeyManager, Master Key: "+text);
    }

    @POST
    @Path("/insert-data-to-encrypted")
    @Produces(MediaType.APPLICATION_JSON)
    public Document insertDataToEncrypted(
            @QueryParam("ID") String ID,
            @QueryParam("name") String name,
            @QueryParam("age") int age,
            @QueryParam("blood_type") String blood_type){

        fieldEncyp.insertDataEncrypt(ID, name, age, blood_type);
        return new Document().append("option", "@insertDataToEncrypted "+name);
    }

    @POST
    @Path("/insert-data-normal")
    @Produces(MediaType.APPLICATION_JSON)
    public Document insertDataNormal(
            @QueryParam("ID") String ID,
            @QueryParam("name") String name,
            @QueryParam("age") int age,
            @QueryParam("blood_type") String blood_type
    ){

        fieldEncyp.insertDataNoEncrypt(ID, name, age, blood_type);
        return new Document().append("option", "@insertDataNormal "+name);
    }

    @GET
    @Path("/get-data-user")
    @Produces(MediaType.APPLICATION_JSON)
    public Document getDataUser(@QueryParam("ID") String ID){

        Document data = fieldEncyp.getDataUserID(ID);
        return new Document().append("option", "@getDataUser "+data.toString());
    }

    @GET
    @Path("/get-data-all-users")
    @Produces(MediaType.APPLICATION_JSON)
    public Document getDataAllUsers(){
        ArrayList<Document> data = fieldEncyp.getAllData();
        return new Document().append("option", "@getDataAllUsers "+data.toString());
    }

    @PUT
    @Path("/update-data-encrypted")
    @Produces(MediaType.APPLICATION_JSON)
    public Document upateDataEncrypted(
            @QueryParam("ID") String ID,
            @QueryParam("name") String name,
            @QueryParam("age") int age,
            @QueryParam("blood_type") String blood_type
    ){

        fieldEncyp.updateDataEncrypt(ID, name, age, blood_type);
        return new Document().append("option", "@upateDataEncrypted "+name);
    }

    @DELETE
    @Path("/delete-data")
    @Produces(MediaType.APPLICATION_JSON)
    public Document deleteData(
            @QueryParam("ID") String ID
    ){
        fieldEncyp.deleteData(ID);
        return new Document().append("option", "@deleteData "+ID);
    }

}
