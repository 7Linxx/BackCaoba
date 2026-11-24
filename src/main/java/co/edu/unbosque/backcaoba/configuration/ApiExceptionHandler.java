package co.edu.unbosque.backcaoba.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> validation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> fields = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String message = Objects.requireNonNullElse(
                    fieldError.getDefaultMessage(),
                    "Dato inválido.");
            fields.putIfAbsent(fieldError.getField(), message);
        }

        return response(
                HttpStatus.BAD_REQUEST,
                "Revisa los datos del formulario.",
                request,
                fields);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> business(
            ResponseStatusException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = Objects.requireNonNullElse(
                ex.getReason(),
                "No fue posible procesar la solicitud.");

        return response(status, message, request, Map.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> accessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        return response(
                HttpStatus.FORBIDDEN,
                "No tienes permiso para realizar esta operación con tu rol actual.",
                request,
                Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> conflict(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        String causa = ex.getMostSpecificCause().getMessage();
        String detalle = causa == null ? "" : causa.toLowerCase(Locale.ROOT);

        String mensaje = "No se pudo guardar la información porque existe un dato duplicado o una relación inválida. Verifica el correo y los datos obligatorios.";
        if (detalle.contains("reserva_sin_solapamiento")) {
            mensaje = "El alojamiento ya cuenta con una reserva confirmada en las fechas seleccionadas.";
        } else if (detalle.contains("clave_idempotencia")) {
            mensaje = "Esta solicitud de reserva ya fue procesada previamente.";
        }

        return response(HttpStatus.CONFLICT, mensaje, request, Map.of());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> constraint(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        return response(
                HttpStatus.BAD_REQUEST,
                "Hay datos que no cumplen las validaciones.",
                request,
                Map.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> unreadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return response(
                HttpStatus.BAD_REQUEST,
                "El formato de los datos enviados no es válido.",
                request,
                Map.of());
    }

    private ResponseEntity<Object> response(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> fields) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());

        if (!fields.isEmpty()) {
            body.put("fieldErrors", fields);
        }

        return ResponseEntity.status(status).body(body);
    }
}