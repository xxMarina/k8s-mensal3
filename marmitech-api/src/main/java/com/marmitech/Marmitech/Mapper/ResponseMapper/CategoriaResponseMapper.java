package com.marmitech.Marmitech.Mapper.ResponseMapper;

import com.marmitech.Marmitech.DTO.ResponseDTO.CategoriaResponseDTO;
import com.marmitech.Marmitech.Entity.Categoria;

public class CategoriaResponseMapper {

    public static CategoriaResponseDTO toDto(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao()
        );
    }
}
