package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.PdfConstants;
import com.almotawaj.wallet.config.constants.SortFields;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.wallet.model.DirectionTotal;
import com.almotawaj.wallet.wallet.model.PdfFile;
import com.almotawaj.wallet.wallet.model.StatementData;
import com.almotawaj.wallet.wallet.model.TransactionDirection;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import com.almotawaj.wallet.wallet.model.request.TransactionSearchRequest;
import com.almotawaj.wallet.wallet.repository.TransactionTotalsRepository;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import com.almotawaj.wallet.wallet.repository.specification.WalletTransactionSpecifications;
import com.almotawaj.wallet.wallet.service.pdf.PdfLabels;
import com.almotawaj.wallet.wallet.service.pdf.ReceiptPdfRenderer;
import com.almotawaj.wallet.wallet.service.pdf.StatementPdfRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionDocumentService {
    private static final ZoneId ZONE = ZoneId.of(WalletConstants.TIME_ZONE);
    private static final PageRequest STATEMENT_ROWS = PageRequest.of(0, PdfConstants.STATEMENT_MAX_ROWS, Sort.by(Sort.Direction.ASC, SortFields.CREATED_AT));
    private static final DateTimeFormatter STATEMENT_NUMBER_DATE = DateTimeFormatter.ofPattern(PdfConstants.STATEMENT_NUMBER_DATE_PATTERN).withZone(ZONE);

    private final WalletProvisioner provisioner;
    private final WalletTransactionRepository transactionRepository;
    private final TransactionTotalsRepository totalsRepository;
    private final WalletHolderNames names;
    private final ReceiptPdfRenderer receiptRenderer;
    private final StatementPdfRenderer statementRenderer;
    private final MessageSource messageSource;
    private final AuditService auditService;
    private final Clock clock;

    @Transactional
    public PdfFile receipt(UUID userId, UUID transactionId, Locale locale) {
        WalletTransaction transaction = transactionRepository.findByIdAndWalletUserId(transactionId, userId).orElseThrow(() ->
                new InformationNotFoundException(ErrorMessages.TRANSACTION_NOT_FOUND, ErrorCodes.TRANSACTION_NOT_FOUND));
        String ownerName = names.fullName(transaction.getWallet().getUser());
        byte[] content = receiptRenderer.render(transaction, ownerName, new PdfLabels(messageSource, locale));
        auditService.record(userId, AuditAction.RECEIPT_DOWNLOADED, AuditTargetType.WALLET_TRANSACTION, transactionId, null);
        return new PdfFile(PdfConstants.RECEIPT_FILE_NAME.formatted(transaction.getReference()), content);
    }

    @Transactional
    public PdfFile statement(UUID userId, TransactionSearchRequest filter, Locale locale) {
        Wallet wallet = provisioner.getOrCreate(userId);
        Specification<WalletTransaction> specification = WalletTransactionSpecifications.forWallet(wallet.getId(), filter, ZONE);
        Page<WalletTransaction> page = transactionRepository.findAll(specification, STATEMENT_ROWS);
        List<DirectionTotal> totals = totalsRepository.totalsByDirection(specification);
        StatementData statement = new StatementData(names.fullName(wallet.getUser()), wallet.getIban(), statementNumber(wallet),
                filter.from(), filter.to(), page.getContent(), page.getTotalElements(),
                openingBalance(wallet, filter.from()), closingBalance(wallet, filter.to()),
                DirectionTotal.countOf(totals, TransactionDirection.CREDIT), DirectionTotal.amountOf(totals, TransactionDirection.CREDIT),
                DirectionTotal.countOf(totals, TransactionDirection.DEBIT), DirectionTotal.amountOf(totals, TransactionDirection.DEBIT),
                isNarrowed(filter));
        byte[] content = statementRenderer.render(statement, new PdfLabels(messageSource, locale));
        auditService.record(userId, AuditAction.STATEMENT_DOWNLOADED, AuditTargetType.USER, userId, null);
        return new PdfFile(PdfConstants.STATEMENT_FILE_NAME.formatted(LocalDate.now(clock.withZone(ZONE))), content);
    }

    private BigDecimal openingBalance(Wallet wallet, LocalDate from) {
        return from == null ? BigDecimal.ZERO : balanceBefore(wallet, from.atStartOfDay(ZONE).toInstant());
    }

    private BigDecimal closingBalance(Wallet wallet, LocalDate to) {
        return to == null ? wallet.getBalance() : balanceBefore(wallet, to.plusDays(1).atStartOfDay(ZONE).toInstant());
    }

    private BigDecimal balanceBefore(Wallet wallet, Instant before) {
        return transactionRepository.findFirstByWalletIdAndCreatedAtBeforeOrderByCreatedAtDesc(wallet.getId(), before)
                .map(WalletTransaction::getBalanceAfter)
                .orElse(BigDecimal.ZERO);
    }

    private String statementNumber(Wallet wallet) {
        String iban = wallet.getIban();
        String ibanTail = iban.substring(Math.max(0, iban.length() - PdfConstants.STATEMENT_NUMBER_IBAN_DIGITS));
        return PdfConstants.STATEMENT_NUMBER_PATTERN.formatted(STATEMENT_NUMBER_DATE.format(clock.instant()), ibanTail);
    }

    private static boolean isNarrowed(TransactionSearchRequest filter) {
        return (filter.search() != null && !filter.search().isBlank()) || filter.type() != null || filter.direction() != null
                || filter.minAmount() != null || filter.maxAmount() != null;
    }
}
