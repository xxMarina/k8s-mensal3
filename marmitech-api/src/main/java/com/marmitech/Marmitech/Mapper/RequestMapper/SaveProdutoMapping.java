package com.marmitech.Marmitech.Mapper.RequestMapper;

import com.marmitech.Marmitech.DTO.RequestDTO.ProdutoSaveDTO;
import com.marmitech.Marmitech.Entity.Categoria;
import com.marmitech.Marmitech.Entity.Produto;

public class SaveProdutoMapping {
    public static ProdutoSaveDTO toDto(Produto produto){
        return new ProdutoSaveDTO(
            produto.getNome(),
            produto.getDescricao(),
            produto.getCategoria() != null ? produto.getCategoria().getId() : null,
            produto.getEstoque(),
            produto.getPrecoUnitario(),
            produto.getSku()
        );
    }

    public static Produto toEntity(ProdutoSaveDTO produtoDto, Categoria categoria){
        Produto produto = new Produto();
        produto.setNome(produtoDto.nome());
        produto.setDescricao(produtoDto.descricao());
        produto.setCategoria(categoria);
        produto.setEstoque(produtoDto.estoque());
        produto.setPrecoUnitario(produtoDto.precoUnitario());
        produto.setSku(produtoDto.sku());

        return produto;
    }
}
