package com.almotawaj.wallet.paymentrequest.service;

import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.kyc.model.KycStatus;
import com.almotawaj.wallet.paymentrequest.model.request.CreatePaymentRequest;
import com.almotawaj.wallet.paymentrequest.repository.PaymentRequestRepository;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.wallet.service.RecipientResolver;
import com.almotawaj.wallet.wallet.service.WalletAccessPolicy;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentRequestAccessTest {
    private static final UUID CLIENT_ID = UUID.randomUUID();
    private static final UUID REQUEST_ID = UUID.randomUUID();

    @Mock
    private PaymentRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecipientResolver recipientResolver;
    @Mock
    private PaymentRequestNotifier notifier;
    @Mock
    private AuditService auditService;
    @Spy
    private WalletAccessPolicy accessPolicy = new WalletAccessPolicy();

    @InjectMocks
    private PaymentRequestService service;

    @BeforeEach
    void setUp() {
        User client = new User();
        client.setId(CLIENT_ID);
        client.setKycStatus(KycStatus.PENDING);
        when(userRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));
    }

    @Test
    void clientUnderReviewCannotCreateRequests() {
        assertKycRequired(() -> service.create(CLIENT_ID, new CreatePaymentRequest("payer@example.com", new BigDecimal("12.500"), null)));
    }

    @Test
    void clientUnderReviewCannotListCancelOrDeclineRequests() {
        assertKycRequired(() -> service.list(CLIENT_ID, Pageable.unpaged()));
        assertKycRequired(() -> service.cancel(CLIENT_ID, REQUEST_ID));
        assertKycRequired(() -> service.decline(CLIENT_ID, REQUEST_ID));
    }

    private void assertKycRequired(ThrowingCallable call) {
        assertThatThrownBy(call)
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.WALLET_KYC_REQUIRED);
        verifyNoInteractions(requestRepository, recipientResolver, notifier, auditService);
    }
}
