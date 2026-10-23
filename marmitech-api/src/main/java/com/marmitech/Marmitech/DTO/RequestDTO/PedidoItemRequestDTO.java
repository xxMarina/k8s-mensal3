package com.marmitech.Marmitech.DTO.RequestDTO;

public record PedidoItemRequestDTO(int id, int pedidoId, int produtoId, int quantidade, Double precoUnitarioPedido) {

}
