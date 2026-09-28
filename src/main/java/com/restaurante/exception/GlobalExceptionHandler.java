package com.restaurante.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.restaurante.model.dto.response.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Manejador global de errores: convierte cualquier excepcion en un {@link ErrorResponseDTO} uniforme.
 *
 * <p>Hereda de {@link ResponseEntityExceptionHandler}, que ya sabe traducir a codigos HTTP
 * correctos las excepciones propias de Spring MVC. Antes esas excepciones caian en el handler
 * generico y respondian <b>500</b> cuando en realidad eran errores del cliente:</p>
 * <ul>
 *   <li>{@code HttpMessageNotReadableException} (JSON mal formado o enum con valor invalido) -&gt; 400</li>
 *   <li>{@code MethodArgumentTypeMismatchException} (ej. {@code /platos/abc}) -&gt; 400</li>
 *   <li>{@code MissingServletRequestParameterException} (falta un {@code @RequestParam}) -&gt; 400</li>
 *   <li>{@code HttpRequestMethodNotSupportedException} (verbo HTTP incorrecto) -&gt; 405</li>
 * </ul>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ----------------------------- Errores del dominio -----------------------------

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(RecursoNoEncontradoException ex,
                                                           HttpServletRequest request) {
        log.warn("RecursoNoEncontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(404, "Not Found", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflicto(ConflictoException ex,
                                                            HttpServletRequest request) {
        log.warn("Conflicto: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(409, "Conflict", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler({EstadoInvalidoException.class, ReglaDeNegocioException.class})
    public ResponseEntity<ErrorResponseDTO> handleNegocio(RuntimeException ex,
                                                          HttpServletRequest request) {
        log.warn("ReglaDeNegocio: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildError(422, "Unprocessable Entity", ex.getMessage(), request.getRequestURI()));
    }

    // ------------------------ Errores de la capa web (Spring MVC) ------------------------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validacion de entrada fallida: {}", mensaje);
        return respuestaError(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        log.warn("Cuerpo de la peticion ilegible: {}", ex.getMessage());
        return respuestaError(HttpStatus.BAD_REQUEST,
                "El cuerpo de la peticion no es un JSON valido o contiene un valor no permitido", request);
    }

    /**
     * Punto unico para el resto de excepciones que Spring MVC ya clasifica
     * (405, 415, 400 por tipo de parametro, 404 de rutas no mapeadas, etc.).
     * Sin este override todas ellas terminarian respondiendo 500.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {
        if (statusCode.is5xxServerError()) {
            log.error("Error al procesar la peticion: {}", ex.getMessage(), ex);
        } else {
            log.warn("Peticion rechazada (HTTP {}): {}", statusCode.value(), ex.getMessage());
        }
        return respuestaError(statusCode, mensajeDe(ex, statusCode), request);
    }

    // ------------------------------ Ultimo recurso ------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenerico(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(500, "Internal Server Error", "Error inesperado del servidor",
                        request.getRequestURI()));
    }

    // --------------------------------- Helpers ---------------------------------

    private ResponseEntity<Object> respuestaError(HttpStatusCode statusCode, String mensaje, WebRequest request) {
        return ResponseEntity.status(statusCode)
                .body(buildError(statusCode.value(), motivoDe(statusCode), mensaje, rutaDe(request)));
    }

    private String motivoDe(HttpStatusCode statusCode) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        return status == null ? "Error" : status.getReasonPhrase();
    }

    private String mensajeDe(Exception ex, HttpStatusCode statusCode) {
        if (ex instanceof MethodArgumentTypeMismatchException mismatch) {
            return "El valor '" + mismatch.getValue() + "' no es valido para el parametro '"
                    + mismatch.getName() + "'";
        }
        if (ex instanceof MissingServletRequestParameterException faltante) {
            return "Falta el parametro obligatorio '" + faltante.getParameterName() + "'";
        }
        if (ex instanceof HttpRequestMethodNotSupportedException metodo) {
            return "El metodo HTTP " + metodo.getMethod() + " no esta soportado para esta ruta";
        }
        if (ex instanceof NoResourceFoundException noEncontrada) {
            return "Ruta no encontrada: /" + noEncontrada.getResourcePath();
        }
        return "La peticion no se pudo procesar (HTTP " + statusCode.value() + ")";
    }

    private String rutaDe(WebRequest request) {
        return request instanceof ServletWebRequest servletRequest
                ? servletRequest.getRequest().getRequestURI()
                : "";
    }

    private ErrorResponseDTO buildError(int status, String error, String message, String path) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }
}
