package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.kyc.model.KycStatus;
import com.almotawaj.wallet.kyc.repository.KycApplicationRepository;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipientSuggestionServiceTest {
    private static final UUID SENDER_ID = UUID.randomUUID();

    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;
    @Spy
    private WalletAccessPolicy accessPolicy;
    @Mock
    private KycApplicationRepository kycApplicationRepository;

    private RecipientSuggestionService service;

    @BeforeEach
    void setUp() {
        User sender = new User();
        sender.setRole(UserRole.CLIENT);
        sender.setKycStatus(KycStatus.APPROVED);
        when(userRepository.findById(SENDER_ID)).thenReturn(Optional.of(sender));
        service = new RecipientSuggestionService(userRepository, walletRepository, accessPolicy,
                new WalletHolderNames(kycApplicationRepository));
    }

    @Test
    void suggest_returnsNothingForShortQueries() {
        assertThat(service.suggest(SENDER_ID, " ab ")).isEmpty();
        verify(walletRepository, never()).findRecipientSuggestions(any(), any(), any(), any());
    }

    @Test
    void suggest_escapesWildcardsAndMasksMobile() {
        User recipient = new User();
        recipient.setEmailAddress("sara@example.com");
        recipient.setMobileNumber("+97333123456");
        recipient.setRole(UserRole.MERCHANT);
        Wallet wallet = new Wallet();
        wallet.setUser(recipient);
        wallet.setIban("BH02ALMT00000000001234");
        when(walletRepository.findRecipientSuggestions(eq(SENDER_ID), eq("sa\\_r%"), eq("+973sa\\_r%"), any()))
                .thenReturn(List.of(wallet));

        var suggestions = service.suggest(SENDER_ID, "Sa_R");

        assertThat(suggestions).singleElement().satisfies(suggestion -> {
            assertThat(suggestion.iban()).isEqualTo("BH02ALMT00000000001234");
            assertThat(suggestion.maskedMobile()).isEqualTo("********3456");
        });
    }
}
