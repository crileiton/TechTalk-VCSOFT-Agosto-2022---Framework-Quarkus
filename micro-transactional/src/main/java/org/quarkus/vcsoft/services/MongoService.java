package org.quarkus.vcsoft.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MongoService {

    @Inject
    MongoClient mongoClient;

    private static final Logger LOG = Logger.getLogger(MongoService.class);

    public MongoCollection<Document> getCollectionEstudiante() {
        LOG.infof("@getCollectionEstudiante > Inicia onexion a la base de datos TalkVcsoft " +
                "coleccion Estudiante");
        MongoCollection<Document> collection = mongoClient.getDatabase("TalkVcsoft").getCollection("Estudiante");
        LOG.infof("@getCollectionEstudiante > Se obtiene conexión a la base de datos TalkVcsoft" +
                " coleccion Estudiante");
        return collection;
    }

    public MongoCollection<Document> getCollectionResultados() {
        LOG.infof("@getCollectionEstudiante > Inicia onexion a la base de datos TalkVcsoft " +
                "coleccion Resultado");
        MongoCollection<Document> collection = mongoClient.getDatabase("TalkVcsoft").getCollection("Resultado");
        LOG.infof("@getCollectionEstudiante > Se obtiene conexión a la base de datos TalkVcsoft" +
                " coleccion Resultado");
        return collection;
    }

}
