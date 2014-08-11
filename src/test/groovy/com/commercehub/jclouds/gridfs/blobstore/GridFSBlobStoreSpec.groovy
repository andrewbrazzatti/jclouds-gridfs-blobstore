package com.commercehub.jclouds.gridfs.blobstore

import com.google.common.io.ByteSource
import com.mongodb.Mongo
import com.mongodb.MongoClient
import org.jclouds.Constants
import org.jclouds.ContextBuilder
import org.jclouds.blobstore.BlobStore
import org.jclouds.blobstore.BlobStoreContext
import org.jclouds.blobstore.ContainerNotFoundException
import org.jclouds.blobstore.domain.StorageType
import org.jclouds.blobstore.options.CreateContainerOptions
import org.jclouds.blobstore.options.GetOptions
import org.jclouds.blobstore.options.ListContainerOptions
import org.jclouds.blobstore.options.PutOptions
import spock.lang.Shared
import spock.lang.Specification

class GridFSBlobStoreSpec extends Specification {
    private static final DB_NAME = this.simpleName
    private static final BUCKET = "bk1"
    private static final CONTAINER = "${DB_NAME}/${BUCKET}"
    private static final BLOB_NAME = "JabbaTheHutt"
    private static final PAYLOAD = ByteSource.wrap("Random data".bytes)
    private static final PAYLOAD_MD5 = "fd6073b6d8ba3a3c1ab5316b9c79e12b"
    private static final CONTENT_TYPE = "text/plain"

    @Shared
    private Mongo mongo
    @Shared
    private BlobStoreContext context
    @Shared
    private BlobStore blobStore

    def setupSpec() {
        String host = "localhost"
        int port = 27017
        mongo = new MongoClient(host, port)
        mongo.getDB(DB_NAME).dropDatabase()
        // TODO: use embedded mongo
        def overrides = new Properties()
        overrides.setProperty(Constants.PROPERTY_ENDPOINT, "mongodb://${host}:${port}");
        context = ContextBuilder.newBuilder("gridfs")
            .overrides(overrides)
            .buildView(BlobStoreContext)
        blobStore = context.getBlobStore()
    }

    def cleanupSpec() {
        if (context) {
            context.close()
        }
        if (mongo) {
            mongo.getDB(DB_NAME).dropDatabase()
            mongo.close()
        }
    }

    def "can create and delete containers without create container options"() {
        assert !blobStore.containerExists(CONTAINER)

        expect:
        blobStore.createContainerInLocation(null, CONTAINER)
        blobStore.containerExists(CONTAINER)
        !blobStore.createContainerInLocation(null, CONTAINER)
        blobStore.deleteContainer(CONTAINER)
        !blobStore.containerExists(CONTAINER)
    }

    def "can create and delete containers with NONE create container options"() {
        assert !blobStore.containerExists(CONTAINER)

        expect:
        blobStore.createContainerInLocation(null, CONTAINER, CreateContainerOptions.NONE)
        blobStore.containerExists(CONTAINER)
        !blobStore.createContainerInLocation(null, CONTAINER, CreateContainerOptions.NONE)
        blobStore.deleteContainer(CONTAINER)
        !blobStore.containerExists(CONTAINER)
    }

    def "delete container if empty"() {
        assert !blobStore.containerExists(CONTAINER)

        when:
            def payload = blobStore.blobBuilder(BLOB_NAME).payload(PAYLOAD).build()
            blobStore.putBlob(CONTAINER, payload) == PAYLOAD_MD5
            def containerDeleted = blobStore.deleteContainerIfEmpty(CONTAINER)

        then:
            !containerDeleted
            blobStore.containerExists(CONTAINER)
            blobStore.blobExists(CONTAINER, BLOB_NAME)

        when:
            blobStore.removeBlob(CONTAINER, BLOB_NAME)
            containerDeleted = blobStore.deleteContainerIfEmpty(CONTAINER)

        then:
            containerDeleted
            !blobStore.containerExists(CONTAINER)
            !blobStore.blobExists(CONTAINER, BLOB_NAME)
    }

    def "doesn't allow creating containers with public read"() {
        when:
        blobStore.createContainerInLocation(null, CONTAINER, CreateContainerOptions.Builder.publicRead())

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains("public read is not supported")
    }

    def "can create and delete blobs without put options"() {
        assert !blobStore.blobExists(CONTAINER, BLOB_NAME)
        // TODO: test put when container doesn't exist
        // TODO: test remove when container doesn't exist
        // TODO: test put to overwrite
        // TODO: test get blob, payloads and metadata

        expect:
        def payload = blobStore.blobBuilder(BLOB_NAME).payload(PAYLOAD).build()
        blobStore.putBlob(CONTAINER, payload) == PAYLOAD_MD5
        blobStore.blobExists(CONTAINER, BLOB_NAME)

        when:
        blobStore.removeBlob(CONTAINER, BLOB_NAME)

        then:
        !blobStore.blobExists(CONTAINER, BLOB_NAME)
    }

    def "can create and delete blobs with multipart put options"() {
        assert !blobStore.blobExists(CONTAINER, BLOB_NAME)

        expect:
        def payload = blobStore.blobBuilder(BLOB_NAME).payload(PAYLOAD).build()
        blobStore.putBlob(CONTAINER, payload, PutOptions.Builder.multipart()) == PAYLOAD_MD5
        blobStore.blobExists(CONTAINER, BLOB_NAME)

        when:
        blobStore.removeBlob(CONTAINER, BLOB_NAME)

        then:
        !blobStore.blobExists(CONTAINER, BLOB_NAME)
    }

