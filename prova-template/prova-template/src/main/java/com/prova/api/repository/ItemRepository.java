package com.prova.api.repository;

import com.prova.api.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Lista todos os não deletados
    List<Item> findByDeletedFalse();

    // Filtro "startsWith" pelo nome, ignorando deletados
    // Spring Data JPA já gera o SQL a partir do nome do método - nada a implementar.
    List<Item> findByNomeStartingWithIgnoreCaseAndDeletedFalse(String nome);
}
