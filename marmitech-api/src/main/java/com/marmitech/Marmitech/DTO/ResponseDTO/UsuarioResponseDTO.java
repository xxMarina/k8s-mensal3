package com.marmitech.Marmitech.DTO.ResponseDTO;

import java.time.LocalDate;

public record UsuarioResponseDTO(
        int id,
        String nome,
        String email,
        String cargo,
        LocalDate dataCriacao
) {
}
