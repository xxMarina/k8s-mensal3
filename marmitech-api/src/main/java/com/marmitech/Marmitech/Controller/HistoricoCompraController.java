package com.marmitech.Marmitech.Controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.marmitech.Marmitech.DTO.RequestDTO.HistoricoSaveDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.HistoricoResponseDTO;
import com.marmitech.Marmitech.Entity.HistoricoCompra;
import com.marmitech.Marmitech.Services.HistoricoCompraService;

@RestController
@RequestMapping("/historicoCompra")
public class HistoricoCompraController {

    @Autowired
    private HistoricoCompraService historicoCompraService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<HistoricoCompra> save(@RequestBody @Valid HistoricoSaveDTO historicoCompra) {
        return new ResponseEntity<>(historicoCompraService.save(historicoCompra), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{historicoCompraId}")
    public ResponseEntity<HistoricoCompra> update(@RequestBody HistoricoCompra historicoCompra, @PathVariable int historicoCompraId) {
        return new ResponseEntity<>(historicoCompraService.update(historicoCompra, historicoCompraId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{historicoCompraId}")
    public ResponseEntity<Void> delete(@PathVariable int historicoCompraId) {
        historicoCompraService.delete(historicoCompraId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/findById/{historicoCompraId}")
    public ResponseEntity<HistoricoResponseDTO> findById(@PathVariable int historicoCompraId) {
        return new ResponseEntity<>(historicoCompraService.findById(historicoCompraId), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping
    public ResponseEntity<List<HistoricoResponseDTO>> findAll() {
        return new ResponseEntity<>(historicoCompraService.findAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/findByDataEvento")
    public ResponseEntity<List<HistoricoResponseDTO>> findByDataEvento(@RequestParam String dataEvento) {
        return new ResponseEntity<>(historicoCompraService.findByDataEvento(dataEvento), HttpStatus.OK);
    }
}