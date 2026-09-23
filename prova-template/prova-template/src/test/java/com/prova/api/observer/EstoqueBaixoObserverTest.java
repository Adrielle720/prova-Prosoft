package com.prova.api.observer;

import com.prova.api.model.Operacao;
import com.prova.api.model.Produto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class EstoqueBaixoObserverTest {

    private final EstoqueBaixoObserver observer = new EstoqueBaixoObserver();

    private Produto produtoComQuantidade(int quantidade) {
        Produto produto = new Produto("Cabo HDMI", "2 metros", new BigDecimal("30.00"), quantidade);
        produto.setId(3L);
        return produto;
    }

    @Test
    void create_comQuantidadeMenorQue10_deveLogarEstoqueBaixo(CapturedOutput output) {
        observer.notificar(new ProdutoEvento(Operacao.CREATE, produtoComQuantidade(9)));

        assertThat(output).contains("estoque baixo", "Cabo HDMI");
    }

    @Test
    void create_comQuantidade10_naoDeveLogar(CapturedOutput output) {
        observer.notificar(new ProdutoEvento(Operacao.CREATE, produtoComQuantidade(10)));

        assertThat(output).doesNotContain("estoque baixo");
    }

    @Test
    void delete_naoDeveLogarMesmoComEstoqueBaixo(CapturedOutput output) {
        observer.notificar(new ProdutoEvento(Operacao.DELETE, produtoComQuantidade(1)));

        assertThat(output).doesNotContain("estoque baixo");
    }
}
