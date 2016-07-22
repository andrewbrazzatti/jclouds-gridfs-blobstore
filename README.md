# THIS PROJECT IS DEPRECATED
jclouds-gridfs-blobstore is no longer maintained, and **this** repository will be removed from GitHub on or after Sunday, January 22, 2017. Published release artifacts will **continue** to be available indefinitely via
[Bintray JCenter](https://bintray.com/bintray/jcenter?filterByPkgName=jclouds-gridfs-blobstore).

# Overview

A JClouds BlobStore provider backed by MongoDB's GridFS.  Not all capabilities are supported, but there should be
enough for common use cases.

# Usage

First, add a dependency to your build file.  Releases are published to
[Bintray JCenter](https://bintray.com/bintray/jcenter).  See the [changelog](CHANGES.md) for the latest version.

Gradle:

```groovy
repositories {
    jcenter()
}
dependencies {
    compile "com.commercehub.jclouds:jclouds-gridfs-blobstore:1.0.0"
}
```

Maven:

```xml
<dependency>
    <groupId>com.commercehub.jclouds</groupId>
    <artifactId>jclouds-gridfs-blobstore</artifactId>
    <version>1.0.1</version>
</dependency>
```

Next, obtain an instance of the `BlobStore` using a
[standard MongoDB connection string](http://docs.mongodb.org/manual/reference/connection-string/):

```java
Properties overrides = new Properties();
overrides.setProperty(Constants.PROPERTY_ENDPOINT, "mongodb://my_mongo_server:27017/?maxPoolSize=50");
BlobStoreContext context = ContextBuilder.newBuilder("gridfs").overrides(overrides)
    .buildView(BlobStoreContext.class);
BlobStore blobStore = context.getBlobStore();
```

You can also use a non-standard connection string (deprecated):

```java
overrides.setProperty(Constants.PROPERTY_ENDPOINT, "gridfs://my_mongo_server:27017");
```

To use a replica set when using a non-standard connection string, specify additional members as a comma or
semicolon-separated list, like this:

```java
overrides.setProperty(Constants.PROPERTY_ENDPOINT, "gridfs://node1:27017;node2:27017;node3:27017");
```

Then, use the blob store to put/get blobs.  The container is in the format **DB**[/**BUCKET**], where **DB** is the
name of the database and **BUCKET** is the optional name of the GridFS bucket (which defaults to `fs`).

```java
blobStore.createContainerInLocation(null, "blobStore");
Blob blobToPut = blobStore.blobBuilder("myBlob").payload("myPayload").build();
blobStore.putBlob("blobStore", blob);
Blob blobFromGet = blobStore.getBlob("blobStore", "myBlob");
```

Finally, close the context to allow it to properly clean up.

```java
context.close();
```

# Development

## Releasing
Releases are uploaded to [Bintray](https://bintray.com/) via the
[gradle-release](https://github.com/townsfolk/gradle-release) plugin and
[gradle-bintray-plugin](https://github.com/bintray/gradle-bintray-plugin). To upload a new release, you need to be a
member of the [commercehub-oss Bintray organization](https://bintray.com/commercehub-oss). You need to specify your
Bintray username and API key when uploading. Your API key can be found on your
[Bintray user profile page](https://bintray.com/profile/edit). You can put your username and API key in
`~/.gradle/gradle.properties` like so:

    bintrayUserName = johndoe
    bintrayApiKey = 0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef

Then, to upload the release:

    ./gradlew release

Alternatively, you can specify your Bintray username and API key on the command line:

    ./gradlew -PbintrayUserName=johndoe -PbintrayApiKey=0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef release

The `release` task will prompt you to enter the version to be released, and will create and push a release tag for the
specified version. It will also upload the release artifacts to Bintray.

After the release artifacts have been uploaded to Bintray, they must be published to become visible to users. See
Bintray's [Publishing](https://bintray.com/docs/uploads/uploads_publishing.html) documentation for instructions.

After publishing the release on Bintray, it's also nice to create a GitHub release. To do so:
*   Visit the project's [releases](https://github.com/commercehub-oss/jclouds-gridfs-blobstore/releases) page
*   Click the "Draft a new release" button
*   Select the tag that was created by the Gradle `release` task
*   Enter a title; typically, this should match the tag (e.g. "1.2.0")
*   Enter a description of what changed since the previous release (see the
    [changelog](https://github.com/commercehub-oss/jclouds-gridfs-blobstore/blob/master/CHANGES.md))
*   Click the "Publish release" button
