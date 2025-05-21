package com.emiteai.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuditoriaServiceTest {

    @Mock
    EntityManager em;
    @Mock
    Query query;
    AuditoriaService service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyInt(), any())).thenReturn(query);
        service = new AuditoriaService(em, new ObjectMapper());
    }

    @Test
    void deveRegistrarAuditoria() {
        service.registrarOperacao("TESTE", "ENTIDADE", 1L, null, new Dados("x"));
        verify(em).createNativeQuery(argThat(sql -> sql.trim().startsWith("INSERT INTO auditoria")));
        verify(query, times(5)).setParameter(anyInt(), any());
        verify(query).executeUpdate();
    }

    record Dados(String foo) {
    }
}
