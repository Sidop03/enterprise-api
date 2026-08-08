package com.example.enterprise_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(new Info()
                        .title("Enterprise Benefits API")
                        .description("Production-ready API for retrieving comprehensive user benefit summaries.\n\n"
                                + "**Key Features:**\n"
                                + "- Multi-tenant architecture with client isolation\n"
                                + "- OAuth2/JWT authentication with scope-based authorization\n"
                                + "- Intelligent caching (5-minute TTL) for performance optimization\n"
                                + "- Request tracking via X-Request-ID headers\n"
                                + "- Comprehensive error handling with structured responses\n"
                                + "- Contract-driven design for consumer compatibility\n\n"
                                + "**Profile Options:**\n"
                                + "- `summary`: Minimal payload for mobile clients (balance, status only)\n"
                                + "- `detailed`: Full benefit information including eligibility history\n\n"
                                + "**Usage:**\n"
                                + "1. Obtain OAuth2 JWT token from your identity provider\n"
                                + "2. Include token in Authorization header\n"
                                + "3. Call GET /v1/users/{userId}/benefits\n"
                                + "4. Handle cache hits via X-Cache-Hit header\n"
                                + "5. Track requests via X-Request-ID for support")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Benefits API Support")
                                .email("support@enterprise.com")
                                .url("https://docs.enterprise.com")));
    }
}
