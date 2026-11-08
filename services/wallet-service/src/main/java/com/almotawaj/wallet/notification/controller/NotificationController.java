package com.almotawaj.wallet.notification.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.NotificationDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.notification.service.NotificationHub;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Tag(name = NotificationDocs.TAG, description = NotificationDocs.TAG_DESCRIPTION)
public class NotificationController {
    private final NotificationHub notificationHub;

    @GetMapping(path = ApiPaths.NOTIFICATIONS_STREAM, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize(SecurityConstants.HAS_WALLET_ROLE)
    @Operation(summary = NotificationDocs.STREAM, description = NotificationDocs.STREAM_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = NotificationDocs.STREAM_OK)
    public SseEmitter stream(@AuthenticationPrincipal MyUserDetails userDetails) {
        return notificationHub.subscribe(userDetails.user().getId());
    }
}
