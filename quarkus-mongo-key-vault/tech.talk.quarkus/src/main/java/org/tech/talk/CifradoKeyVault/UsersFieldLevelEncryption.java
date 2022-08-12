package org.tech.talk.CifradoKeyVault;

import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.model.vault.EncryptOptions;
import com.mongodb.client.vault.ClientEncryption;
import io.quarkus.logging.Log;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.*;
import org.bson.json.JsonWriterSettings;
import org.bson.types.Binary;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.Updates.combine;
import static java.util.Collections.singletonList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApplicationScoped
public class UsersFieldLevelEncryption {

    @Inject
    MongoRepository mongoService;

    private static final int SIZE_MASTER_KEY = 96;
    private static final String DETERMINISTIC = "AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic";
    private static final String RANDOM = "AEAD_AES_256_CBC_HMAC_SHA_512-Random";
    private static final String LOCAL = "local";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String MASTER_KEY_FILENAME = "master_key_main.txt";
    private static final MongoNamespace ENCRYPTED_NS = new MongoNamespace("Customers", "users");
    private static final JsonWriterSettings INDENT = JsonWriterSettings.builder().indent(true).build();

    private ConnectionHelper connectionHelper;
    private ClientEncryption encryption;

    private MongoCollection<Document> vaultColl;
    private MongoCollection<Document> usersColl;




    private void makeConnectionDB(){
        this.connectionHelper = new ConnectionHelper(getKeyMaster());
        this.encryption = this.connectionHelper.getEncryptionClient();
        MongoClient client = this.connectionHelper.getMongoClient();
        this.vaultColl = this.connectionHelper.getVaultCollection();
        this.usersColl = client.getDatabase(ENCRYPTED_NS.getDatabaseName())
                .getCollection(ENCRYPTED_NS.getCollectionName());
    }


    public byte[] getKeyMaster_OLD(){
        ConsoleDecoration.printSection("MASTER KEY");
        return generateNewOrRetrieveMasterKeyFromFile(MASTER_KEY_FILENAME);
    }

    public byte[] getKeyMaster(){
        Document data = mongoService.getCollectionKeyMaster().find(eq("Id", 7845)).first();
        Binary key = data.get("keyMaster", Binary.class);
        byte[] keyMaster = key.getData();
        return keyMaster;
    }


    public byte[] createNewKeyMaster(){

        byte [] key = generateMasterKey();
        Document keyMasterSave = new Document()
                .append("fecha", new Date())
                .append("keyMaster", key)
                .append("autor", "Leonardo Gonzalez")
                .append("Id", 7845);

        mongoService.getCollectionKeyMaster().insertOne(keyMasterSave);
       // mongoService.getCollectionCustomer().drop();
       // mongoService.getCollectionVault().drop();

        return key;
    }

    private byte[] generateNewOrRetrieveMasterKeyFromFile(String filename) {
        byte[] masterKey = new byte[SIZE_MASTER_KEY];
        try {
            retrieveMasterKeyFromFile(filename, masterKey);
            System.out.println("An existing Master Key was found in file \"" + filename + "\".");
        } catch (IOException e) {
            masterKey = generateMasterKey();
            saveMasterKeyToFile(filename, masterKey);
            System.out.println("A new Master Key has been generated and saved to file \"" + filename + "\".");
        }
        return masterKey;
    }

