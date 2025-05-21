package com.emiteai.presentation.controller;

import com.emiteai.application.dto.PessoaRequest;
import com.emiteai.application.dto.PessoaResponse;
import com.emiteai.application.service.PessoaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pessoas")
@RequiredArgsConstructor
public class PessoaController {
    
    private final PessoaService pessoaService;
    private final AmqpTemplate amqpTemplate;
    
    @PostMapping
    public ResponseEntity<PessoaResponse> cadastrar(@Valid @RequestBody PessoaRequest request) {
        return ResponseEntity.ok(pessoaService.cadastrar(request));
    }
    
    @PostMapping("/relatorio")
    public ResponseEntity<String> gerarRelatorio() {
        amqpTemplate.convertAndSend("fila-relatorio-pessoas", "gerar");
        return ResponseEntity.accepted().body("Relatório está sendo gerado");
    }
}