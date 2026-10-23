package com.marmitech.Marmitech.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marmitech.Marmitech.Entity.HistoricoCompra;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HistoricoCompraRepository extends JpaRepository<HistoricoCompra, Integer>{

    List<HistoricoCompra> findByDataEvento(String dataEvento);

}
