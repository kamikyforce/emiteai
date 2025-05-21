package com.emiteai.presentation.interceptor;

import com.emiteai.application.service.AuditoriaService;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditoriaInterceptor implements HandlerInterceptor {

    private final AuditoriaService auditoriaService;

    @Override
    public void afterCompletion(HttpServletRequest req,
                                HttpServletResponse res,
                                Object handler,
                                Exception ex) {

        try {
            var dadosAtuais = new AuditPayload(
                    req.getMethod(),
                    req.getRequestURI(),
                    res.getStatus());

            auditoriaService.registrarOperacao(
                    "REQUEST",
                    "HTTP",
                    null,
                    null,
                    dadosAtuais);
        } catch (Exception e) {
           
            log.warn("Falha ao auditar requisição", e);
        }
    }

    record AuditPayload(String metodo, String uri, int status) {}
}
