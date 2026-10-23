package com.almotawaj.wallet.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.SortFields;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.VoucherDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.model.request.CreateVoucherRequest;
import com.almotawaj.wallet.model.request.UpdateVoucherRequest;
import com.almotawaj.wallet.model.response.PageResponse;
import com.almotawaj.wallet.model.response.VoucherResponse;
import com.almotawaj.wallet.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = VoucherDocs.MERCHANT_TAG, description = VoucherDocs.MERCHANT_TAG_DESCRIPTION)
@RequestMapping(ApiPaths.MERCHANT_VOUCHERS)
@PreAuthorize(SecurityConstants.HAS_ROLE_MERCHANT)
@RequiredArgsConstructor
public class MerchantVoucherController {
    private final VoucherService voucherService;

    @Operation(summary = VoucherDocs.LIST_MINE, description = VoucherDocs.LIST_MINE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = VoucherDocs.LIST_MINE_OK)
    @GetMapping
    public PageResponse<VoucherResponse> list(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @PageableDefault(sort = SortFields.CREATED_AT, direction = Sort.Direction.DESC) Pageable pageable) {
        return voucherService.listMine(userDetails.user().getId(), pageable);
    }

    @Operation(summary = VoucherDocs.CREATE, description = VoucherDocs.CREATE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.CREATED, description = VoucherDocs.CREATE_CREATED)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = VoucherDocs.CREATE_UNPROCESSABLE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VoucherResponse create(@AuthenticationPrincipal MyUserDetails userDetails,
                                  @Valid @RequestBody CreateVoucherRequest request) {
        return voucherService.create(userDetails.user().getId(), request);
    }

    @Operation(summary = VoucherDocs.UPDATE, description = VoucherDocs.UPDATE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = VoucherDocs.UPDATE_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = VoucherDocs.UPDATE_NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.CONFLICT, description = VoucherDocs.UPDATE_CONFLICT)
    @PutMapping(ApiPaths.VOUCHER_BY_ID)
    public VoucherResponse update(@AuthenticationPrincipal MyUserDetails userDetails,
                                  @PathVariable UUID voucherId,
                                  @Valid @RequestBody UpdateVoucherRequest request) {
        return voucherService.update(userDetails.user().getId(), voucherId, request);
    }
}
