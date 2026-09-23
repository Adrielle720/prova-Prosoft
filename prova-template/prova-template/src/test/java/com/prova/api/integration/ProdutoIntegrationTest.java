package com.prova.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prova.api.dto.ProdutoRequest;
import com.prova.api.model.Auditoria;
import com.prova.api.model.Operacao;
import com.prova.api.repository.AuditoriaRepository;
import com.prova.api.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Teste de integracao da rota de criacao (POST /produtos) com H2 em memoria.
@SpringBootTest
@AutoConfigureMockMvc
class ProdutoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limparBanco() {
        auditoriaRepository.deleteAll();
        produtoRepository.deleteAll();
    }

    @Test
    void criar_deveRetornar201PersistirProdutoERegistrarAuditoria() throws Exception {
        ProdutoRequest request = new ProdutoRequest("Teclado", "Teclado mecanico", new BigDecimal("199.90"), 5);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Teclado")))
                .andExpect(jsonPath("$.descricao", is("Teclado mecanico")))
                .andExpect(jsonPath("$.preco", is(199.90)))
                .andExpect(jsonPath("$.quantidade", is(5)));

        assertThat(produtoRepository.findAll()).hasSize(1);

        List<Auditoria> auditorias = auditoriaRepository.findAll();
        assertThat(auditorias).hasSize(1);
        assertThat(auditorias.get(0).getOperacao()).isEqualTo(Operacao.CREATE);
        assertThat(auditorias.get(0).getTimestamp()).isNotNull();
    }

    @Test
    void criar_comDadosInvalidos_deveRetornar400ENaoPersistir() throws Exception {
        ProdutoRequest request = new ProdutoRequest("", null, null, -1);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertThat(produtoRepository.findAll()).isEmpty();
        assertThat(auditoriaRepository.findAll()).isEmpty();
    }
}
