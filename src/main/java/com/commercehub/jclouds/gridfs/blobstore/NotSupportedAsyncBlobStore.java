package com.commercehub.jclouds.gridfs.blobstore;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.domain.Location;
import org.jclouds.javax.annotation.Nullable;

import java.util.Set;

class NotSupportedAsyncBlobStore implements AsyncBlobStore {
    // TODO: remove this class when async binding is no longer required

    @Override
    public BlobStoreContext getContext() {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public BlobBuilder blobBuilder(String name) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Set<? extends Location>> listAssignableLocations() {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<PageSet<? extends StorageMetadata>> list() {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Boolean> containerExists(String container) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Boolean> createContainerInLocation(@Nullable Location location, String container) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Boolean> createContainerInLocation(@Nullable Location location, String container, CreateContainerOptions options) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<PageSet<? extends StorageMetadata>> list(String container) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<PageSet<? extends StorageMetadata>> list(String container, ListContainerOptions options) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Void> clearContainer(String container) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Void> clearContainer(String container, ListContainerOptions options) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Void> deleteContainer(String container) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Boolean> deleteContainerIfEmpty(String container) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Boolean> directoryExists(String container, String directory) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Void> createDirectory(String container, String directory) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Void> deleteDirectory(String containerName, String name) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Boolean> blobExists(String container, String name) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<String> putBlob(String container, Blob blob) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<String> putBlob(String container, Blob blob, PutOptions options) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Blob> getBlob(String container, String key) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Blob> getBlob(String container, String key, GetOptions options) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Void> removeBlob(String container, String key) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Long> countBlobs(String container) {
        throw new UnsupportedOperationException("async is not supported");
    }

    @Override
    public ListenableFuture<Long> countBlobs(String container, ListContainerOptions options) {
        throw new UnsupportedOperationException("async is not supported");
    }
}
