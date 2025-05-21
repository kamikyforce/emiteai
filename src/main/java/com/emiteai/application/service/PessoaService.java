package com.emiteai.application.service;

import com.emiteai.application.dto.PessoaRequest;
import com.emiteai.application.dto.PessoaResponse;
import com.emiteai.domain.entity.Pessoa;
import com.emiteai.infrastructure.integration.viacep.ViaCepClient;
import com.emiteai.infrastructure.integration.viacep.ViaCepResponse;
import com.emiteai.infrastructure.persistence.repository.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PessoaService {
    
    private final PessoaRepository pessoaRepository;
    private final ViaCepClient viaCepClient;
    private final AuditoriaService auditoriaService;
    
    @Transactional
    public PessoaResponse cadastrar(PessoaRequest request) {
        if (pessoaRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        
        String cepLimpo = request.getCep().replace("-", "");
        ViaCepResponse endereco = viaCepClient.consultarCep(cepLimpo);
        
        if (endereco.getErro() != null && endereco.getErro()) {
            throw new IllegalArgumentException("CEP não encontrado");
        }
        
        Pessoa pessoa = Pessoa.builder()
            .nome(request.getNome())
            .telefone(request.getTelefone())
            .cpf(request.getCpf())
            .numero(request.getNumero())
            .complemento(request.getComplemento())
            .cep(request.getCep())
            .bairro(endereco.getBairro())
            .municipio(endereco.getLocalidade())
            .estado(endereco.getUf())
            .build();
            
        pessoa = pessoaRepository.save(pessoa);
        
        auditoriaService.registrarOperacao(
            "CADASTRO",
            "PESSOA",
            pessoa.getId(),
            null,
            pessoa
        );
        
        return mapToResponse(pessoa);
    }
    
    private PessoaResponse mapToResponse(Pessoa pessoa) {
        PessoaResponse response = new PessoaResponse();
        response.setId(pessoa.getId());
        response.setNome(pessoa.getNome());
        response.setTelefone(pessoa.getTelefone());
        response.setCpf(pessoa.getCpf());
        response.setNumero(pessoa.getNumero());
        response.setComplemento(pessoa.getComplemento());
        response.setCep(pessoa.getCep());
        response.setBairro(pessoa.getBairro());
        response.setMunicipio(pessoa.getMunicipio());
        response.setEstado(pessoa.getEstado());
        return response;
    }
}