package com.prova.api.observer;

import com.prova.api.model.Auditoria;
import com.prova.api.model.Operacao;
import com.prova.api.model.Produto;
import com.prova.api.repository.AuditoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditoriaObserverTest {

    @Mock
    private AuditoriaRepository repository;

    @InjectMocks
    private AuditoriaObserver observer;

    @Test
    void notificar_devePersistirOperacaoETimestamp() {
        Produto produto = new Produto("Mouse", "Mouse sem fio", new BigDecimal("50.00"), 20);
        produto.setId(7L);

        observer.notificar(new ProdutoEvento(Operacao.DELETE, produto));

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getOperacao()).isEqualTo(Operacao.DELETE);
        assertThat(captor.getValue().getProdutoId()).isEqualTo(7L);
        assertThat(captor.getValue().getTimestamp()).isNotNull();
    }
}
