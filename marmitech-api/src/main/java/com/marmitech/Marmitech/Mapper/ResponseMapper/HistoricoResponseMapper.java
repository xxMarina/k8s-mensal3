package com.marmitech.Marmitech.Mapper.ResponseMapper;

import com.marmitech.Marmitech.DTO.ResponseDTO.HistoricoResponseDTO;
import com.marmitech.Marmitech.Entity.HistoricoCompra;

public class HistoricoResponseMapper {
    public static HistoricoResponseDTO toDto(HistoricoCompra historico) {
        int pedidoId = (historico.getPedido() != null) ? historico.getPedido().getId() : 0;

        return new HistoricoResponseDTO(
                historico.getId(),
                historico.getTipoEvento(),
                historico.getDataEvento(),
                pedidoId
        );
    }
}