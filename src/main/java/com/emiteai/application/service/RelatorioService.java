package com.emiteai.application.service;

import com.emiteai.domain.entity.Pessoa;
import com.emiteai.infrastructure.persistence.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelatorioService {

    private static String DIR_NAME         = "relatorios";
    private static final String PREFIX     = "relatorio-pessoas-";
    private static final String SUFFIX     = ".csv";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final PessoaRepository pessoaRepository;

    @RabbitListener(queues = "fila-relatorio-pessoas")
    public void gerarRelatorio(@SuppressWarnings("unused") String mensagem) {
        try {
            Path dir = Paths.get(DIR_NAME);
            if (Files.notExists(dir)) Files.createDirectories(dir);

            String nomeArquivo = PREFIX + LocalDateTime.now().format(FMT) + SUFFIX;
            Path arquivo = dir.resolve(nomeArquivo);

            try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(arquivo))) {
                w.println("ID,Nome,Telefone,CPF,Número,Complemento,CEP,Bairro,Município,Estado");
                List<Pessoa> pessoas = pessoaRepository.findAll();
                pessoas.forEach(p -> w.printf(
                        "%d,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        p.getId(), p.getNome(), p.getTelefone(), p.getCpf(), p.getNumero(),
                        p.getComplemento(), p.getCep(), p.getBairro(), p.getMunicipio(), p.getEstado()));
            }

            log.info("Relatório gerado em {}", arquivo.toAbsolutePath());
        } catch (Exception e) {
            log.error("Erro ao gerar relatório", e);
            throw new RuntimeException("Falha ao gerar relatório", e);
        }
    }

    
    public Path obterUltimoRelatorio() {
        try {
            Path dir = Paths.get(DIR_NAME);
            return Files.list(dir)
                    .filter(p -> p.getFileName().toString().startsWith(PREFIX))
                    .filter(p -> p.getFileName().toString().endsWith(SUFFIX))
                    .max(Comparator.comparingLong(this::lastModified))
                    .orElseThrow(() -> new FileNotFoundException("Nenhum relatório encontrado"));
        } catch (IOException e) {
            throw new RuntimeException("Falha ao localizar relatório", e);
        }
    }

    private long lastModified(Path p) {
        try { return Files.getLastModifiedTime(p).toMillis(); }
        catch (IOException e) { return 0L; }
    }
}
