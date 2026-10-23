package com.marmitech.Marmitech.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HistoricoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank(message = "data_evento não pode ser null ou vazio")
    private String dataEvento;

    @NotNull
    @NotBlank(message = "tipo_evento não pode ser null ou vazio")
    private String tipoEvento;

    @NotNull
    @NotBlank(message = "descricao não pode ser null ou vazio")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    @JsonBackReference("pedido-historico")
    //referência na relação Pedido <-> HistoricoCompra. objeto historicoCompra seja incluido tambem no Pedido e vice versa
    private Pedido pedido;
}
