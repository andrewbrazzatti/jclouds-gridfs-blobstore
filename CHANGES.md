# Changelog

* 1.4.0
    *   Upgrade jclouds-blobstore dependency to version 1.8.0
    *   Upgrade mongo-java-driver dependency to 2.12.3
    *   Upgrade groovy-all test dependency to version 2.3.6

*   1.3.0 (2014-05-15)
    *   Add support for [standard MongoDB connection strings](http://docs.mongodb.org/manual/reference/connection-string/)
    *   Support for non-standard "gridfs://" connection strings is deprecated and may be removed in a future release
    *   Upgrade mongo-java-driver dependency to 2.12.1
    *   Upgrade groovy-all test dependency to version 2.3.0
    *   Upgrade jclouds-blobstore dependency to version 1.7.2

*   1.2.0 (2014-02-17)
    *   Upgrade JClouds to version 1.7.1, in order to support changes between Java versions 1.7.0_45 and 1.7.0_51.  See https://issues.apache.org/jira/browse/JCLOUDS-427

*   1.0.1 (2013-10-24)
    *   Fix IllegalArgumentException when using an endpoint that includes dot characters

*   1.0.0 (2013-10-01)
    *   Add support for `createContainerInLocation`, `containerExists`, `deleteContainer`
    *   Add support for `putBlob`, `blobExists`, `removeBlob`, `getBlob`
