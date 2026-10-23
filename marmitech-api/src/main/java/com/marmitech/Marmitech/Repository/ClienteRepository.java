package com.marmitech.Marmitech.Repository;

import com.marmitech.Marmitech.Entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    List<Cliente> findByNome(String nome);

    Optional<Cliente> findByCpfCnpj(String cpfCnpj);

    @Query("SELECT c FROM Cliente c WHERE c.nome = :nome")
    List<Cliente> getByNome(@Param("nome") String nome);

    @Query("SELECT c FROM Cliente c WHERE c.cpfCnpj = :cpfCnpj")
    Cliente getByCpfCnpj(@Param("cpfCnpj") String cpfCnpj);
}
