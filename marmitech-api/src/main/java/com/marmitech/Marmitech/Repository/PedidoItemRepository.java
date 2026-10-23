package com.marmitech.Marmitech.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marmitech.Marmitech.Entity.PedidoItem;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Integer> {

    boolean existsByProdutoId(Integer produtoId);

}
