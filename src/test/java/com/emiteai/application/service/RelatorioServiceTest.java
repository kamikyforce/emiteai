package com.emiteai.application.service;

import com.emiteai.domain.entity.Pessoa;
import com.emiteai.infrastructure.persistence.repository.PessoaRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import java.nio.file.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RelatorioServiceTest {

    @TempDir Path tempDir;
    @Mock   PessoaRepository repository;
    @InjectMocks RelatorioService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        TestUtils.setStaticField(RelatorioService.class, "DIR_NAME", tempDir.toString());
    }

    @Test
    void deveGerarCsvComTodosOsRegistros() throws Exception {
        when(repository.findAll()).thenReturn(List.of(
                Pessoa.builder().id(1L).nome("Ana").telefone("...").cpf("111.111.111-11")
                      .numero("10").cep("01000-000").bairro("Centro")
                      .municipio("SP").estado("SP").build()));

        service.gerarRelatorio("trigger");

        try (var files = Files.list(tempDir)) {
            Path csv = files.findFirst().orElseThrow();
            assertThat(Files.readString(csv))
                    .contains("Ana")
                    .contains("CPF");
        }
    }
}
