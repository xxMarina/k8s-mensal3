package com.marmitech.Marmitech.DTO.ResponseDTO;

public record HistoricoResponseDTO(
        int id,
        String tipoEvento,
        String dataEvento,
        int pedidoId
) {
}