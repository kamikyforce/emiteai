package com.emiteai.application.dto;

import org.hibernate.validator.constraints.br.CPF;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PessoaRequest {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\([1-9]{2}\\) (?:[2-8]|9[1-9])[0-9]{3}\\-[0-9]{4}$", message = "Telefone inválido")
    private String telefone;
    
    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$", message = "CPF deve estar no formato XXX.XXX.XXX-XX")
    private String cpf;
    
    @NotBlank(message = "Número é obrigatório")
    private String numero;
    
    private String complemento;
    
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^[0-9]{5}\\-[0-9]{3}$", message = "CEP inválido")
    private String cep;
}