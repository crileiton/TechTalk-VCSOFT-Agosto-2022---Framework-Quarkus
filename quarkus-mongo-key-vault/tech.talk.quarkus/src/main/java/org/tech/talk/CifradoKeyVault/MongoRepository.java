package org.tech.talk.CifradoKeyVault;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MongoRepository {


    @ConfigProperty(name = "propiedades.conection.mongoDb")
    String conectionMongoDB;


   // private static String conectionMongoDB ="mongodb+srv://leonardogonzalez:IngLeoVCS@cluster0.jf11b.mongodb.net/test";

    public MongoCollection<Document> getCollectionKeyMaster() {
        MongoClient mongoClient = MongoClients.create(conectionMongoDB);
        MongoDatabase database = mongoClient.getDatabase("Encryption");
        MongoCollection<Document> collection = database.getCollection("KeyMaster");
        return  collection;
    }

    public MongoCollection<Document> getCollectionVault() {
        MongoClient mongoClient = MongoClients.create(conectionMongoDB);
        MongoDatabase database = mongoClient.getDatabase("Encryption");
        MongoCollection<Document> collection = database.getCollection("vault");
        return  collection;
    }

    public MongoCollection<Document> getCollectionCustomer() {
        MongoClient mongoClient = MongoClients.create(conectionMongoDB);
        MongoDatabase database = mongoClient.getDatabase("Customers");
        MongoCollection<Document> collection = database.getCollection("users");
        return  collection;
    }


}
