package com.upc.sgmg_backend.repository;

import com.upc.sgmg_backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByDni(String dni);

    List<Cliente> findByEstado(String estado);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.membresias WHERE c.id = :id")
    Optional<Cliente> findByIdWithMembresias(@Param("id") Integer id);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.membresias WHERE c.dni = :dni")
    Optional<Cliente> findByDniWithMembresias(@Param("dni") String dni);
}