    def "doesn't allow put with null payload"() {
        when:
        blobStore.putBlob(CONTAINER, blobStore.blobBuilder(BLOB_NAME).build())

        then:
        thrown(NullPointerException)
    }

    def "doesn't allow put with non-multipart"() {
        when:
        def payload = blobStore.blobBuilder(BLOB_NAME).payload(PAYLOAD).build()
        blobStore.putBlob(CONTAINER, payload, PutOptions.Builder.multipart(false))

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains("only multipart is supported")
    }

    def "get from non-existent container throws exception"() {
        when:
        blobStore.getBlob("containerDoesNotExist", "blobDoesNotExist")

        then:
        thrown(ContainerNotFoundException)
    }

    def "get non-existent blob returns null"() {
        expect:
        blobStore.getBlob(CONTAINER, "blobDoesNotExist") == null
    }

    def "can retrieve blob without get options"() {
        when:
        def payload = blobStore.blobBuilder(BLOB_NAME).payload(PAYLOAD).contentType(CONTENT_TYPE)
                .userMetadata([user: "joe", type: "profilePicture"]).build()
        blobStore.putBlob(CONTAINER, payload)
        def blob = blobStore.getBlob(CONTAINER, BLOB_NAME)

        then:
        blob != null

        blob.allHeaders != null && blob.allHeaders.isEmpty()
        blob.metadata != null
        blob.metadata.container == CONTAINER
        blob.metadata.contentMetadata != null
        blob.metadata.contentMetadata.contentDisposition == null
        blob.metadata.contentMetadata.contentEncoding == null
        blob.metadata.contentMetadata.contentLanguage == null
        blob.metadata.contentMetadata.contentLength == PAYLOAD.size()
        blob.metadata.contentMetadata.contentMD5AsHashCode == null
        blob.metadata.contentMetadata.contentType == CONTENT_TYPE
        blob.metadata.contentMetadata.expires == null
        blob.metadata.creationDate == null
        blob.metadata.ETag == PAYLOAD_MD5
        blob.metadata.lastModified != null
        blob.metadata.name == BLOB_NAME
        blob.metadata.providerId == null
        blob.metadata.publicUri == null
        blob.metadata.type == StorageType.BLOB
        blob.metadata.uri == null
        blob.metadata.userMetadata == [user: "joe", type: "profilePicture"]

        blob.payload != null
        blob.payload.contentMetadata != null
        blob.payload.contentMetadata.contentDisposition == null
        blob.payload.contentMetadata.contentEncoding == null
        blob.payload.contentMetadata.contentLanguage == null
        blob.payload.contentMetadata.contentLength == PAYLOAD.size()
        blob.payload.contentMetadata.contentMD5AsHashCode == null
        blob.payload.contentMetadata.contentType == CONTENT_TYPE
        blob.payload.contentMetadata.expires == null
        blob.payload.openStream().bytes == PAYLOAD.read()

        cleanup:
        blob.payload.release()
    }

    def "get options not supported"() {
        when:
        blobStore.getBlob(CONTAINER, BLOB_NAME, GetOptions.Builder.ifETagDoesntMatch(PAYLOAD_MD5))

        then:
        thrown(IllegalArgumentException)

        when:
        blobStore.getBlob(CONTAINER, BLOB_NAME, GetOptions.Builder.ifETagMatches(PAYLOAD_MD5))

        then:
        thrown(IllegalArgumentException)

        when:
        blobStore.getBlob(CONTAINER, BLOB_NAME, GetOptions.Builder.ifModifiedSince(new Date()))

        then:
        thrown(IllegalArgumentException)

        when:
        blobStore.getBlob(CONTAINER, BLOB_NAME, GetOptions.Builder.ifUnmodifiedSince(new Date()))

        then:
        thrown(IllegalArgumentException)

        when:
        blobStore.getBlob(CONTAINER, BLOB_NAME, GetOptions.Builder.range(0, 100))

        then:
        thrown(IllegalArgumentException)
    }

    def "list not supported"() {
        when:
        blobStore.list()
        then:
        thrown(UnsupportedOperationException)

        when:
        blobStore.list(CONTAINER)
        then:
        thrown(UnsupportedOperationException)

        when:
        blobStore.list(CONTAINER, ListContainerOptions.NONE)
        then:
        thrown(UnsupportedOperationException)
    }

    def "clearContainer not supported"() {
        when:
        blobStore.clearContainer(CONTAINER)
        then:
        thrown(UnsupportedOperationException)

        when:
        blobStore.clearContainer(CONTAINER, ListContainerOptions.NONE)
        then:
        thrown(UnsupportedOperationException)
    }

    def "directory operations not supported"() {
        when:
        blobStore.createDirectory(CONTAINER, "myDirectory")
        then:
        thrown(UnsupportedOperationException)

        when:
        blobStore.deleteDirectory(CONTAINER, "myDirectory")
        then:
        thrown(UnsupportedOperationException)

        when:
        blobStore.directoryExists(CONTAINER, "myDirectory")
        then:
        thrown(UnsupportedOperationException)
    }

    def "blobMetadata not supported"() {
        when:
        blobStore.blobMetadata(CONTAINER, BLOB_NAME)
        then:
        thrown(UnsupportedOperationException)
    }

    def "countBlobs not supported"() {
        when:
        blobStore.countBlobs(CONTAINER)
        then:
        thrown(UnsupportedOperationException)

        when:
        blobStore.countBlobs(CONTAINER, ListContainerOptions.NONE)
        then:
        thrown(UnsupportedOperationException)
    }
}
