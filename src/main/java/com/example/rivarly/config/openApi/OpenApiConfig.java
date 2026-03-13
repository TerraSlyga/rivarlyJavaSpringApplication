package com.example.rivarly.config.openApi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation.
 * This class defines the OpenAPI bean with necessary settings like API info and security schemes.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates and configures the OpenAPI bean.
     * The bean contains info about the project API, contact details, and security configurations.
     *
     * @return an instance of {@link OpenAPI}.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rivarly API")
                        .version("1.0.0")
                        .description("Documentation for Rivarly API")
                        .contact(new Contact()
                                .name("Olexandr 'TerraSlyga' Krupskyi")
                                .email("terraslyga2004@email.com")));
    }
}