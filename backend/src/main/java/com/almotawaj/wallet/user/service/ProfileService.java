package com.almotawaj.wallet.user.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.common.model.FileType;
import com.almotawaj.wallet.common.service.FileStorageService;
import com.almotawaj.wallet.common.service.FileTypeDetector;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.KycConstants;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.request.UpdateProfileRequest;
import com.almotawaj.wallet.user.model.response.ProfileResponse;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.wallet.model.DocumentContent;
import com.almotawaj.wallet.wallet.service.WalletHolderNames;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final WalletHolderNames names;
    private final FileTypeDetector fileTypeDetector;
    private final FileStorageService fileStorageService;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public ProfileResponse get(UUID userId) {
        User user = find(userId);
        return ProfileResponse.from(user, names.fullName(user));
    }

    @Transactional
    public ProfileResponse updateName(UUID userId, UpdateProfileRequest request) {
        User user = find(userId);
        user.setDisplayName(request.displayName().strip());
        record(userId, AuditAction.PROFILE_UPDATED);
        return ProfileResponse.from(user, names.fullName(user));
    }

    @Transactional(readOnly = true)
    public DocumentContent loadPicture(UUID userId) {
        User user = find(userId);
        if (user.getPictureKey() == null) {
            throw new InformationNotFoundException(ErrorMessages.PROFILE_PICTURE_NOT_FOUND, ErrorCodes.PROFILE_PICTURE_NOT_FOUND);
        }
        return new DocumentContent(fileStorageService.load(user.getPictureKey()), user.getPictureContentType());
    }

    @Transactional
    public ProfileResponse replacePicture(UUID userId, MultipartFile file) {
        User user = find(userId);
        FileType type = fileTypeDetector.detect(file, KycConstants.PHOTO_FILE_TYPES);
        removeStoredPicture(user);
        user.setPictureKey(fileStorageService.store(file, type));
        user.setPictureContentType(type.getContentType());
        record(userId, AuditAction.PROFILE_PICTURE_UPDATED);
        return ProfileResponse.from(user, names.fullName(user));
    }

    @Transactional
    public void removePicture(UUID userId) {
        User user = find(userId);
        if (user.getPictureKey() != null) {
            removeStoredPicture(user);
            user.setPictureKey(null);
            user.setPictureContentType(null);
            record(userId, AuditAction.PROFILE_PICTURE_REMOVED);
        }
    }

    private void removeStoredPicture(User user) {
        if (user.getPictureKey() != null) {
            fileStorageService.deleteAfterCommit(user.getPictureKey());
        }
    }

    private User find(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.USER_NOT_FOUND));
    }

    private void record(UUID userId, AuditAction action) {
        auditService.record(userId, action, AuditTargetType.USER, userId, null);
    }
}
