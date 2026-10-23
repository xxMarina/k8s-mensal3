package com.marmitech.Marmitech.DTO.ResponseDTO;

public record PedidoItemResponseDTO(int id, int pedidoId, int produtoId, String clienteNome, String produtoNome, int quantidade, Double precoUnitarioPedido, Double subtotal) {

}