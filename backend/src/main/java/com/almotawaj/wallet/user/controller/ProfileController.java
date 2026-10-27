package com.almotawaj.wallet.user.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.ProfileDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.user.model.request.UpdateProfileRequest;
import com.almotawaj.wallet.user.model.response.ProfileResponse;
import com.almotawaj.wallet.user.service.ProfileService;
import com.almotawaj.wallet.wallet.model.DocumentContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = ProfileDocs.TAG, description = ProfileDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.PROFILE)
@PreAuthorize(SecurityConstants.HAS_WALLET_ROLE)
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @Operation(summary = ProfileDocs.GET, description = ProfileDocs.GET_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = ProfileDocs.PROFILE_OK)
    @GetMapping
    public ProfileResponse get(@AuthenticationPrincipal MyUserDetails userDetails) {
        return profileService.get(userDetails.user().getId());
    }

    @Operation(summary = ProfileDocs.UPDATE, description = ProfileDocs.UPDATE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = ProfileDocs.PROFILE_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @PreAuthorize(SecurityConstants.HAS_ROLE_MERCHANT)
    @PatchMapping
    public ProfileResponse updateName(@AuthenticationPrincipal MyUserDetails userDetails,
                                      @Valid @RequestBody UpdateProfileRequest request) {
        return profileService.updateName(userDetails.user().getId(), request);
    }

    @Operation(summary = ProfileDocs.PICTURE_GET)
    @ApiResponse(responseCode = ApiDocs.OK, description = ProfileDocs.PICTURE_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = ProfileDocs.PICTURE_NOT_FOUND)
    @GetMapping(ApiPaths.PROFILE_PICTURE)
    public ResponseEntity<Resource> getPicture(@AuthenticationPrincipal MyUserDetails userDetails) {
        DocumentContent picture = profileService.loadPicture(userDetails.user().getId());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(picture.contentType())).body(picture.resource());
    }

    @Operation(summary = ProfileDocs.PICTURE_PUT, description = ProfileDocs.PICTURE_PUT_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = ProfileDocs.PROFILE_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ProfileDocs.PICTURE_BAD_REQUEST)
    @ApiResponse(responseCode = ApiDocs.CONTENT_TOO_LARGE, description = ProfileDocs.PICTURE_TOO_LARGE)
    @PutMapping(path = ApiPaths.PROFILE_PICTURE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfileResponse replacePicture(@AuthenticationPrincipal MyUserDetails userDetails, @RequestPart MultipartFile file) {
        return profileService.replacePicture(userDetails.user().getId(), file);
    }

    @Operation(summary = ProfileDocs.PICTURE_DELETE)
    @ApiResponse(responseCode = ApiDocs.NO_CONTENT, description = ProfileDocs.PICTURE_DELETED)
    @DeleteMapping(ApiPaths.PROFILE_PICTURE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePicture(@AuthenticationPrincipal MyUserDetails userDetails) {
        profileService.removePicture(userDetails.user().getId());
    }
}
