package com.almotawaj.wallet.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.SimulationDocs;
import com.almotawaj.wallet.model.request.PayoutRaceRequest;
import com.almotawaj.wallet.model.request.VoucherRaceRequest;
import com.almotawaj.wallet.model.response.PayoutRaceResult;
import com.almotawaj.wallet.model.response.VoucherRaceResult;
import com.almotawaj.wallet.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = SimulationDocs.TAG, description = SimulationDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.ADMIN_SIMULATIONS)
@PreAuthorize(SecurityConstants.HAS_ROLE_SUPER_ADMIN)
@RequiredArgsConstructor
public class SimulationController {
    private final SimulationService simulationService;

    @Operation(summary = SimulationDocs.VOUCHER_RACE, description = SimulationDocs.VOUCHER_RACE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = SimulationDocs.VOUCHER_RACE_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @PostMapping(ApiPaths.SIMULATION_VOUCHER_RACE)
    public VoucherRaceResult voucherRace(@Valid @RequestBody VoucherRaceRequest request) throws InterruptedException {
        return simulationService.voucherRace(request);
    }

    @Operation(summary = SimulationDocs.PAYOUT_RACE, description = SimulationDocs.PAYOUT_RACE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = SimulationDocs.PAYOUT_RACE_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @PostMapping(ApiPaths.SIMULATION_PAYOUT_RACE)
    public PayoutRaceResult payoutRace(@Valid @RequestBody PayoutRaceRequest request) {
        return simulationService.payoutRace(request);
    }
}
