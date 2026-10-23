package com.marmitech.Marmitech.Controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.marmitech.Marmitech.DTO.ResponseDTO.PedidoItemResponseDTO;
import com.marmitech.Marmitech.Entity.PedidoItem;
import com.marmitech.Marmitech.Services.PedidoItemService;

@RestController
@RequestMapping("/pedidoItem")
public class PedidoItemController {
    @Autowired
    private PedidoItemService pedidoItemService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<PedidoItem> save(@RequestBody @Valid PedidoItemResponseDTO pedidoItem) {
        return new ResponseEntity<>(pedidoItemService.save(pedidoItem), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{pedidoItemId}")
    public ResponseEntity<PedidoItem> update(@RequestBody PedidoItemResponseDTO pedidoItem,
            @PathVariable int pedidoItemId) {
        return new ResponseEntity<>(pedidoItemService.update(pedidoItem, pedidoItemId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{pedidoItemId}")
    public ResponseEntity<Void> delete(@PathVariable int pedidoItemId) {
        pedidoItemService.delete(pedidoItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/findById/{pedidoItemId}")
    public ResponseEntity<PedidoItem> findById(@PathVariable int pedidoItemId) {
        return new ResponseEntity<>(pedidoItemService.findById(pedidoItemId), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping("/findAll")
    public ResponseEntity<List<PedidoItemResponseDTO>> findAll() {
        return new ResponseEntity<>(pedidoItemService.findAll(), HttpStatus.OK);
    }
}