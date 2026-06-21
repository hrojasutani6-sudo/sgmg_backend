package com.upc.sgmg_backend.service;

import com.upc.sgmg_backend.entity.Membresia;

import java.util.List;

public interface MembresiaService {

    Membresia crearMembresia(Integer clienteId, Membresia membresia);

    Membresia obtenerMembresiaPorId(Integer id);

    Membresia obtenerMembresiaConCliente(Integer id);

    Membresia obtenerMembresiaActivaPorCliente(Integer clienteId);

    List<Membresia> listarTodas();

    List<Membresia> listarPorEstado(String estadoMembresia);

    List<Membresia> listarPorTipoPlan(String tipoPlan);

    List<Membresia> listarPorCliente(Integer clienteId);

    Membresia actualizarMembresia(Integer id, Membresia membresiaActualizar);

    void eliminarMembresia(Integer id);

    boolean verificarVigenciaCliente(Integer clienteId);

    List<Object[]> obtenerReporteIngresosPorPlan();
}