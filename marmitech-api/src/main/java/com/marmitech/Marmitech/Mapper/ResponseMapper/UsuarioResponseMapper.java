package com.marmitech.Marmitech.Mapper.ResponseMapper;

import com.marmitech.Marmitech.DTO.ResponseDTO.UsuarioResponseDTO;
import com.marmitech.Marmitech.Entity.Usuario;

public class UsuarioResponseMapper {

    public static UsuarioResponseDTO toDto(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCargo(),
                usuario.getDataCriacao()
        );
    }
}
