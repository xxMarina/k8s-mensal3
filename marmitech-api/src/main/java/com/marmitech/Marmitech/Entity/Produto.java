package com.marmitech.Marmitech.Entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank(message = "sku não pode ser null ou vazio")
    private String sku;

    @NotNull
    @NotBlank(message = "nome não pode ser null ou vazio")
    private String nome;

    @NotNull
    @NotBlank(message = "descricao não pode ser null ou vazio")
    private String descricao;

    @NotNull(message = "precoUnitario não pode ser null ou vazio")
    @Min(1)
    private Double precoUnitario;

    @NotNull(message = "estoque não pode ser null ou vazio")
    @Min(1)
    private int estoque;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    private String dataCadastro;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "produto")
    @JsonManagedReference("produto-item") /*
                                           * referência na relação Produto <-> PedidoItem.
                                           * permitindo que a lista de PedidoItem seja incluída a partir do Produto.
                                           */
    private Set<PedidoItem> pedidoItems;

}
