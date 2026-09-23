package com.prova.api.service;

import com.prova.api.dto.ProdutoRequest;
import com.prova.api.exception.ResourceNotFoundException;
import com.prova.api.model.Operacao;
import com.prova.api.model.Produto;
import com.prova.api.observer.ProdutoEvento;
import com.prova.api.observer.ProdutoObserver;
import com.prova.api.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @Mock
    private ProdutoObserver observer1;

    @Mock
    private ProdutoObserver observer2;

    private ProdutoService service;

    private Produto produto;

    @BeforeEach
    void setUp() {
        service = new ProdutoService(repository, List.of(observer1, observer2));
        produto = new Produto("Teclado", "Teclado mecanico", new BigDecimal("199.90"), 5);
        produto.setId(1L);
    }

    @Test
    void listar_deveRetornarTodosOsProdutos() {
        when(repository.findAll()).thenReturn(List.of(produto));

        List<Produto> resultado = service.listar();

        assertThat(resultado).containsExactly(produto);
        verify(repository).findAll();
    }

    @Test
    void buscarPorId_quandoExiste_deveRetornarProduto() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));

        Produto resultado = service.buscarPorId(1L);

        assertThat(resultado).isEqualTo(produto);
    }

    @Test
    void buscarPorId_quandoNaoExiste_deveLancarExcecao() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void criar_deveSalvarENotificarObserversComCreate() {
        ProdutoRequest request = new ProdutoRequest("Teclado", "Teclado mecanico", new BigDecimal("199.90"), 5);
        when(repository.save(any(Produto.class))).thenReturn(produto);

        Produto resultado = service.criar(request);

        assertThat(resultado).isEqualTo(produto);

        ArgumentCaptor<Produto> salvo = ArgumentCaptor.forClass(Produto.class);
        verify(repository).save(salvo.capture());
        assertThat(salvo.getValue().getNome()).isEqualTo("Teclado");
        assertThat(salvo.getValue().getDescricao()).isEqualTo("Teclado mecanico");
        assertThat(salvo.getValue().getPreco()).isEqualByComparingTo("199.90");
        assertThat(salvo.getValue().getQuantidade()).isEqualTo(5);

        ProdutoEvento esperado = new ProdutoEvento(Operacao.CREATE, produto);
        verify(observer1).notificar(esperado);
        verify(observer2).notificar(esperado);
    }

    @Test
    void excluir_quandoExiste_deveRemoverENotificarObserversComDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));

        service.excluir(1L);

        verify(repository).delete(produto);
        ProdutoEvento esperado = new ProdutoEvento(Operacao.DELETE, produto);
        verify(observer1).notificar(esperado);
        verify(observer2).notificar(esperado);
    }

    @Test
    void excluir_quandoNaoExiste_deveLancarExcecaoSemNotificar() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).delete(any());
        verifyNoInteractions(observer1, observer2);
    }
}
