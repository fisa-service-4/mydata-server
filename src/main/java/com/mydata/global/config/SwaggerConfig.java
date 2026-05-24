package com.mydata.global.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  private static final String SECURITY_SCHEME_NAME = "bearerAuth";

  @Bean
  public OpenAPI openAPI() {

    SecurityRequirement securityRequirement =
        new SecurityRequirement().addList(SECURITY_SCHEME_NAME);

    SecurityScheme securityScheme =
        new SecurityScheme()
            .name(SECURITY_SCHEME_NAME)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");

    return new OpenAPI()
        .info(apiInfo())
        .addSecurityItem(securityRequirement)
        .schemaRequirement(SECURITY_SCHEME_NAME, securityScheme)
        .externalDocs(new ExternalDocumentation().description("MyData API Docs"));
  }

  private Info apiInfo() {

    return new Info()
        .title("MyData Server API")
        .description("프리랜서 특화 AI 자산관리 플랫폼 MyData API")
        .version("v1.0.0");
  }
}
