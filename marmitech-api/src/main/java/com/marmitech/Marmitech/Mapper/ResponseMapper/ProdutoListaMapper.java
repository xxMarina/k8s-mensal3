package com.marmitech.Marmitech.Mapper.ResponseMapper;

import com.marmitech.Marmitech.DTO.ResponseDTO.ProdutoListaDTO;
import com.marmitech.Marmitech.Entity.Produto;

public class ProdutoListaMapper {
    public static ProdutoListaDTO toDto(Produto produto) {
        Integer catId = produto.getCategoria() != null ? produto.getCategoria().getId() : null;
        String catNome = produto.getCategoria() != null ? produto.getCategoria().getNome() : null;
        return new ProdutoListaDTO(
            produto.getId(),
            produto.getNome(),
            produto.getDescricao(),
            catId,
            catNome,
            produto.getDataCadastro(),
            produto.getPrecoUnitario(),
            produto.getEstoque(),
            produto.getSku()
        );
    }
}