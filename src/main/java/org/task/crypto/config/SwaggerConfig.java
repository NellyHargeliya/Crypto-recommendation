package org.task.crypto.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Crypto Investment Recommendation API",
                                version = "v1",
                                description = "API for managing crypto investments and recommendations"))
public class SwaggerConfig {
}
