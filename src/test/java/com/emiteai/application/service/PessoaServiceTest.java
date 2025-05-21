package com.emiteai.application.service;

import com.emiteai.application.dto.PessoaRequest;
import com.emiteai.domain.entity.Pessoa;
import com.emiteai.infrastructure.integration.viacep.ViaCepClient;
import com.emiteai.infrastructure.integration.viacep.ViaCepResponse;
import com.emiteai.infrastructure.persistence.repository.PessoaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;
    
    @Mock
    private ViaCepClient viaCepClient;
    
    @Mock
    private AuditoriaService auditoriaService;
    
    @InjectMocks
    private PessoaService pessoaService;
    
    @Test
    void deveCadastrarPessoaComSucesso() {
        // Arrange
        PessoaRequest request = new PessoaRequest();
        request.setNome("João Silva");
        request.setCpf("123.456.789-00");
        request.setTelefone("(11) 98765-4321");
        request.setCep("01234-567");
        request.setNumero("123");
        
        ViaCepResponse viaCepResponse = new ViaCepResponse();
        viaCepResponse.setBairro("Centro");
        viaCepResponse.setLocalidade("São Paulo");
        viaCepResponse.setUf("SP");
        
        when(viaCepClient.consultarCep(anyString())).thenReturn(viaCepResponse);
        when(pessoaRepository.save(any())).thenReturn(new Pessoa());
        
        // Act
        var response = pessoaService.cadastrar(request);
        
        // Assert
        assertNotNull(response);
        verify(pessoaRepository).save(any());
        verify(auditoriaService).registrarOperacao(eq("CADASTRO"), eq("PESSOA"), any(), any(), any());
    }
}