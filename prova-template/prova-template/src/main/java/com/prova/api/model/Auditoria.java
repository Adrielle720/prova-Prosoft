package com.prova.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

// Registro de auditoria gravado pelo AuditoriaObserver a cada CREATE/DELETE.
@Entity
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Operacao operacao;

    private LocalDateTime timestamp;

    private Long produtoId;

    public Auditoria() {
    }

    public Auditoria(Operacao operacao, LocalDateTime timestamp, Long produtoId) {
        this.operacao = operacao;
        this.timestamp = timestamp;
        this.produtoId = produtoId;
    }

    public Long getId() {
        return id;
    }

    public Operacao getOperacao() {
        return operacao;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Long getProdutoId() {
        return produtoId;
    }
}
