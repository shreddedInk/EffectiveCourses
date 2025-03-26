package com.coffeeshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI coffeeShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("CoffeeShop API")
                        .description("API for CoffeeShop")
                        .version(" ")
                        .contact(new Contact()
                                .name("student-miron")
                                .email("mironmoskalenko2000@gmail.com"))
                );
    }
}