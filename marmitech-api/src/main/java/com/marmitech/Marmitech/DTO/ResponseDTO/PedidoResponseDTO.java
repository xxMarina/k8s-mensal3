package com.marmitech.Marmitech.DTO.ResponseDTO;

import java.util.Set;
import lombok.Builder;

@Builder
public record PedidoResponseDTO(
        //Colocando o id para aparecer no hostorico do front
        int id,
        String nomeCliente,
        String status,
        String enderecoEntrega,
        Set<PedidoItemResponseDTO> pedidoItems,
        Double valorTotal,
        String dataPedido
) {
}