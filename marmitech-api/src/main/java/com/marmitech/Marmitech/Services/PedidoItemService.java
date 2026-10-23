package com.marmitech.Marmitech.Services;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marmitech.Marmitech.DTO.RequestDTO.PedidoItemRequestDTO;
import com.marmitech.Marmitech.DTO.ResponseDTO.PedidoItemResponseDTO;
import com.marmitech.Marmitech.Entity.Pedido;
import com.marmitech.Marmitech.Entity.PedidoItem;
import com.marmitech.Marmitech.Entity.Produto;
import com.marmitech.Marmitech.Mapper.RequestMapper.SaveProdutoItemMapping;
import com.marmitech.Marmitech.Mapper.ResponseMapper.ProdutoItemResponseMapper;
import com.marmitech.Marmitech.Repository.PedidoItemRepository;
import com.marmitech.Marmitech.Repository.PedidoRepository;
import com.marmitech.Marmitech.Repository.ProdutoRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoItemService {

    private Boolean isNumeroValido(int novoNum) {
        return novoNum > 0;
    }

    private Boolean isPrecoValido(Double novoNum) {
        return novoNum > 0;
    }

    private <T> T validator(T novo, T antigo, Predicate<T> validar) {
        return validar.test(novo) ? novo : antigo;
    }

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public PedidoItem save(PedidoItemResponseDTO novoPedidoItem) {
        PedidoItemRequestDTO requestDto = SaveProdutoItemMapping.toDto(novoPedidoItem);

        Pedido pedido = pedidoRepository.findById(requestDto.pedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + requestDto.pedidoId()));
        Produto produto = produtoRepository.findById(requestDto.produtoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + requestDto.produtoId()));

        PedidoItem pedidoItem = new PedidoItem();
        pedidoItem.setPedido(pedido);
        pedidoItem.setProduto(produto);
        pedidoItem.setQuantidade(requestDto.quantidade());
        pedidoItem.setPrecoUnitarioPedido(requestDto.precoUnitarioPedido());

        Double preco = requestDto.precoUnitarioPedido();
        if (preco == null) {
            throw new IllegalArgumentException("O preço unitário não pode ser nulo.");
        }
        pedidoItem.setPrecoUnitarioPedido(preco);

        Double subtotal = preco * requestDto.quantidade();
        pedidoItem.setSubtotal(subtotal);

        return pedidoItemRepository.save(pedidoItem);
    }

    @Transactional
    public PedidoItem update(PedidoItemResponseDTO atualizadoPedidoItem, int pedidoItemId) {

        PedidoItem antigPedidoItem = pedidoItemRepository.findById(pedidoItemId)
                .orElseThrow(() -> new RuntimeException("PedidoItem not found with id: " + pedidoItemId));

        if (atualizadoPedidoItem.pedidoId() != antigPedidoItem.getPedido().getId()) {
            Pedido novoPedido = pedidoRepository.findById(atualizadoPedidoItem.pedidoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Novo Pedido não encontrado com ID: " + atualizadoPedidoItem.pedidoId()));
            antigPedidoItem.setPedido(novoPedido);
        }

        if (atualizadoPedidoItem.produtoId() != antigPedidoItem.getProduto().getId()) {
            Produto novoProduto = produtoRepository.findById(atualizadoPedidoItem.produtoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Novo Produto não encontrado com ID: " + atualizadoPedidoItem.produtoId()));
            antigPedidoItem.setProduto(novoProduto);
        }

        antigPedidoItem.setPrecoUnitarioPedido(validator(atualizadoPedidoItem.precoUnitarioPedido(),
                antigPedidoItem.getPrecoUnitarioPedido(), this::isPrecoValido));
        antigPedidoItem.setQuantidade(
                validator(atualizadoPedidoItem.quantidade(), antigPedidoItem.getQuantidade(), this::isNumeroValido));

        Double novoSubtotal = antigPedidoItem.getPrecoUnitarioPedido() * antigPedidoItem.getQuantidade();
        antigPedidoItem.setSubtotal(novoSubtotal);

        return pedidoItemRepository.save(antigPedidoItem);
    }

    @Transactional
    public String delete(int pedidoItemId) {
        pedidoItemRepository.deleteById(pedidoItemId);
        return "Linha deleteda da tabela.";
    }

    public PedidoItem findById(int pedidoId) {
        return pedidoItemRepository.findById(pedidoId).get();
    }

    public List<PedidoItemResponseDTO> findAll() {
        return pedidoItemRepository.findAll().stream().map(ProdutoItemResponseMapper::toDto).toList();
    }
}
