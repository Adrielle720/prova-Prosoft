package com.prova.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * TODO NA PROVA: ajustar os campos conforme os atributos pedidos no enunciado.
 */
public class ItemRequest {

    @NotBlank(message = "nome é obrigatório")
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
