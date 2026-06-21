package com.upc.sgmg_backend.repository;

import com.upc.sgmg_backend.entity.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Integer> {

    List<Membresia> findByEstadoMembresia(String estadoMembresia);

    List<Membresia> findByTipoPlan(String tipoPlan);

    List<Membresia> findByClienteId(Integer clienteId);

    Optional<Membresia> findByClienteIdAndEstadoMembresia(Integer clienteId, String estadoMembresia);

    @Query("SELECT m FROM Membresia m JOIN FETCH m.cliente WHERE m.id = :id")
    Optional<Membresia> findByIdWithCliente(@Param("id") Integer id);

    @Query("SELECT m.tipoPlan, COUNT(m), COALESCE(SUM(m.precioMensual), 0) FROM Membresia m WHERE m.estadoMembresia = :estado GROUP BY m.tipoPlan")
    List<Object[]> obtenerReporteIngresosPorPlan(@Param("estado") String estado);
}