package com.prova.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * TODO NA PROVA: renomear esta classe para a entidade pedida no enunciado
 * (ex: Curso, Reserva, Produto...) e ajustar os atributos.
 *
 * Já vem pronto:
 * - id autogerado
 * - nome (usado no exemplo de filtro startsWith)
 * - deleted (para soft delete)
 */
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    // Soft delete: nunca removemos a linha do banco, só marcamos.
    private boolean deleted = false;

    public Item() {
    }

    public Item(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
