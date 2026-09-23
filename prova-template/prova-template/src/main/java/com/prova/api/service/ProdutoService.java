package com.prova.api.service;

import com.prova.api.dto.ProdutoRequest;
import com.prova.api.exception.ResourceNotFoundException;
import com.prova.api.model.Operacao;
import com.prova.api.model.Produto;
import com.prova.api.observer.ProdutoEvento;
import com.prova.api.observer.ProdutoObserver;
import com.prova.api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Subject do padrao Observer: o Spring injeta todos os beans ProdutoObserver
// (AuditoriaObserver e EstoqueBaixoObserver) e o service notifica cada um.
@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final List<ProdutoObserver> observers;

    public ProdutoService(ProdutoRepository repository, List<ProdutoObserver> observers) {
        this.repository = repository;
        this.observers = observers;
    }

    public List<Produto> listar() {
        return repository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
    }

    public Produto criar(ProdutoRequest request) {
        Produto produto = new Produto(request.getNome(), request.getDescricao(),
                request.getPreco(), request.getQuantidade());
        Produto salvo = repository.save(produto);
        notificarObservers(new ProdutoEvento(Operacao.CREATE, salvo));
        return salvo;
    }

    public void excluir(Long id) {
        Produto produto = buscarPorId(id);
        repository.delete(produto);
        notificarObservers(new ProdutoEvento(Operacao.DELETE, produto));
    }

    private void notificarObservers(ProdutoEvento evento) {
        observers.forEach(observer -> observer.notificar(evento));
    }
}
