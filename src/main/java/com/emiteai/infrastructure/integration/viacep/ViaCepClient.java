package com.emiteai.infrastructure.integration.viacep;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "viacep-client", url = "https://viacep.com.br/ws")
public interface ViaCepClient {
    
    @GetMapping("/{cep}/json")
    ViaCepResponse consultarCep(@PathVariable("cep") String cep);
}