package com.almotawaj.wallet.kyc.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.ValidationPatterns;
import com.almotawaj.wallet.config.constants.docs.DocExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record KycSubmitRequest(
        @Schema(example = DocExamples.FULL_NAME)
        @NotBlank(message = ErrorMessages.FULL_NAME_REQUIRED)
        @Size(max = ValidationLimits.FULL_NAME_MAX, message = ErrorMessages.FULL_NAME_TOO_LONG)
        @Pattern(regexp = ValidationPatterns.FULL_NAME, message = ErrorMessages.FULL_NAME_INVALID)
        String fullName,

        @Schema(example = DocExamples.CPR)
        @NotBlank(message = ErrorMessages.CPR_REQUIRED)
        @Pattern(regexp = ValidationPatterns.CPR_NUMBER, message = ErrorMessages.CPR_INVALID)
        String cprNumber,

        @Schema(example = DocExamples.DATE_OF_BIRTH)
        @NotNull(message = ErrorMessages.DATE_OF_BIRTH_REQUIRED)
        @Past(message = ErrorMessages.DATE_OF_BIRTH_PAST)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dateOfBirth,

        @Schema(example = DocExamples.NATIONALITY)
        @NotBlank(message = ErrorMessages.NATIONALITY_REQUIRED)
        @Pattern(regexp = ValidationPatterns.NATIONALITY, message = ErrorMessages.NATIONALITY_INVALID)
        String nationality,

        @Schema(example = DocExamples.BLOCK)
        @Pattern(regexp = ValidationPatterns.BLOCK, message = ErrorMessages.BLOCK_INVALID)
        @NotNull(message = ErrorMessages.BLOCK_INVALID)
        String block,

        @Schema(example = DocExamples.ROAD)
        @Pattern(regexp = ValidationPatterns.ROAD, message = ErrorMessages.ROAD_INVALID)
        @NotNull(message = ErrorMessages.ROAD_INVALID)
        String road,

        @Schema(example = DocExamples.BUILDING)
        @Pattern(regexp = ValidationPatterns.BUILDING, message = ErrorMessages.BUILDING_INVALID)
        @NotNull(message = ErrorMessages.BUILDING_INVALID)
        String building,

        @Schema(example = DocExamples.FLAT)
        @Pattern(regexp = ValidationPatterns.FLAT, message = ErrorMessages.FLAT_INVALID)
        String flat,

        @Schema(example = DocExamples.AREA)
        @NotBlank(message = ErrorMessages.AREA_REQUIRED)
        @Size(max = ValidationLimits.AREA_MAX, message = ErrorMessages.AREA_TOO_LONG)
        String area,

        @Schema(example = DocExamples.CPR_EXPIRY)
        @NotNull(message = ErrorMessages.EXPIRY_DATE_REQUIRED)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate cprExpiryDate,

        @Schema(example = DocExamples.PASSPORT_EXPIRY)
        @NotNull(message = ErrorMessages.EXPIRY_DATE_REQUIRED)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate passportExpiryDate,

        @NotNull(message = ErrorMessages.FILE_REQUIRED)
        MultipartFile cprFile,

        @NotNull(message = ErrorMessages.FILE_REQUIRED)
        MultipartFile passportFile,

        @NotNull(message = ErrorMessages.FILE_REQUIRED)
        MultipartFile photo
) {
}
