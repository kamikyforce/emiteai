package com.emiteai.application.dto;

import lombok.Data;

@Data
public class PessoaResponse {
    private Long id;
    private String nome;
    private String telefone;
    private String cpf;
    private String numero;
    private String complemento;
    private String cep;
    private String bairro;
    private String municipio;
    private String estado;
}