    private void retrieveMasterKeyFromFile(String filename, byte[] masterKey) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            fis.read(masterKey, 0, SIZE_MASTER_KEY);
        }
    }


    private byte[] generateMasterKey() {
        byte[] masterKey = new byte[SIZE_MASTER_KEY];
        SECURE_RANDOM.nextBytes(masterKey);
        return masterKey;
    }

    private void saveMasterKeyToFile(String filename, byte[] masterKey) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(masterKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearCluster(ConnectionHelper connectionHelper){
        connectionHelper.cleanCluster();
    }

    private void createKeyAltNamesUniqueIndex(MongoCollection<Document> vaultColl) {
        vaultColl.createIndex(ascending("keyAltNames"),
                new IndexOptions().unique(true).partialFilterExpression(exists("keyAltNames")));
    }

    private EncryptOptions deterministic(String keyAltName) {
        return new EncryptOptions(DETERMINISTIC).keyAltName(keyAltName);
    }

    private EncryptOptions random(String keyAltName) {
        return new EncryptOptions(RANDOM).keyAltName(keyAltName);
    }

    private DataKeyOptions keyAltName(String altName) {
        return new DataKeyOptions().keyAltNames(singletonList(altName));
    }

    private void createKeys(ClientEncryption encryption, String IdKeyAltName) {
        BsonBinary newKeyId = encryption.createDataKey(LOCAL, keyAltName(IdKeyAltName));
        Log.infof("Created new data key ID: " + newKeyId.asUuid());
    }

    private void createDataEncrypt(ClientEncryption encryption, String ID, String name, int age, String blood_type) {
        BsonBinary ageDoc = encryption.encrypt(new BsonString(age+""), deterministic(ID));
        BsonBinary bloodTypeDoc = encryption.encrypt(new BsonString(blood_type), random(ID));

        BsonDocument medicalLocation = new BsonDocument("Hospital", new BsonString("Clinica Central")).append("Direccion", new BsonString("Cr 23 #56-21"));
        BsonBinary medicalRecord = encryption.encrypt(new BsonArray(singletonList(medicalLocation)), random(ID));

        Document dataUser =  new Document("ID", ID)
                .append("nombre", name)
                .append("edad", ageDoc)
                .append("tipoSangre", bloodTypeDoc)
                .append("centroMédico", medicalRecord);

        usersColl.insertOne(dataUser);
        Log.infof("Data inserted sucessfull");
    }

    private void createDataNormal(String ID, String name, int age, String blood_type) {
        BsonDocument medicalLocation = new BsonDocument("Hospital", new BsonString("Clinica Central")).append("Direccion", new BsonString("Cr 23 #56-21"));

        Document dataUser =  new Document("ID", ID)
                .append("nombre", name)
                .append("edad", age)
                .append("tipoSangre", blood_type)
                .append("centroMédico", medicalLocation);

        usersColl.insertOne(dataUser);
        Log.infof("Data inserted sucessfull");
    }

    private void makeHeaderEncrypt(){
        ConsoleDecoration.printSection("CREATE KEY ALT NAMES UNIQUE INDEX");
        createKeyAltNamesUniqueIndex(vaultColl);
    }

    public void insertDataEncrypt(String ID, String name, int age, String blood_type){
        //MAKE CONNECTION
        makeConnectionDB();

        //CREATE KEY ALT NAMES UNIQUE INDEX
        makeHeaderEncrypt();

        //CREATE DATA ENCRYPTION KEYS
        ConsoleDecoration.printSection("CREATE DATA ENCRYPTION KEYS");
        createKeys(encryption, ID);

        //INSERT ENCRYPTED DOCUMENTS
        ConsoleDecoration.printSection("INSERT ENCRYPTED DOCUMENTS");
        createDataEncrypt(encryption, ID, name, age, blood_type);

     //   encryption.close();
    }

    public void insertDataNoEncrypt(String ID, String name, int age, String blood_type){
        makeConnectionDB();
        //INSERT NORMAL DOCUMENTS
        ConsoleDecoration.printSection("INSERT NORMAL DOCUMENTS");
        createDataNormal(ID, name, age, blood_type);

      //  encryption.close();
    }

    public Document getDataUserID(String ID){
        makeConnectionDB();
        Document dataQuery = usersColl.find(combine(eq("ID", ID))).first();
        String doc = dataQuery.toJson(INDENT);

        Log.infof("@getDataUserID, ID de paciente consultado %s ---> %s", ID, doc);

       // encryption.close();

        return dataQuery;
    }

    public ArrayList<Document> getAllData(){
        makeConnectionDB();
        ArrayList<Document> results = usersColl.find().into(new ArrayList<>());
        String allData = "";
        for (Document data : results ) {
            allData += data.toJson(INDENT) + "\n";
        }
        Log.infof("@getDataUserID, listado de pacientes %s", allData);

       // encryption.close();

        return results;
    }

    public void updateDataEncrypt(String ID, String name, int age, String blood_type){
        makeConnectionDB();
        BsonBinary nameEncryp = encryption.encrypt(new BsonString(name+""), deterministic(ID));

        Document setData = new Document()
                .append("nombre", nameEncryp)
                .append("edad", age)
                .append("tipoSangre", blood_type);

        Document update = new Document();
        update.append("$set", setData);

        long numUpdate = usersColl.updateOne( combine(eq("ID", ID)), update ).getMatchedCount();
        Log.infof("@updateDataEncrypt, Paciente ID %s cantidad %s ", ID,numUpdate);

      //  encryption.close();
    }


    public void deleteData(String ID){

        makeConnectionDB();
        long deletedCount = vaultColl.deleteOne(eq("keyAltNames", ID)).getDeletedCount();
        Log.infof("@deleteData, vaultColl ---> Paciente ID %s cantidad %s ", ID, deletedCount);

        long numDelete = usersColl.deleteMany(eq("ID", ID)).getDeletedCount();
        Log.infof("@deleteData, usersColl ---> Paciente ID %s cantidad %s ", ID,numDelete);

       // encryption.close();
    }

}
