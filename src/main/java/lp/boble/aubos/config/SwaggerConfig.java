package lp.boble.aubos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String token = "bearerAuth";
        String apiKey = "apiKeyAuth";

        return new OpenAPI().info(new Info()
                .title("Aubos API")
                .version("1.0 巴西人")
                .description("Documentação do Aubos API")
                .contact(new Contact()
                        .name("Github: Perfil")
                        .url("https://github.com/levipereira9")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Github: Código")
                        .url("https://github.com/LeviPereira9/aubos")
                )
                .addSecurityItem(new SecurityRequirement().addList(token))
                .addSecurityItem(new SecurityRequirement().addList(apiKey))
                .components(new Components()
                        .addSecuritySchemes(token, new SecurityScheme()
                                .name("Authorization")
                                .in(SecurityScheme.In.HEADER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                        .addSecuritySchemes(apiKey, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-KEY")
                        )
                )
                ;

    }

}
