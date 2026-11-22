package co.edu.unbosque.backcaoba.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@SuppressWarnings("PMD.LooseCoupling")
public class ApiExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> validation(
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
        public ResponseEntity<Map<String, Object>> business(
                        ResponseStatusException ex,
                        HttpServletRequest request) {

                HttpStatus status = HttpStatus.valueOf(
                                ex.getStatusCode().value());

                String message = Objects.requireNonNullElse(
                                ex.getReason(),
                                "No fue posible procesar la solicitud.");

                return response(status, message, request, Map.of());
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<Map<String, Object>> conflict(
                        DataIntegrityViolationException ex,
                        HttpServletRequest request) {

                return response(
                                HttpStatus.CONFLICT,
                                "No se pudo guardar la información porque existe un dato duplicado o una relación inválida. Verifica el correo y los datos obligatorios.",
                                request,
                                Map.of());
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<Map<String, Object>> constraint(
                        ConstraintViolationException ex,
                        HttpServletRequest request) {

                return response(
                                HttpStatus.BAD_REQUEST,
                                "Hay datos que no cumplen las validaciones.",
                                request,
                                Map.of());
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<Map<String, Object>> unreadable(
                        HttpMessageNotReadableException ex,
                        HttpServletRequest request) {

                return response(
                                HttpStatus.BAD_REQUEST,
                                "El formato de los datos enviados no es válido.",
                                request,
                                Map.of());
        }

        private ResponseEntity<Map<String, Object>> response(
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
