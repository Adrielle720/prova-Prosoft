package com.prova.api.service;

import com.prova.api.dto.ItemRequest;
import com.prova.api.exception.ResourceNotFoundException;
import com.prova.api.model.Item;
import com.prova.api.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository repository;

    @Autowired
    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    // GET /itens (com filtro opcional startsWith)
    public List<Item> listar(String filtroNome) {
        if (filtroNome == null || filtroNome.isBlank()) {
            return repository.findByDeletedFalse();
        }
        return repository.findByNomeStartingWithIgnoreCaseAndDeletedFalse(filtroNome);
    }

    // POST /itens
    public Item criar(ItemRequest request) {
        Item item = new Item(request.getNome());
        return repository.save(item);
    }

    // DELETE /itens/{id} -> soft delete
    public void deletar(Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado: " + id));
        item.setDeleted(true);
        repository.save(item);
    }
}
