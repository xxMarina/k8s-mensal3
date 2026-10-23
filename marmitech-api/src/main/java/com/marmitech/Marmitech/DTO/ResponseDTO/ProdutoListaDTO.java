package com.marmitech.Marmitech.DTO.ResponseDTO;

public record ProdutoListaDTO(int id, String nome, String descricao, Integer categoriaId, String categoriaNome, String dataCadastro, Double precoUnitario, int estoque, String sku) {

}