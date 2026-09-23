package com.prova.api.observer;

// Padrao Observer: todo observer registrado no ProdutoService recebe os eventos.
public interface ProdutoObserver {

    void notificar(ProdutoEvento evento);
}
