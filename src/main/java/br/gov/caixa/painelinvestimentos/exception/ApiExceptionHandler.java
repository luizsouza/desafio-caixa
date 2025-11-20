package br.gov.caixa.painelinvestimentos.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String MSG_UNREADABLE_BODY =
            "Corpo da requisição inválido. Verifique se o JSON está bem formado e se os campos numéricos contêm apenas números.";
    private static final String MSG_INVALID_FIELD_TEMPLATE =
            "O campo '%s' recebeu um valor inválido (%s). Ajuste o formato e tente novamente.";
    private static final String MSG_INVALID_PARAMETER_TEMPLATE =
            "O parâmetro '%s' recebeu o valor '%s' em um formato inválido. Use o padrão indicado na documentação (ex.: AAAA-MM-DD).";
    private static final String DEFAULT_PARAMETER_NAME = "parâmetro";
    private static final String VALIDATION_MESSAGE = "Erro de validação nos campos enviados";

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        String message = MSG_UNREADABLE_BODY;
        Throwable rootCause = ex.getMostSpecificCause();
        if (rootCause instanceof InvalidFormatException invalidFormat && !invalidFormat.getPath().isEmpty()) {
            String field = invalidFormat.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : Integer.toString(ref.getIndex()))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("campo");
            Object value = invalidFormat.getValue();
            message = String.format(MSG_INVALID_FIELD_TEMPLATE, field, value);
        }

        return buildError(HttpStatus.BAD_REQUEST, "Bad Request", message, request);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, BindException.class, DateTimeParseException.class})
    public ResponseEntity<ApiError> handleInvalidParameters(
            Exception ex,
            HttpServletRequest request) {

        String parameter = DEFAULT_PARAMETER_NAME;
        String value = "";
        if (ex instanceof MethodArgumentTypeMismatchException mismatch) {
            parameter = mismatch.getName();
            value = Objects.toString(mismatch.getValue(), "");
        } else if (ex instanceof BindException bindException && bindException.getFieldError() != null) {
            parameter = bindException.getFieldError().getField();
            value = Objects.toString(bindException.getFieldError().getRejectedValue(), "");
        } else if (ex instanceof DateTimeParseException parse) {
            value = parse.getParsedString();
        }

        String message = String.format(MSG_INVALID_PARAMETER_TEMPLATE, parameter, value);

        return buildError(HttpStatus.BAD_REQUEST, "Bad Request", message, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return buildError(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request) {

        return buildError(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                VALIDATION_MESSAGE,
                request.getRequestURI()
        );

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.put(err.getField(), err.getDefaultMessage()));

        error.setFields(fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(SemDadosPerfilException.class)
    public ResponseEntity<ApiError> handleMissingPerfil(
            SemDadosPerfilException ex,
            HttpServletRequest request) {

        return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request);
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status,
                                                String error,
                                                String message,
                                                HttpServletRequest request) {
        ApiError apiError = new ApiError(
                status.value(),
                error,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(apiError);
    }
}
