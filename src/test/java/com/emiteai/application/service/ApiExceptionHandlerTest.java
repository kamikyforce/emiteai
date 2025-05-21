package com.emiteai.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import com.emiteai.presentation.exception.ApiExceptionHandler;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void deveMapearDuplicidadePara409() {
        ResponseEntity<ApiExceptionHandler.ErrorDTO> res =
                handler.duplicatedKey(new DataIntegrityViolationException("dup"));
        assertThat(res.getStatusCode().value()).isEqualTo(409);
        assertThat(res.getBody().mensagem()).isEqualTo("CPF já cadastrado");
    }
}
