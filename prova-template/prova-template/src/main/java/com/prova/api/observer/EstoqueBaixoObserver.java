package com.prova.api.observer;

import com.prova.api.model.Operacao;
import com.prova.api.model.Produto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Observable 2: ao cadastrar um produto com quantidade < 10, loga alerta de estoque baixo.
@Component
public class EstoqueBaixoObserver implements ProdutoObserver {

    public static final int LIMITE_ESTOQUE_BAIXO = 10;

    private static final Logger log = LoggerFactory.getLogger(EstoqueBaixoObserver.class);

    @Override
    public void notificar(ProdutoEvento evento) {
        if (evento.operacao() != Operacao.CREATE) {
            return;
        }
        Produto produto = evento.produto();
        if (produto.getQuantidade() < LIMITE_ESTOQUE_BAIXO) {
            log.warn("ALERTA: estoque baixo para o produto '{}' (id={}): {} unidade(s)",
                    produto.getNome(), produto.getId(), produto.getQuantidade());
        }
    }
}
