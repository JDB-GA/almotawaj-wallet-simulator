package com.almotawaj.wallet.config.constants;

import com.almotawaj.wallet.common.model.FileType;
import com.almotawaj.wallet.kyc.model.KycApplicationStatus;

import java.util.List;
import java.util.Set;

public final class KycConstants {
    public static final long MAX_FILE_BYTES = 5L * 1024 * 1024;
    public static final int MINIMUM_AGE_YEARS = 18;
    public static final Set<FileType> IDENTITY_FILE_TYPES = Set.of(FileType.PDF);
    public static final Set<FileType> PHOTO_FILE_TYPES = Set.of(FileType.JPEG, FileType.PNG);
    public static final List<KycApplicationStatus> CPR_BLOCKING_STATUSES =
            List.of(KycApplicationStatus.PENDING, KycApplicationStatus.APPROVED);
    public static final String STORAGE_ROOT_PROPERTY = "${storage-root}";

    private KycConstants() {
    }
}
