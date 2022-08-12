package org.tech.talk.CifradoKeyVault;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApplicationScoped
public class ConnectionHelper {


    // https://github.com/mongodb-developer/java-quick-start/blob/master/src/main/java/com/mongodb/quickstart/csfle/ClientSideFieldLevelEncryption.java

  /*  @ConfigProperty(name = "propiedades.conection.mongoDb")
    String URL_MONGODB;
*/
    private static final Logger LOG = Logger.getLogger(ConnectionHelper.class);

    ConnectionString CONNECTION_STR = new ConnectionString("mongodb+srv://cloud-db-mongodb:sn_VGZbXsjkTP4_@cluster0.ipvfkae.mongodb.net/test");

    private static final MongoNamespace VAULT_NS = new MongoNamespace("Encryption", "vault");
    private static final MongoNamespace ENCRYPTED_NS = new MongoNamespace("Customers", "users");
    private static final String LOCAL = "local";
    private  Map<String, Map<String, Object>> kmsProviders;
    private  ClientEncryption encryption;
    private MongoClient client;

    public ConnectionHelper(byte[] masterKey) {
        LOG.infof("INITIALIZATION");
        this.kmsProviders = generateKmsProviders(masterKey);
        this.encryption = createEncryptionClient();
        this.client = createMongoClient();
    }

    private MongoClient createMongoClient() {
        AutoEncryptionSettings aes = AutoEncryptionSettings.builder()
                .keyVaultNamespace(VAULT_NS.getFullName())
                .kmsProviders(kmsProviders)
                .bypassAutoEncryption(true)
                .build();
        MongoClientSettings mcs = MongoClientSettings.builder()
                .applyConnectionString(CONNECTION_STR)
                .autoEncryptionSettings(aes)
                .build();
        LOG.infof("=> Creating MongoDB client with automatic decryption.");
        return MongoClients.create(mcs);
    }

    private ClientEncryption createEncryptionClient() {
        MongoClientSettings kvmcs = MongoClientSettings.builder().applyConnectionString(CONNECTION_STR).build();
        ClientEncryptionSettings ces = ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(kvmcs)
                .keyVaultNamespace(VAULT_NS.getFullName())
                .kmsProviders(kmsProviders)
                .build();
        LOG.infof("=> Creating encryption client.");
        return ClientEncryptions.create(ces);
    }

    public MongoClient resetMongoClient() {
        client.close();
        try {
            // sleep to make sure we are not reusing the Data Encryption Key Cache.
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return client = createMongoClient();
    }

    public MongoCollection<Document> getVaultCollection() {
        return client.getDatabase(VAULT_NS.getDatabaseName()).getCollection(VAULT_NS.getCollectionName());
    }

    public ClientEncryption getEncryptionClient() {
        return encryption;
    }

    public MongoClient getMongoClient() {
        return client;
    }

    public void cleanCluster() {
        LOG.infof("=> Cleaning entire cluster.");
        client.getDatabase(VAULT_NS.getDatabaseName()).drop();
        client.getDatabase(ENCRYPTED_NS.getDatabaseName()).drop();
    }

    private Map<String, Map<String, Object>> generateKmsProviders(byte[] masterKey) {
        LOG.infof("=> Creating local Key Management System using the master key.");
        return new HashMap<String, Map<String, Object>>() {{
            put(LOCAL, new HashMap<String, Object>() {{
                put("key", masterKey);
            }});
        }};
    }

    public void closeConnections() {
        encryption.close();
        client.close();
    }


}
