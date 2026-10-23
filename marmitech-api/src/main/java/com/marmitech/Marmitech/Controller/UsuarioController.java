package com.marmitech.Marmitech.Controller;

import com.marmitech.Marmitech.DTO.RequestDTO.UsuarioRequestDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.UsuarioResponseDTO;
import com.marmitech.Marmitech.Entity.Usuario;
import com.marmitech.Marmitech.Services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.marmitech.Marmitech.DTO.ResponseDTO.LoginResponseDTO;
import com.marmitech.Marmitech.Security.JwUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    private JwUtil jwUtil;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<UsuarioResponseDTO> save(@RequestBody @Valid UsuarioRequestDTO dto) {
        var result = usuarioService.save(dto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findAll")
    public ResponseEntity<List<UsuarioResponseDTO>> findAll() {
        var result = usuarioService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("findById/{id}")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Integer id) {
        var result = usuarioService.findById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<UsuarioResponseDTO> update(@PathVariable Integer id, @RequestBody UsuarioRequestDTO dto) {
        var result = usuarioService.update(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findByCargo/{cargo}")
    public ResponseEntity<List<UsuarioResponseDTO>> findByCargo(@PathVariable String cargo) {
        var result = usuarioService.findByCargo(cargo);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findByNome/{nome}")
    public ResponseEntity<List<UsuarioResponseDTO>> findByNome(@PathVariable String nome) {
        var result = usuarioService.findByNome(nome);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginData) {
        try {
            var usuario = usuarioService.login(loginData.getEmail(), loginData.getSenha());
            String token = jwUtil.generateToken(usuario.getEmail(), usuario.getCargo());
            return ResponseEntity.ok(new LoginResponseDTO(
                    token,
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getCargo()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}