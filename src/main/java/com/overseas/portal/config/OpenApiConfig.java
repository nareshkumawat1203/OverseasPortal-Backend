package com.overseas.portal.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Overseas Study Portal API",
        version = "1.0.0",
        description = "REST API for the Overseas Study Portal — connecting students with universities and service providers worldwide.",
        contact = @Contact(name = "Support", email = "support@overseasportal.com")
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Dev"),
        @Server(url = "https://api.overseasportal.com", description = "Production")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT Bearer Token",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {}
