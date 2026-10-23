package com.marmitech.Marmitech.DTO.RequestDTO;

public record HistoricoSaveDTO(
        int pedidoId,
        String descricao,
        String dataEvento,
        String tipoEvento
) {
}