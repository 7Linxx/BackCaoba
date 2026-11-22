package co.edu.unbosque.backcaoba.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación OpenAPI / Swagger de Caoba Marketplace.
 * <p>
 * La interfaz queda disponible en {@code /swagger-ui.html} y la especificación
 * en {@code /v3/api-docs}.
 * </p>
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_SEGURIDAD = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        String descripcion = "<h2>API REST de Caoba Marketplace</h2>"
                + "<p>Marketplace de alojamientos: registro, autenticación con JWT, "
                + "gestión de perfil y administración de usuarios.</p>"
                + "<h3>Roles</h3>"
                + "<ul>"
                + "<li><strong>HUESPED</strong>: busca y reserva alojamientos.</li>"
                + "<li><strong>ANFITRION</strong>: publica y administra alojamientos.</li>"
                + "<li><strong>ADMINISTRADOR</strong>: gestiona usuarios y publicaciones.</li>"
                + "</ul>";

        String descripcionSeguridad = "Autenticación mediante JWT."
                + "<ol>"
                + "<li>Obtén un token con <code>POST /api/auth/login</code>.</li>"
                + "<li>Pulsa <strong>Authorize</strong> y pega solo el token (sin la palabra Bearer).</li>"
                + "</ol>";

        Info info = new Info()
                .title("Caoba Marketplace API")
                .version("0.1.0")
                .description(descripcion);

        SecurityScheme esquema = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description(descripcionSeguridad);

        return new OpenAPI()
                .info(info)
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_SEGURIDAD))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_SEGURIDAD, esquema)
                        .addResponses("UnauthorizedError", respuestaError(
                                "No autenticado: token JWT inválido o expirado",
                                "{\"status\": 401, \"error\": \"Se requiere autenticacion valida.\"}"))
                        .addResponses("ForbiddenError", respuestaError(
                                "Acceso prohibido: no tienes permisos para esta operación",
                                "{\"status\": 403, \"error\": \"Forbidden\"}"))
                        .addResponses("NotFoundError", respuestaError(
                                "Recurso no encontrado",
                                "{\"status\": 404, \"mensaje\": \"Usuario no encontrado.\"}")));
    }

    private ApiResponse respuestaError(String descripcion, String ejemplo) {
        return new ApiResponse()
                .description(descripcion)
                .content(new Content().addMediaType("application/json",
                        new MediaType().addExamples("error", new Example().value(ejemplo))));
    }
}
