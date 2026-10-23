package com.marmitech.Marmitech.DTO.ResponseDTO;

public record LoginResponseDTO(
        String token,
        Integer id,
        String nome,
        String email,
        String cargo) {
}