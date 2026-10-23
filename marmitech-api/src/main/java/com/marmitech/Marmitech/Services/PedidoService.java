package com.marmitech.Marmitech.Services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marmitech.Marmitech.DTO.RequestDTO.PedidoRequestDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.PedidoResponseDTO;
import com.marmitech.Marmitech.Entity.Pedido;
import com.marmitech.Marmitech.Entity.PedidoItem;
import com.marmitech.Marmitech.Entity.Produto;
import com.marmitech.Marmitech.Mapper.RequestMapper.PedidoRequestMapper;
import com.marmitech.Marmitech.Mapper.ResponseMapper.PedidoResponseMapper;
import com.marmitech.Marmitech.Repository.ClienteRepository;
import com.marmitech.Marmitech.Repository.PedidoRepository;
import com.marmitech.Marmitech.Repository.ProdutoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public PedidoResponseDTO save(PedidoRequestDTO dto) {
        Pedido pedido = PedidoRequestMapper.toEntity(dto);
        pedido.setDataPedido(LocalDate.now().toString());

        for (PedidoItem item : pedido.getPedidoItems()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + item.getProduto().getId()));

            item.setProduto(produto);
            item.setPedido(pedido);
        }

        if (pedido.getCliente() != null && pedido.getCliente().getId() > 0) {
            var cliente = clienteRepository.findById(pedido.getCliente().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            pedido.setCliente(cliente);
        }

        Pedido saved = pedidoRepository.save(pedido);
        return PedidoResponseMapper.toDto(saved);
    }

    public List<PedidoResponseDTO> findAll() {
        return pedidoRepository
                .findAll()
                .stream()
                .map(PedidoResponseMapper::toDto)
                .toList();
    }

    public Pedido findById(Integer id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID DO PEDIDO INVALIDO");
        }
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido com ID " + id + " não encontrado"));
    }

    public List<Pedido> findByStatus(String status) {
        return pedidoRepository.findByStatus(status);
    }

    public List<Pedido> findByProduto(int produtoId) {
        Produto produto = new Produto();
        produto.setId(produtoId);

        return pedidoRepository.findByPedidoItemsProduto(produto);
    }

    public List<Pedido> findByProdutoNome(String nomeProduto) {
        return pedidoRepository.findByPedidoItemsProdutoNome(nomeProduto);
    }

    @Transactional
    public Pedido update(Integer id, Pedido pedido) {
        Pedido pedidoUpdate = findById(id);

        if (pedido.getValorTotal() != null) {
            pedidoUpdate.setValorTotal(pedido.getValorTotal());
        }
        if (pedido.getStatus() != null && !pedido.getStatus().isBlank()) {
            pedidoUpdate.setStatus(pedido.getStatus());
        }
        if (pedido.getEnderecoEntrega() != null && !pedido.getEnderecoEntrega().isBlank()) {
            pedidoUpdate.setEnderecoEntrega(pedido.getEnderecoEntrega());
        }
        if (pedido.getUsuario() != null) {
            pedidoUpdate.setUsuario(pedido.getUsuario());
        }
        if (pedido.getCliente() != null) {
            pedidoUpdate.setCliente(pedido.getCliente());
        }

        return pedidoRepository.save(pedidoUpdate);
    }

    public List<Pedido> findByClienteEmail(String email) {
        return pedidoRepository.findByClienteEmail(email);
    }

    @Transactional
    public void delete(Integer id) {
        var delete = findById(id);
        pedidoRepository.delete(delete);
    }
}