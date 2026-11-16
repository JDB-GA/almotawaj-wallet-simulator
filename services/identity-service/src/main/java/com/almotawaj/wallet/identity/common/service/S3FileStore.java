package com.almotawaj.wallet.identity.common.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;

public class S3FileStore implements FileStore {
    private final S3Client s3;
    private final String bucket;

    public S3FileStore(S3Client s3, String bucket) {
        this.s3 = s3;
        this.bucket = bucket;
    }

    @Override
    public void save(String key, InputStream input, long size) {
        s3.putObject(request -> request.bucket(bucket).key(key), RequestBody.fromInputStream(input, size));
    }

    @Override
    public Resource load(String key) {
        return new ByteArrayResource(s3.getObjectAsBytes(request -> request.bucket(bucket).key(key)).asByteArray());
    }

    @Override
    public void delete(String key) {
        s3.deleteObject(request -> request.bucket(bucket).key(key));
    }
}
