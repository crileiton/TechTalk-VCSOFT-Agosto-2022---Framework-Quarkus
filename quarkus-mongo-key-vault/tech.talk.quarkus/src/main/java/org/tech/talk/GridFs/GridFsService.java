package org.tech.talk.GridFs;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;


import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;


// https://github.com/mongodb/mongo-java-driver/blob/master/driver-sync/src/examples/gridfs/GridFSTour.java
//https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/gridfs/#std-label-gridfs-create-bucket

@ApplicationScoped
public class GridFsService {

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "propiedades.dowloadFile.location")
    String saveFile;

    @ConfigProperty(name = "propiedades.loadFile.location")
    String loadFile;

    private static final Logger LOG = Logger.getLogger(GridFsService.class);

    public String saveFile() throws IOException {

        MongoDatabase database = mongoClient.getDatabase("FileStore");
        //database.drop();

        GridFSBucket gridFSBucket = GridFSBuckets.create(database);

        /*
         * UploadFromStream Example
         */
        // Get the input stream
        InputStream streamToUploadFrom = new ByteArrayInputStream("Hello VCSoft! Load data".getBytes(StandardCharsets.UTF_8));

        // Create some custom options
        GridFSUploadOptions options = new GridFSUploadOptions()
                .chunkSizeBytes(1024)
                .metadata(new Document("type", "presentation").append("contentType", "txt"));

        ObjectId fileId = gridFSBucket.uploadFromStream("mongodb-tutorial01", streamToUploadFrom, options);
        streamToUploadFrom.close();
        LOG.infof("The file Id of the uploaded file is: %s", fileId.toHexString());

        /*
         * OpenUploadStream Example
         */

        // Get some data to write
        byte[] data = "Data to upload into GridFS".getBytes(StandardCharsets.UTF_8);


        GridFSUploadStream uploadStream = gridFSBucket.openUploadStream("sampleData");
        uploadStream.write(data);
        uploadStream.close();
        LOG.infof("The fileId of the uploaded file is: %s", uploadStream.getObjectId().toHexString());

        /*
         * Find documents
         */

        LOG.infof("-------- Nombre de los archivos guardados----------");
        gridFSBucket.find().forEach(new Consumer<GridFSFile>() {
            @Override
            public void accept(final GridFSFile gridFSFile) {
                LOG.infof(gridFSFile.getFilename());
            }
        });
        LOG.infof("---------------------------------------------------");

        /*
         * Find documents with a filter
         */
        /*gridFSBucket.find(eq("metadata.contentType", "image/png")).forEach(
                new Consumer<GridFSFile>() {
                    @Override
                    public void accept(final GridFSFile gridFSFile) {
                        System.out.println(gridFSFile.getFilename());
                    }
                });*/

        /*
         * DownloadToStream
         */
        FileOutputStream streamToDownloadTo = new FileOutputStream(saveFile+"mongodb-tutorial01.txt");
        gridFSBucket.downloadToStream(fileId, streamToDownloadTo);
        streamToDownloadTo.close();

        /*
         * DownloadToStreamByName
         */
        streamToDownloadTo = new FileOutputStream(saveFile+"mongodb-tutorial02.txt");
        GridFSDownloadOptions downloadOptions = new GridFSDownloadOptions().revision(0);
        gridFSBucket.downloadToStream("mongodb-tutorial01", streamToDownloadTo, downloadOptions);
        streamToDownloadTo.close();

        /*
         * OpenDownloadStream
         */
        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(fileId);
        int fileLength = (int) downloadStream.getGridFSFile().getLength();
        byte[] bytesToWriteTo = new byte[fileLength];
        downloadStream.read(bytesToWriteTo);
        downloadStream.close();

        LOG.infof("OpenDownloadStream> %s ",new String(bytesToWriteTo, StandardCharsets.UTF_8));

        /*
         * OpenDownloadStreamByName
         */

        downloadStream = gridFSBucket.openDownloadStream("sampleData");
        fileLength = (int) downloadStream.getGridFSFile().getLength();
        bytesToWriteTo = new byte[fileLength];
        downloadStream.read(bytesToWriteTo);
        downloadStream.close();

        LOG.infof("OpenDownloadStreamByName> %s", new String(bytesToWriteTo, StandardCharsets.UTF_8));

        /*
         * Rename
         */
       // gridFSBucket.rename(fileId, "mongodbTutorial-update-name");

        /*
         * Delete
         */
       // gridFSBucket.delete(fileId);

        return fileId.toHexString();
    }


    public String saveZip() throws IOException {

        MongoDatabase database = mongoClient.getDatabase("FileStore");
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);


        Path filePath = Paths.get(loadFile+"UploadFile.zip");
        byte[] data = Files.readAllBytes(filePath);

        GridFSUploadOptions options = new GridFSUploadOptions()
                .chunkSizeBytes(100000)
                .metadata(new Document("type", "zip archive"));

        String idFile = "Nop";

        try (GridFSUploadStream uploadStream = gridFSBucket.openUploadStream("UploadFile_Load.zip", options)) {
            uploadStream.write(data);
            uploadStream.flush();
            idFile = uploadStream.getObjectId().toHexString();

            LOG.infof("The file id of the uploaded file is: %s", uploadStream.getObjectId().toHexString());
        } catch (Exception e) {
            LOG.errorf("The file upload failed: %s" , e);
        }

        GridFSDownloadOptions downloadOptions = new GridFSDownloadOptions().revision(0);
        try (FileOutputStream streamToDownloadTo = new FileOutputStream(saveFile+"UploadFile_Load.zip")) {
            gridFSBucket.downloadToStream("UploadFile_Load.zip", streamToDownloadTo, downloadOptions);
            streamToDownloadTo.flush();
        }

        return idFile;
    }

}
