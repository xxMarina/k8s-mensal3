package com.marmitech.Marmitech.DTO.RequestDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoriaRequestDTO(
        @NotNull @NotBlank String nome,
        String descricao
) {
}
