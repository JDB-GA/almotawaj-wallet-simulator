package com.almotawaj.wallet.chatbot.exception;

import com.almotawaj.wallet.chatbot.config.ChatbotConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException e) {
        return problem(HttpStatus.BAD_REQUEST, ChatbotConstants.VALIDATION_FAILED_MESSAGE, ChatbotConstants.VALIDATION_FAILED);
    }

    @ExceptionHandler(ChatbotUnavailableException.class)
    public ProblemDetail handleUnavailable(ChatbotUnavailableException e) {
        return problem(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), ChatbotConstants.CHATBOT_UNAVAILABLE);
    }

    @ExceptionHandler(InvalidDocumentException.class)
    public ProblemDetail handleInvalidDocument(InvalidDocumentException e) {
        return problem(HttpStatus.BAD_REQUEST, e.getMessage(), ChatbotConstants.INVALID_DOCUMENT);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleTooLarge(MaxUploadSizeExceededException e) {
        return problem(HttpStatus.CONTENT_TOO_LARGE, ChatbotConstants.DOCUMENT_TOO_LARGE_MESSAGE, ChatbotConstants.DOCUMENT_TOO_LARGE);
    }

    private static ProblemDetail problem(HttpStatus status, String detail, String code) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setProperty(ChatbotConstants.ERROR_CODE_PROPERTY, code);
        return problem;
    }
}
