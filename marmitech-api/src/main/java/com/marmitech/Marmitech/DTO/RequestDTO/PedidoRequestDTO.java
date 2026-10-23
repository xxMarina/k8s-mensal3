package com.marmitech.Marmitech.DTO.RequestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PedidoRequestDTO(
        Double valorTotal,
        String status,
        String enderecoEntrega,
        EntityId cliente,
        Set<ItemPedido> pedidoItems
) {
    public record EntityId(Integer id) {}
    public record ItemPedido(Integer quantidade, Double precoUnitarioPedido, EntityId produto) {}
}
