package com.almotawaj.wallet.chatbot.config;

import java.util.List;

public final class ChatbotConstants {
    public static final String CHAT_PATH = "/api/chat";
    public static final String STREAM_PATH = "/stream";
    public static final String DOCUMENTS_PATH = "/api/admin/chat/documents";
    public static final String DOCUMENT_ID_PATH = "/{documentId}";
    public static final String HAS_ROLE_SUPER_ADMIN = "hasRole('SUPER_ADMIN')";
    public static final String HEALTH_PATHS = "/actuator/health/**";
    public static final String ERROR_PATH = "/error";

    public static final String AUTH_COOKIE_NAME = "access_token";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORITIES_CLAIM = "authorities";
    public static final String JWT_SECRET_PROPERTY = "${jwt-secret}";

    public static final String GEMINI_PROPERTIES_PREFIX = "gemini";
    public static final String API_KEY_HEADER = "x-goog-api-key";
    public static final String GENERATE_CONTENT_PATH = "/v1beta/models/{model}:generateContent";
    public static final String USER_ROLE = "user";
    public static final String STORES_PATH = "/v1beta/fileSearchStores";
    public static final String STORE_DISPLAY_NAME = "almotawaj-docs";
    public static final String STREAM_CONTENT_PATH = "/v1beta/models/{model}:streamGenerateContent?alt=sse";
    public static final String SSE_DATA_PREFIX = "data:";
    public static final String DELTA_EVENT = "delta";
    public static final String DONE_EVENT = "done";
    public static final String ERROR_EVENT = "error";
    public static final long STREAM_TIMEOUT_MS = 60_000;
    public static final String LIST_DOCUMENTS_PATH = "/v1beta/%s/documents?pageSize={pageSize}";
    public static final String DOCUMENT_PATH = "/v1beta/%s/documents/{documentId}?force=true";
    public static final String UPLOAD_PATH = "/upload/v1beta/%s:uploadToFileSearchStore";
    public static final String UPLOAD_PROTOCOL_HEADER = "X-Goog-Upload-Protocol";
    public static final String UPLOAD_COMMAND_HEADER = "X-Goog-Upload-Command";
    public static final String UPLOAD_OFFSET_HEADER = "X-Goog-Upload-Offset";
    public static final String UPLOAD_LENGTH_HEADER = "X-Goog-Upload-Header-Content-Length";
    public static final String UPLOAD_TYPE_HEADER = "X-Goog-Upload-Header-Content-Type";
    public static final String UPLOAD_URL_HEADER = "X-Goog-Upload-URL";
    public static final String UPLOAD_PROTOCOL = "resumable";
    public static final String UPLOAD_START = "start";
    public static final String UPLOAD_FINISH = "upload, finalize";
    public static final String UPLOAD_OFFSET = "0";
    public static final String DOCUMENT_MIME_TYPE = "text/plain";
    public static final String DOCUMENT_NAME_SEPARATOR = "/";
    public static final int DOCUMENTS_PAGE_SIZE = 20;
    public static final List<String> ALLOWED_EXTENSIONS = List.of(".md", ".txt");

    public static final int QUESTION_MAX = 500;
    public static final int SOURCES_MAX = 5;

    public static final String SYSTEM_INSTRUCTION = """
            You are the Almotawaj Digital Wallet assistant.
            Answer only from the wallet documentation found by the file search tool.
            If the documentation does not cover the question, say you don't know.
            Keep answers short and clear, using bullets or numbered steps when helpful.
            Reply in the same language as the question.
            """;

    public static final String ERROR_CODE_PROPERTY = "code";
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String VALIDATION_FAILED_MESSAGE = "The request is not valid.";
    public static final String CHATBOT_UNAVAILABLE = "CHATBOT_UNAVAILABLE";
    public static final String CHATBOT_UNAVAILABLE_MESSAGE = "The assistant is not available right now. Please try again later.";
    public static final String INVALID_DOCUMENT = "INVALID_DOCUMENT";
    public static final String INVALID_DOCUMENT_MESSAGE = "Only non-empty .md or .txt files are allowed.";
    public static final String DOCUMENT_TOO_LARGE = "DOCUMENT_TOO_LARGE";
    public static final String DOCUMENT_TOO_LARGE_MESSAGE = "The file is too large.";
    public static final String QUESTION_REQUIRED = "Question is required.";
    public static final String QUESTION_TOO_LONG = "Question must be at most 500 characters.";

    private ChatbotConstants() {
    }
}
