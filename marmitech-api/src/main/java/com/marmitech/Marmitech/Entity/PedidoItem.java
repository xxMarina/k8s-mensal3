package com.marmitech.Marmitech.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int quantidade;

    private Double precoUnitarioPedido;

    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    @JsonBackReference("pedido-item") //add referencia para que o json n entre em loop, objeto PedidoItem seja incluido tambem no Pedido e vice versa
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    @JsonBackReference("produto-item")//referência na relação Produto <-> PedidoItem
    private Produto produto;
}
