package com.almotawaj.wallet.config.openapi;

import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

@Component
public class ErrorResponsesCustomizer implements OpenApiCustomizer {
    @Override
    public void customise(io.swagger.v3.oas.models.OpenAPI openApi) {
        ModelConverters.getInstance().read(ProblemDetail.class).forEach(openApi.getComponents()::addSchemas);
        openApi.getPaths().values().forEach(path -> path.readOperations().forEach(this::addErrorResponses));
    }

    private void addErrorResponses(Operation operation) {
        ApiResponses responses = operation.getResponses();
        boolean isProtected = operation.getSecurity() == null || !operation.getSecurity().isEmpty();
        if (isProtected) {
            responses.putIfAbsent(ApiDocs.UNAUTHORIZED, new ApiResponse().description(ApiDocs.UNAUTHORIZED_DESCRIPTION));
            responses.putIfAbsent(ApiDocs.FORBIDDEN, new ApiResponse().description(ApiDocs.FORBIDDEN_DESCRIPTION));
        }
        responses.putIfAbsent(ApiDocs.SERVER_ERROR, new ApiResponse().description(ApiDocs.SERVER_ERROR_DESCRIPTION));
        responses.forEach((code, response) -> {
            if (code.startsWith(ApiDocs.CLIENT_ERROR_PREFIX) || code.startsWith(ApiDocs.SERVER_ERROR_PREFIX)) {
                response.setContent(new Content().addMediaType(ApiDocs.PROBLEM_JSON,
                        new MediaType().schema(new Schema<>().$ref(ApiDocs.PROBLEM_SCHEMA_REF))));
            }
        });
    }
}
