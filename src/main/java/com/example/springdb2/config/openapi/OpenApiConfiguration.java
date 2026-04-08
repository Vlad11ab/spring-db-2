package com.example.springdb2.config.openapi;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenApi(@Value("${application.bootstrap-admin.email}") String superAdminEmail,
                                 @Value("${application.bootstrap-admin.password}") String superAdminPassword) {
        String description = """
                REST API for the online school learning project.

                Authentication:
                1. Call POST /api/v1/auth/login with email and password.
                2. Copy the JWT token from the response.
                3. Authorize Swagger with: Bearer <token>

                Bootstrap super admin:
                - email: %s
                - password: %s

                This bootstrap account is created automatically on application startup if it does not already exist.
                """.formatted(superAdminEmail, superAdminPassword);

        return new OpenAPI()
                .info(new Info()
                        .title("Online School API")
                        .version("v1")
                        .description(description))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
