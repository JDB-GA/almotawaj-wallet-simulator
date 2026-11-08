package com.almotawaj.wallet.config;

import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.GatewayDocs;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(ApiDocs.TITLE)
                        .version(ApiDocs.VERSION)
                        .description(ApiDocs.DESCRIPTION))
                .addSecurityItem(new SecurityRequirement().addList(ApiDocs.SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(ApiDocs.SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SecurityConstants.AUTH_COOKIE_NAME)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .description(ApiDocs.SECURITY_SCHEME_DESCRIPTION))
                        .addSecuritySchemes(ApiDocs.SEED_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(ApiDocs.BEARER)
                                .description(ApiDocs.SEED_SCHEME_DESCRIPTION))
                        .addSecuritySchemes(GatewayDocs.API_KEY_SCHEME_NAME, new SecurityScheme()
                                .name(SecurityConstants.API_KEY_HEADER)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description(GatewayDocs.API_KEY_SCHEME_DESCRIPTION)));
    }
}
