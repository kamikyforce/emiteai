package com.emiteai.application.service;

import com.emiteai.application.dto.PessoaRequest;
import com.emiteai.presentation.controller.PessoaController;
import com.emiteai.presentation.interceptor.AuditoriaInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.anyOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PessoaController.class)
@Import(PessoaControllerTest.Config.class)
class PessoaControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    PessoaService pessoaService;

    @MockBean
    AmqpTemplate amqp;

    @MockBean
    AuditoriaService auditoriaService;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void deveAceitarGeracaoDeRelatorio() throws Exception {
        mvc.perform(post("/api/pessoas/relatorio"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Relatório está sendo gerado"));

        verify(amqp).convertAndSend(eq("fila-relatorio-pessoas"), eq("gerar"));
    }

    @Test
    void deveValidarPayloadDeCadastro() throws Exception {
        PessoaRequest req = new PessoaRequest(); // campos nulos

        mvc.perform(post("/api/pessoas")
                .contentType("application/json")
                .content(mapper.writeValueAsBytes(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem", anyOf(
                        containsString("Campo inválido"),
                        containsString("telefone"),
                        containsString("cep"),
                        containsString("nome")
                )));
    }

    @TestConfiguration
    static class Config {
        @Bean
        public AuditoriaInterceptor auditoriaInterceptor(AuditoriaService auditoriaService) {
            return new AuditoriaInterceptor(auditoriaService);
        }
    }
}
