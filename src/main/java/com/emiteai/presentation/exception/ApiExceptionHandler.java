package com.emiteai.presentation.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    public record ErrorDTO(int status, String mensagem) {}

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDTO> duplicatedKey(DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorDTO(409, "CPF já cadastrado"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> validation(MethodArgumentNotValidException ex) {
        String field = ex.getBindingResult()
                         .getFieldErrors().stream()
                         .findFirst()
                         .map(FieldError::getField)
                         .orElse("dados");
        return ResponseEntity
                .unprocessableEntity()
                .body(new ErrorDTO(422, "Campo inválido: " + field));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> business(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorDTO(400, ex.getMessage()));
    }

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<ErrorDTO> viacep(feign.FeignException ex) {
        HttpStatus status = ex.status() == 400 ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
        String msg       = ex.status() == 400 ? "CEP inválido" : "Erro ao consultar CEP";
        return ResponseEntity.status(status).body(new ErrorDTO(status.value(), msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> unknown(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDTO(500, "Falha inesperada"));
    }
}
