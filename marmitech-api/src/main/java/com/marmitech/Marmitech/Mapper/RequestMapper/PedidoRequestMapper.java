package com.marmitech.Marmitech.Mapper.RequestMapper;

import com.marmitech.Marmitech.DTO.RequestDTO.PedidoRequestDTO;
import com.marmitech.Marmitech.Entity.*;

public class PedidoRequestMapper {

    public static Pedido toEntity(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setValorTotal(dto.valorTotal());
        pedido.setStatus(dto.status());
        pedido.setEnderecoEntrega(dto.enderecoEntrega());

        if (dto.cliente() != null && dto.cliente().id() != null && dto.cliente().id() > 0) {
            Cliente cliente = new Cliente();
            cliente.setId(dto.cliente().id());
            pedido.setCliente(cliente);
        }

        if (dto.pedidoItems() != null) {
            dto.pedidoItems().forEach(itemDto -> {
                PedidoItem item = new PedidoItem();
                item.setQuantidade(itemDto.quantidade());
                item.setPrecoUnitarioPedido(itemDto.precoUnitarioPedido());
                if (itemDto.produto() != null && itemDto.produto().id() != null && itemDto.produto().id() > 0) {
                    Produto produto = new Produto();
                    produto.setId(itemDto.produto().id());
                    item.setProduto(produto);
                }
                pedido.addItem(item);
            });
        }

        return pedido;
    }
}
