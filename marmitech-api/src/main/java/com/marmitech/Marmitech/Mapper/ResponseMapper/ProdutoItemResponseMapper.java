package com.marmitech.Marmitech.Mapper.ResponseMapper;

import com.marmitech.Marmitech.DTO.ResponseDTO.PedidoItemResponseDTO;
import com.marmitech.Marmitech.Entity.Cliente;
import com.marmitech.Marmitech.Entity.Pedido;
import com.marmitech.Marmitech.Entity.PedidoItem;
import com.marmitech.Marmitech.Entity.Produto;

public class ProdutoItemResponseMapper {

    public static PedidoItemResponseDTO toDto(PedidoItem pedidoItem) {
        Pedido pedido = pedidoItem.getPedido();
        Produto produto = pedidoItem.getProduto();
        Cliente cliente = (pedido != null) ? pedido.getCliente() : null;

        int pedidoId = (pedido != null) ? pedido.getId() : 0;
        int produtoId = (produto != null) ? produto.getId() : 0;
        String produtoNome = (produto != null) ? produto.getNome() : "Produto não informado";
        String clienteNome = (cliente != null) ? cliente.getNome() : "Cliente não informado";

        return new PedidoItemResponseDTO(
                pedidoItem.getId(),
                pedidoId,
                produtoId,
                clienteNome,
                produtoNome,
                pedidoItem.getQuantidade(),
                pedidoItem.getPrecoUnitarioPedido(),
                pedidoItem.getSubtotal()
        );
    }
}