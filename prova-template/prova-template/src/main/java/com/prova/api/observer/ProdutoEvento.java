package com.prova.api.observer;

import com.prova.api.model.Operacao;
import com.prova.api.model.Produto;

// Evento publicado pelo ProdutoService (subject) para os observers.
public record ProdutoEvento(Operacao operacao, Produto produto) {
}
