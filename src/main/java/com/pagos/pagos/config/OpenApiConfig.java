package com.pagos.pagos.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Pagos - RetailMax")
                        .description("Microservicio para gestión de pagos del sistema RetailMax")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo RetailMax")
                                .email("desarrollo@retailmax.com")
                                .url("https://retailmax.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://api.retailmax.com/pagos")
                                .description("Servidor de Producción")
                ));
    }
} 