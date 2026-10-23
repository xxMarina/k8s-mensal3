package com.marmitech.Marmitech.DTO.ResponseDTO;

public record ClienteResponseDTO(
        int id,
        String nome,
        String email,
        String telefone,
        String cpfCnpj,
        String endereco,
        String dataCadastro
) {
}
