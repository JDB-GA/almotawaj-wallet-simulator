package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.ConflictException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.model.AuditAction;
import com.almotawaj.wallet.model.AuditTargetType;
import com.almotawaj.wallet.model.Voucher;
import com.almotawaj.wallet.model.VoucherStatus;
import com.almotawaj.wallet.model.request.CreateVoucherRequest;
import com.almotawaj.wallet.model.request.UpdateVoucherRequest;
import com.almotawaj.wallet.model.response.PageResponse;
import com.almotawaj.wallet.model.response.VoucherPurchaseResponse;
import com.almotawaj.wallet.model.response.VoucherResponse;
import com.almotawaj.wallet.repository.VoucherPurchaseRepository;
import com.almotawaj.wallet.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final VoucherPurchaseRepository purchaseRepository;
    private final WalletProvisioner provisioner;
    private final WalletHolderNames names;
    private final AuditService auditService;
    private final Clock clock;

    @Transactional
    public VoucherResponse create(UUID merchantId, CreateVoucherRequest request) {
        ensurePriceWithinFaceValue(request.price(), request.faceValue());
        Voucher voucher = new Voucher();
        voucher.setMerchant(provisioner.getOrCreate(merchantId).getUser());
        voucher.setTitle(request.title().strip());
        voucher.setPrice(request.price());
        voucher.setFaceValue(request.faceValue());
        voucher.setTotalQuantity(request.totalQuantity());
        voucher.setRemaining(request.totalQuantity());
        voucher.setPerClientLimit(request.perClientLimit());
        voucher.setExpiresAt(request.expiresAt());
        voucherRepository.saveAndFlush(voucher);
        auditService.record(merchantId, AuditAction.VOUCHER_CREATED, AuditTargetType.VOUCHER, voucher.getId(), voucher.getTitle());
        log.info(LogMessages.VOUCHER_CREATED, merchantId, voucher.getId(), voucher.getTotalQuantity());
        return toResponse(voucher);
    }

    @Transactional
    public VoucherResponse update(UUID merchantId, UUID voucherId, UpdateVoucherRequest request) {
        Voucher voucher = voucherRepository.findByIdAndMerchantId(voucherId, merchantId).orElseThrow(VoucherService::notFound);
        if (voucher.getVersion() != request.version()) {
            log.warn(LogMessages.VOUCHER_EDIT_CONFLICT, voucherId, merchantId, request.version(), voucher.getVersion());
            throw new ConflictException(ErrorMessages.VOUCHER_EDIT_CONFLICT, ErrorCodes.VOUCHER_EDIT_CONFLICT);
        }
        ensurePriceWithinFaceValue(request.price(), voucher.getFaceValue());
        voucher.setTitle(request.title().strip());
        voucher.setPrice(request.price());
        voucher.setStatus(request.status());
        voucher.setExpiresAt(request.expiresAt());
        voucherRepository.saveAndFlush(voucher);
        auditService.record(merchantId, AuditAction.VOUCHER_UPDATED, AuditTargetType.VOUCHER, voucherId, voucher.getTitle());
        log.info(LogMessages.VOUCHER_UPDATED, merchantId, voucherId, voucher.getVersion());
        return toResponse(voucher);
    }

    @Transactional(readOnly = true)
    public PageResponse<VoucherResponse> listMine(UUID merchantId, Pageable pageable) {
        return PageResponse.from(voucherRepository.findByMerchantId(merchantId, pageable), this::toResponse);
    }

    @Transactional(readOnly = true)
    public PageResponse<VoucherResponse> listAvailable(Pageable pageable) {
        return PageResponse.from(voucherRepository.findAvailable(VoucherStatus.ACTIVE, clock.instant(), pageable), this::toResponse);
    }

    @Transactional(readOnly = true)
    public PageResponse<VoucherPurchaseResponse> listPurchases(UUID buyerId, Pageable pageable) {
        return PageResponse.from(purchaseRepository.findByBuyerId(buyerId, pageable),
                purchase -> VoucherPurchaseResponse.from(purchase, names.fullName(purchase.getVoucher().getMerchant())));
    }

    static InformationNotFoundException notFound() {
        return new InformationNotFoundException(ErrorMessages.VOUCHER_NOT_FOUND, ErrorCodes.VOUCHER_NOT_FOUND);
    }

    private VoucherResponse toResponse(Voucher voucher) {
        return VoucherResponse.from(voucher, names.fullName(voucher.getMerchant()), clock.instant());
    }

    private static void ensurePriceWithinFaceValue(BigDecimal price, BigDecimal faceValue) {
        if (price.compareTo(faceValue) > 0) {
            throw new BusinessRuleException(ErrorMessages.VOUCHER_PRICE_ABOVE_FACE_VALUE, ErrorCodes.VOUCHER_PRICE_ABOVE_FACE_VALUE);
        }
    }
}
