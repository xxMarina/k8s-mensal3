package com.marmitech.Marmitech.DTO.RequestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClienteRequestDTO(
        @NotNull @NotBlank String nome,
        @NotNull @NotBlank String email,
        @NotNull @NotBlank String telefone,
        @NotNull @NotBlank String cpfCnpj,
        @NotNull @NotBlank String endereco
) {
}
