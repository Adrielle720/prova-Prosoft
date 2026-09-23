package com.prova.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prova.api.dto.ItemRequest;
import com.prova.api.model.Item;
import com.prova.api.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de INTEGRAÇÃO: sobe o contexto Spring inteiro + banco H2 em memória
 * (ver src/test/resources/application.properties) e testa via HTTP real
 * (MockMvc) as 3 rotas pedidas no enunciado-modelo:
 *   GET /itens (com filtro startsWith, sem deletados)
 *   POST /itens
 *   DELETE /itens/{id} (soft delete)
 *
 * TODO NA PROVA: renomear "/itens" para a rota do enunciado.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ItemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    @Test
    void deveCriarEListarItem() throws Exception {
        ItemRequest request = new ItemRequest();
        request.setNome("Curso de Java");

        mockMvc.perform(post("/itens")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Curso de Java")))
                .andExpect(jsonPath("$.id", notNullValue()));

        mockMvc.perform(get("/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Curso de Java")));
    }

    @Test
    void deveFiltrarPorNomeComStartsWith() throws Exception {
        repository.save(new Item("Java Básico"));
        repository.save(new Item("Java Avançado"));
        repository.save(new Item("Python Básico"));

        mockMvc.perform(get("/itens").param("nome", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void naoDeveListarItemDeletado() throws Exception {
        Item item = repository.save(new Item("Curso Antigo"));

        mockMvc.perform(delete("/itens/" + item.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deveRetornar404AoDeletarIdInexistente() throws Exception {
        mockMvc.perform(delete("/itens/9999"))
                .andExpect(status().isNotFound());
    }
}
