package com.prova.api.controller;

import com.prova.api.dto.ItemRequest;
import com.prova.api.model.Item;
import com.prova.api.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO NA PROVA: trocar "/itens" pelo nome do recurso do enunciado (ex: /cursos).
 */
@RestController
@RequestMapping("/itens")
public class ItemController {

    private final ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<Item> listar(@RequestParam(required = false) String nome) {
        return service.listar(nome);
    }

    @PostMapping
    public ResponseEntity<Item> criar(@Valid @RequestBody ItemRequest request) {
        Item criado = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
