package com.almotawaj.wallet.exception;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, ErrorMessages.VALIDATION_FAILED, ErrorCodes.VALIDATION_FAILED);
        problem.setProperty(ErrorMessages.VALIDATION_ERRORS_KEY, errors);
        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException e) {
        return problem(HttpStatus.UNAUTHORIZED, ErrorMessages.INVALID_CREDENTIALS, ErrorCodes.INVALID_CREDENTIALS);
    }

    @ExceptionHandler(DisabledException.class)
    public ProblemDetail handleDisabled(DisabledException e) {
        return problem(HttpStatus.UNAUTHORIZED, ErrorMessages.ACCOUNT_DEACTIVATED, ErrorCodes.ACCOUNT_DEACTIVATED);
    }

    @ExceptionHandler(LockedException.class)
    public ProblemDetail handleLocked(LockedException e) {
        return problem(HttpStatus.UNAUTHORIZED, ErrorMessages.ACCOUNT_LOCKED, ErrorCodes.ACCOUNT_LOCKED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException e) {
        return problem(HttpStatus.FORBIDDEN, ErrorMessages.ACCESS_DENIED, ErrorCodes.ACCESS_DENIED);
    }

    @ExceptionHandler({PropertyReferenceException.class, MethodArgumentTypeMismatchException.class})
    public ProblemDetail handleInvalidParameter(Exception e) {
        return problem(HttpStatus.BAD_REQUEST, ErrorMessages.INVALID_REQUEST_PARAMETER, ErrorCodes.INVALID_REQUEST_PARAMETER);
    }

    @ExceptionHandler(InformationExistException.class)
    public ProblemDetail handleExists(InformationExistException e) {
        return problem(HttpStatus.CONFLICT, e.getMessage(), e.getCode());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException e) {
        return problem(HttpStatus.CONFLICT, ErrorMessages.DATA_CONFLICT, ErrorCodes.DATA_CONFLICT);
    }

    @ExceptionHandler(InformationNotFoundException.class)
    public ProblemDetail handleNotFound(InformationNotFoundException e) {
        return problem(HttpStatus.NOT_FOUND, e.getMessage(), e.getCode());
    }

    @ExceptionHandler(OtpVerificationException.class)
    public ProblemDetail handleOtpVerification(OtpVerificationException e) {
        return problem(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCode());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleException e) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, e.getMessage(), e.getCode());
    }

    @ExceptionHandler(InvalidFileException.class)
    public ProblemDetail handleInvalidFile(InvalidFileException e) {
        return problem(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCode());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleUploadTooLarge(MaxUploadSizeExceededException e) {
        return problem(HttpStatus.CONTENT_TOO_LARGE, ErrorMessages.FILE_TOO_LARGE, ErrorCodes.FILE_TOO_LARGE);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ProblemDetail> handleRateLimit(RateLimitExceededException e) {
        return tooManyRequests(e.getRetryAfter(), ErrorMessages.TOO_MANY_REQUESTS, ErrorCodes.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(OtpResendCooldownException.class)
    public ResponseEntity<ProblemDetail> handleOtpResendCooldown(OtpResendCooldownException e) {
        return tooManyRequests(e.getRetryAfter(), ErrorMessages.OTP_RESEND_COOLDOWN, ErrorCodes.OTP_RESEND_COOLDOWN);
    }

    private static ResponseEntity<ProblemDetail> tooManyRequests(Duration retryAfter, String detail, String code) {
        long retryAfterSeconds = Math.max(1, retryAfter.toSeconds());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds))
                .body(problem(HttpStatus.TOO_MANY_REQUESTS, detail, code));
    }

    private static ProblemDetail problem(HttpStatus status, String detail, String code) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setProperty(ErrorCodes.PROPERTY, code);
        return problem;
    }
}
