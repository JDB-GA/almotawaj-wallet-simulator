package com.almotawaj.wallet.paymentrequest.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.paymentrequest.model.PaymentRequest;
import com.almotawaj.wallet.paymentrequest.model.PaymentRequestStatus;
import com.almotawaj.wallet.paymentrequest.repository.PaymentRequestRepository;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.wallet.model.request.TransferRequest;
import com.almotawaj.wallet.wallet.service.RecipientResolver;
import com.almotawaj.wallet.wallet.service.TransferService;
import com.almotawaj.wallet.wallet.service.WalletAccessPolicy;
import com.almotawaj.wallet.wallet.service.WalletHolderNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentRequestServiceTest {
    private static final UUID REQUEST_ID = UUID.randomUUID();

    @Mock
    private PaymentRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecipientResolver recipientResolver;
    @Mock
    private WalletAccessPolicy accessPolicy;
    @Mock
    private TransferService transferService;
    @Mock
    private PaymentRequestNotifier notifier;
    @Mock
    private WalletHolderNames names;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private PaymentRequestService service;

    private User requester;
    private User payer;
    private PaymentRequest request;

    @BeforeEach
    void setUp() {
        requester = user("requester@example.com");
        payer = user("payer@example.com");
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(requester));
        request = new PaymentRequest();
        request.setId(REQUEST_ID);
        request.setRequester(requester);
        request.setPayer(payer);
        request.setAmount(new BigDecimal("12.500"));
        request.setNote("Lunch");
        when(requestRepository.findByIdAndPayerIdForUpdate(REQUEST_ID, payer.getId())).thenReturn(Optional.of(request));
        when(requestRepository.findByIdAndRequesterIdForUpdate(REQUEST_ID, requester.getId())).thenReturn(Optional.of(request));
        when(requestRepository.saveAndFlush(any(PaymentRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void pay_transfersToRequesterAndMarksPaid() {
        service.pay(payer.getId(), REQUEST_ID);

        verify(transferService).transfer(payer.getId(),
                new TransferRequest("requester@example.com", new BigDecimal("12.500"), "Lunch"));
        assertThat(request.getStatus()).isEqualTo(PaymentRequestStatus.PAID);
        verify(auditService).record(payer.getId(), AuditAction.PAYMENT_REQUEST_PAID, AuditTargetType.PAYMENT_REQUEST, REQUEST_ID, null);
    }

    @Test
    void decline_isOnlyAvailableToThePayer() {
        assertThatThrownBy(() -> service.decline(requester.getId(), REQUEST_ID))
                .isInstanceOf(InformationNotFoundException.class);
        assertThat(request.getStatus()).isEqualTo(PaymentRequestStatus.PENDING);
    }

    @Test
    void cancel_marksCancelledAndNotifiesBothSides() {
        var response = service.cancel(requester.getId(), REQUEST_ID);

        assertThat(response.status()).isEqualTo(PaymentRequestStatus.CANCELLED);
        verify(notifier).updated(response);
    }

    @Test
    void decidedRequestsCannotBeDecidedAgain() {
        request.setStatus(PaymentRequestStatus.DECLINED);

        assertThatThrownBy(() -> service.pay(payer.getId(), REQUEST_ID))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.PAYMENT_REQUEST_NOT_PENDING);
        verifyNoInteractions(transferService, auditService, notifier);
    }

    private static User user(String email) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmailAddress(email);
        return user;
    }
}
