package com.marmitech.Marmitech.Mapper.RequestMapper;

import com.marmitech.Marmitech.DTO.RequestDTO.CategoriaRequestDTO;
import com.marmitech.Marmitech.Entity.Categoria;

public class CategoriaRequestMapper {

    public static Categoria toEntity(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());
        categoria.setDescricao(dto.descricao());
        return categoria;
    }

    public static CategoriaRequestDTO toDto(Categoria categoria) {
        return new CategoriaRequestDTO(
                categoria.getNome(),
                categoria.getDescricao()
        );
    }
}
