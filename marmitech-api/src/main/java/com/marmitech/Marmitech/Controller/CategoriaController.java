package com.marmitech.Marmitech.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.marmitech.Marmitech.DTO.RequestDTO.CategoriaRequestDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.CategoriaResponseDTO;
import com.marmitech.Marmitech.Services.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categoria")
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<CategoriaResponseDTO> save(@RequestBody @Valid CategoriaRequestDTO dto) {
        var result = categoriaService.save(dto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/findAll")
    public ResponseEntity<List<CategoriaResponseDTO>> findAll() {
        var result = categoriaService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/findById/{id}")
    public ResponseEntity<CategoriaResponseDTO> findById(@PathVariable Integer id) {
        var result = categoriaService.findById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        categoriaService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<CategoriaResponseDTO> update(@PathVariable Integer id, @RequestBody CategoriaRequestDTO dto) {
        var result = categoriaService.update(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}