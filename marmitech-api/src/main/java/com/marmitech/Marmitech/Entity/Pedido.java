package com.marmitech.Marmitech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String dataPedido;

    @NotNull(message = "O valor total não pode ser nulo")
    @DecimalMin(value = "0.01", message = "O valor total deve ser maior que zero")
    private Double valorTotal;

    @NotNull
    @NotBlank(message = "status não pode ser null ou vazio")
    private String status;

    @NotNull
    @NotBlank(message = "enderecoEntrega não pode ser null ou vazio")
    private String enderecoEntrega;

    // Pedido que sera atrelado ao usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties("pedidos")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente; // quem comprou

    // Pedido que sera atrelado ao historico
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("pedido-historico") /*
                                               * add referencia para que o json n entre em loop e
                                               * permitindo que a lista de históricos seja incluída no objeto Pedido.
                                               */
    private List<HistoricoCompra> historicos;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("pedido-item")
    private Set<PedidoItem> pedidoItems;

    public void addItem(PedidoItem item) {
        if (this.pedidoItems == null) {
            this.pedidoItems = new HashSet<>();
        }
        item.setPedido(this);
        this.pedidoItems.add(item);
    }

    public void addHistorico(HistoricoCompra historico) {
        if (historicos == null) {
            historicos = new ArrayList<>();
        }
        historico.setPedido(this); // garante o vínculo
        historicos.add(historico);
    }

}
