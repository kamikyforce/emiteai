package com.emiteai.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pessoas")
public class Pessoa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private String telefone;
    
    @Column(nullable = false, unique = true)
    private String cpf;
    
    @Column(nullable = false)
    private String numero;
    
    private String complemento;
    
    @Column(nullable = false)
    private String cep;
    
    @Column(nullable = false)
    private String bairro;
    
    @Column(name = "municipio", nullable = false)
    private String municipio;
    
    @Column(name = "estado", nullable = false)
    private String estado;
}