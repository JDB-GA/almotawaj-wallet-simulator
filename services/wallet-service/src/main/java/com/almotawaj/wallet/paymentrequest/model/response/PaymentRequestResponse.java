package com.almotawaj.wallet.paymentrequest.model.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.almotawaj.wallet.paymentrequest.model.PaymentRequest;
import com.almotawaj.wallet.paymentrequest.model.PaymentRequestStatus;
import com.almotawaj.wallet.wallet.model.response.RecipientResponse;
import com.almotawaj.wallet.wallet.service.WalletHolderNames;

public record PaymentRequestResponse(
        UUID id,
        UUID requesterId,
        UUID payerId,
        RecipientResponse requester,
        RecipientResponse payer,
        BigDecimal amount,
        String note,
        PaymentRequestStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static PaymentRequestResponse from(
            PaymentRequest request,
            WalletHolderNames names
    ) {
        RecipientResponse requesterDto = new RecipientResponse(
                names.maskedName(request.getRequester()),
                names.maskedEmail(request.getRequester()),
                request.getRequester().getRole()
        );

        RecipientResponse payerDto = new RecipientResponse(
                names.maskedName(request.getPayer()),
                names.maskedEmail(request.getPayer()),
                request.getPayer().getRole()
        );

        return new PaymentRequestResponse(
                request.getId(),
                request.getRequester().getId(),
                request.getPayer().getId(),
                requesterDto,
                payerDto,
                request.getAmount(),
                request.getNote(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
