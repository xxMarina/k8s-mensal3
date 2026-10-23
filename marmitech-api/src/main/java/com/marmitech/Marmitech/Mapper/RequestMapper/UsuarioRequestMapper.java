package com.marmitech.Marmitech.Mapper.RequestMapper;

import com.marmitech.Marmitech.DTO.RequestDTO.UsuarioRequestDTO;
import com.marmitech.Marmitech.Entity.Usuario;

public class UsuarioRequestMapper {

    public static Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setCargo(dto.cargo());
        return usuario;
    }

    public static UsuarioRequestDTO toDto(Usuario usuario) {
        return new UsuarioRequestDTO(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getCargo()
        );
    }
}
