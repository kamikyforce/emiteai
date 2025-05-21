package com.emiteai.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditoriaService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarOperacao(String tipoOperacao, String entidade, Long entidadeId, Object dadosAnteriores, Object dadosAtuais) {
        String sql = """
            INSERT INTO auditoria (tipo_operacao, entidade, entidade_id, dados_anteriores, dados_atuais)
            VALUES (?, ?, ?, ?::jsonb, ?::jsonb)
        """;
        
        try {
            String dadosAnterioresJson = dadosAnteriores != null ? objectMapper.writeValueAsString(dadosAnteriores) : null;
            String dadosAtuaisJson = dadosAtuais != null ? objectMapper.writeValueAsString(dadosAtuais) : null;
            
            entityManager.createNativeQuery(sql)
                .setParameter(1, tipoOperacao)
                .setParameter(2, entidade)
                .setParameter(3, entidadeId)
                .setParameter(4, dadosAnterioresJson)
                .setParameter(5, dadosAtuaisJson)
                .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar auditoria", e);
        }
    }
}