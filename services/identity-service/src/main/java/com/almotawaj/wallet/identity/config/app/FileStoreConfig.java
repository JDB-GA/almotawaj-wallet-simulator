package com.almotawaj.wallet.identity.config.app;

import com.almotawaj.wallet.identity.common.service.DiskFileStore;
import com.almotawaj.wallet.identity.common.service.FileStore;
import com.almotawaj.wallet.identity.common.service.S3FileStore;
import com.almotawaj.wallet.identity.config.constants.KycConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

@Configuration
public class FileStoreConfig {
    @Bean
    public FileStore fileStore(@Value(KycConstants.STORAGE_BUCKET_PROPERTY) String bucket,
                               @Value(KycConstants.STORAGE_REGION_PROPERTY) String region,
                               @Value(KycConstants.STORAGE_ROOT_PROPERTY) String root) throws IOException {
        if (bucket.isBlank()) {
            return new DiskFileStore(root);
        }
        return new S3FileStore(S3Client.builder().region(Region.of(region)).build(), bucket);
    }
}
