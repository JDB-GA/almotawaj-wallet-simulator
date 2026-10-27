package com.almotawaj.wallet.user.service;

import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.common.model.FileType;
import com.almotawaj.wallet.common.service.FileStorageService;
import com.almotawaj.wallet.common.service.FileTypeDetector;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.exception.InvalidFileException;
import com.almotawaj.wallet.kyc.repository.KycApplicationRepository;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.request.UpdateProfileRequest;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.wallet.service.WalletHolderNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProfileServiceTest {
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n', 0};

    @Mock
    private UserRepository userRepository;
    @Mock
    private KycApplicationRepository kycApplicationRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private AuditService auditService;

    private ProfileService service;
    private User merchant;

    @BeforeEach
    void setUp() {
        service = new ProfileService(userRepository, new WalletHolderNames(kycApplicationRepository), new FileTypeDetector(),
                fileStorageService, auditService);
        merchant = new User();
        merchant.setId(UUID.randomUUID());
        merchant.setEmailAddress("shop@example.com");
        merchant.setRole(UserRole.MERCHANT);
        when(userRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        when(fileStorageService.store(any(), eq(FileType.PNG))).thenReturn("new.png");
    }

    @Test
    void nameFallsBackToTheEmailUntilABusinessNameIsSet() {
        assertThat(service.get(merchant.getId()).name()).isEqualTo("shop@example.com");

        assertThat(service.updateName(merchant.getId(), new UpdateProfileRequest(" Sara Coffee ")).name()).isEqualTo("Sara Coffee");
    }

    @Test
    void replacingThePictureDeletesTheOldFile() {
        merchant.setPictureKey("old.png");

        var profile = service.replacePicture(merchant.getId(), new MockMultipartFile("file", PNG));

        assertThat(profile.hasPicture()).isTrue();
        assertThat(merchant.getPictureKey()).isEqualTo("new.png");
        verify(fileStorageService).deleteAfterCommit("old.png");
    }

    @Test
    void pictureMustBeARealImage() {
        assertThatThrownBy(() -> service.replacePicture(merchant.getId(), new MockMultipartFile("file", "%PDF-1.7".getBytes())))
                .isInstanceOf(InvalidFileException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.FILE_TYPE_INVALID);
        verify(fileStorageService, never()).store(any(), any());
    }

    @Test
    void loadingAMissingPictureIsNotFound() {
        assertThatThrownBy(() -> service.loadPicture(merchant.getId())).isInstanceOf(InformationNotFoundException.class);
    }
}
