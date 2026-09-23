package com.prova.api.service;

import com.prova.api.dto.ItemRequest;
import com.prova.api.exception.ResourceNotFoundException;
import com.prova.api.model.Item;
import com.prova.api.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Teste de UNIDADE (Mockito mocka o repository, não toca banco nenhum).
 * TODO NA PROVA: duplicar/ajustar estes métodos para cada regra nova que
 * você adicionar no service, mantendo 100% de cobertura na camada de serviço.
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository repository;

    @InjectMocks
    private ItemService service;

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item("Item Teste");
        item.setId(1L);
    }

    @Test
    void listar_semFiltro_deveRetornarTodosNaoDeletados() {
        when(repository.findByDeletedFalse()).thenReturn(List.of(item));

        List<Item> resultado = service.listar(null);

        assertThat(resultado).hasSize(1);
        verify(repository).findByDeletedFalse();
        verify(repository, never()).findByNomeStartingWithIgnoreCaseAndDeletedFalse(anyString());
    }

    @Test
    void listar_comFiltroVazio_deveRetornarTodosNaoDeletados() {
        when(repository.findByDeletedFalse()).thenReturn(List.of(item));

        List<Item> resultado = service.listar("  ");

        assertThat(resultado).hasSize(1);
        verify(repository).findByDeletedFalse();
    }

    @Test
    void listar_comFiltro_deveUsarStartsWith() {
        when(repository.findByNomeStartingWithIgnoreCaseAndDeletedFalse("Ite"))
                .thenReturn(List.of(item));

        List<Item> resultado = service.listar("Ite");

        assertThat(resultado).hasSize(1);
        verify(repository).findByNomeStartingWithIgnoreCaseAndDeletedFalse("Ite");
        verify(repository, never()).findByDeletedFalse();
    }

    @Test
    void criar_deveSalvarNoRepository() {
        ItemRequest request = new ItemRequest();
        request.setNome("Novo Item");
        when(repository.save(any(Item.class))).thenReturn(item);

        Item resultado = service.criar(request);

        assertThat(resultado).isEqualTo(item);
        verify(repository).save(any(Item.class));
    }

    @Test
    void deletar_quandoExiste_deveMarcarDeletedTrue() {
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        service.deletar(1L);

        assertThat(item.isDeleted()).isTrue();
        verify(repository).save(item);
    }

    @Test
    void deletar_quandoNaoExiste_deveLancarExcecao() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deletar(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).save(any());
    }
}
