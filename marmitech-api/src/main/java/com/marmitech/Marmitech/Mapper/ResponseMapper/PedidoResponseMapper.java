package com.marmitech.Marmitech.Mapper.ResponseMapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.marmitech.Marmitech.DTO.ResponseDTO.PedidoItemResponseDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.PedidoResponseDTO;
import com.marmitech.Marmitech.Entity.Cliente;
import com.marmitech.Marmitech.Entity.Pedido;
import com.marmitech.Marmitech.Entity.PedidoItem;
import com.marmitech.Marmitech.Entity.Produto;

public class PedidoResponseMapper {

    private static PedidoItemResponseDTO toItemDto(PedidoItem pedidoItem) {
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
                pedidoItem.getSubtotal());
    }

    public static PedidoResponseDTO toDto(Pedido pedido) {
        Set<PedidoItemResponseDTO> pedidoItemResponseDTOs = pedido.getPedidoItems()
                .stream()
                .map( PedidoResponseMapper::toItemDto )
                .collect( Collectors.toSet() );

        String nomeCliente = (pedido.getCliente() != null)
                ? pedido.getCliente().getNome()
                : "Cliente não informado";

        return new PedidoResponseDTO(
                pedido.getId(),
                nomeCliente,
                pedido.getStatus(),
                pedido.getEnderecoEntrega(),
                pedidoItemResponseDTOs,
                pedido.getValorTotal(),
                pedido.getDataPedido()
        );
    }
}