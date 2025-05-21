// src/main/java/com/emiteai/presentation/controller/RelatorioController.java
package com.emiteai.presentation.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

@RestController
public class RelatorioController {

    private static final Path DIR = Paths.get("relatorios");

    @GetMapping("/api/pessoas/relatorio/latest")
    public ResponseEntity<ByteArrayResource> baixarUltimo() throws IOException {
        if (!Files.exists(DIR)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Path ultimoCsv = Files.list(DIR)
                              .filter(p -> p.getFileName().toString().startsWith("relatorio-pessoas-"))
                              .max(Comparator.comparingLong(p -> p.toFile().lastModified()))
                              .orElse(null);

        if (ultimoCsv == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ByteArrayResource body = new ByteArrayResource(Files.readAllBytes(ultimoCsv));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + ultimoCsv.getFileName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(body.contentLength())
                .body(body);
    }
}
