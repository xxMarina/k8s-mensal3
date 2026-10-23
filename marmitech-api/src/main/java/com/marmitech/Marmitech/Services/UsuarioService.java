package com.marmitech.Marmitech.Services;

import com.marmitech.Marmitech.DTO.RequestDTO.UsuarioRequestDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.UsuarioResponseDTO;
import com.marmitech.Marmitech.Entity.Usuario;
import com.marmitech.Marmitech.Mapper.RequestMapper.UsuarioRequestMapper;
import com.marmitech.Marmitech.Mapper.ResponseMapper.UsuarioResponseMapper;
import com.marmitech.Marmitech.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO save(UsuarioRequestDTO dto) {
        Usuario usuario = UsuarioRequestMapper.toEntity( dto );
        usuario.setDataCriacao(LocalDate.now());
        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        Usuario saved = usuarioRepository.save(usuario);
        return UsuarioResponseMapper.toDto( saved );
    }

    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map( UsuarioResponseMapper::toDto )
                .toList();
    }

    public UsuarioResponseDTO findById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(RuntimeException::new);
        return UsuarioResponseMapper.toDto( usuario );
    }

    public void delete(Integer id) {
        var delete = usuarioRepository.findById(id).orElseThrow(RuntimeException::new);
        usuarioRepository.delete(delete);
    }

    public UsuarioResponseDTO update(Integer id, UsuarioRequestDTO dto) {
        Usuario usuarioUpdate = usuarioRepository.findById(id).orElseThrow(RuntimeException::new);
        usuarioUpdate.setDataCriacao(LocalDate.now());
        if (dto.nome() != null && !dto.nome().isBlank()) {
            usuarioUpdate.setNome(dto.nome());
        }
        if (dto.email() != null && !dto.email().isBlank()) {
            usuarioUpdate.setEmail(dto.email());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuarioUpdate.setSenha(passwordEncoder.encode(dto.senha()));
        }
        if (dto.cargo() != null && !dto.cargo().isBlank()) {
            usuarioUpdate.setCargo(dto.cargo());
        }
        Usuario saved = usuarioRepository.save(usuarioUpdate);
        return UsuarioResponseMapper.toDto( saved );
    }

    public List<UsuarioResponseDTO> findByCargo(String cargo) {
        return usuarioRepository.getByCargo(cargo)
                .stream()
                .map( UsuarioResponseMapper::toDto )
                .toList();
    }

    public List<UsuarioResponseDTO> findByNome(String nome) {
        return usuarioRepository.findByNome(nome)
                .stream()
                .map( UsuarioResponseMapper::toDto )
                .toList();
    }

    public Usuario login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        return usuario;
    }
}
