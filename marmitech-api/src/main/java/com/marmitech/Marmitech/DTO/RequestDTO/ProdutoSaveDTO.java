package com.marmitech.Marmitech.DTO.RequestDTO;

public record ProdutoSaveDTO(String nome, String descricao, Integer categoriaId, int estoque, Double precoUnitario, String sku) {
}