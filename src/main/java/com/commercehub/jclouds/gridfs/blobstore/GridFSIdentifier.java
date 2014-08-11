package com.commercehub.jclouds.gridfs.blobstore;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;

class GridFSIdentifier {
    private final String dbName;
    private final String bucket;

    GridFSIdentifier(String dbName, String bucket) {
        this.dbName = dbName;
        this.bucket = bucket;
    }

    boolean storeExists(Mongo mongo) {
        boolean hasFilesCollection = false;
        boolean hasChunksCollection = false;
        String filesCollectionName = getFilesCollectionName();
        String chunksCollectionName = getChunksCollectionName();
        for (String collectionName : getDB(mongo).getCollectionNames()) {
            if (filesCollectionName.equalsIgnoreCase(collectionName)) {
                hasFilesCollection = true;
            }
            if (chunksCollectionName.equalsIgnoreCase(collectionName)) {
                hasChunksCollection = true;
            }
        }
        return hasFilesCollection && hasChunksCollection;
    }

    GridFS connect(Mongo mongo) {
        return new GridFS(getDB(mongo), bucket);
    }

    void dropStoreCollections(Mongo mongo) {
        DB db = getDB(mongo);
        db.getCollection(getFilesCollectionName()).drop();
        db.getCollection(getChunksCollectionName()).drop();
    }

    boolean dropStoreCollectionsIfEmpty(Mongo mongo) {
        DB db = getDB(mongo);
        DBCollection filesCollection = db.getCollection(getFilesCollectionName());
        long count = filesCollection.count();
        if (count == 0) {
            filesCollection.drop();
            db.getCollection(getChunksCollectionName()).drop();
            return true;
        }
        return false;
    }

    private DB getDB(Mongo mongo) {
        return mongo.getDB(dbName);
    }

    private String getFilesCollectionName() {
        return bucket + ".files";
    }

    private String getChunksCollectionName() {
        return bucket + ".chunks";
    }
}
