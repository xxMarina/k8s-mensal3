package com.marmitech.Marmitech.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.marmitech.Marmitech.DTO.RequestDTO.PedidoRequestDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.PedidoResponseDTO;
import com.marmitech.Marmitech.Entity.Pedido;
import com.marmitech.Marmitech.Mapper.ResponseMapper.PedidoResponseMapper;
import com.marmitech.Marmitech.Services.PedidoService;

import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/pedido")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<PedidoResponseDTO> save(@RequestBody PedidoRequestDTO dto) {
        return new ResponseEntity<>(pedidoService.save(dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> findAll() {
        var result = pedidoService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/findById/{id}")
    public ResponseEntity<PedidoResponseDTO> findById(@PathVariable Integer id) {
        var result = pedidoService.findById(id);
        PedidoResponseDTO pedidoDto = PedidoResponseMapper.toDto(result);
        return new ResponseEntity<>(pedidoDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/findByStatus")
    public ResponseEntity<List<Pedido>> findByStatus(@RequestParam String status) {
        List<Pedido> result = pedidoService.findByStatus(status);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/findByProdutoNome")
    public ResponseEntity<List<Pedido>> findByProdutoNome(@RequestParam String nomeProduto) {
        List<Pedido> result = pedidoService.findByProdutoNome(nomeProduto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/findByProduto")
    public ResponseEntity<List<Pedido>> findByProduto(@RequestParam int produtoId) {
        List<Pedido> result = pedidoService.findByProduto(produtoId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<PedidoResponseDTO> update(@PathVariable Integer id, @RequestBody Pedido pedido) {
        Pedido updatedPedido = pedidoService.update(id, pedido);
        PedidoResponseDTO pedidoDto = PedidoResponseMapper.toDto(updatedPedido);
        return new ResponseEntity<>(pedidoDto, HttpStatus.OK);
    }

    @GetMapping("/meus")
    public ResponseEntity<List<PedidoResponseDTO>> meusPedidos() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PedidoResponseDTO> pedidos = pedidoService.findByClienteEmail(email)
                .stream()
                .map(PedidoResponseMapper::toDto)
                .toList();
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        pedidoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}