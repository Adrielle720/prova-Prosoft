package com.prova.api.observer;

import com.prova.api.model.Auditoria;
import com.prova.api.repository.AuditoriaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// Observable 1: persiste no banco cada acao (CREATE e DELETE) com timestamp e operacao.
@Component
public class AuditoriaObserver implements ProdutoObserver {

    private final AuditoriaRepository repository;

    public AuditoriaObserver(AuditoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void notificar(ProdutoEvento evento) {
        repository.save(new Auditoria(evento.operacao(), LocalDateTime.now(), evento.produto().getId()));
    }
}
