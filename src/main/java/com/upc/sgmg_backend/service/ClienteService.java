package com.upc.sgmg_backend.service;

import com.upc.sgmg_backend.entity.Cliente;

import java.util.List;

public interface ClienteService {

    Cliente crearCliente(Cliente cliente);

    Cliente obtenerClientePorId(Integer id);

    Cliente obtenerClientePorDni(String dni);

    Cliente obtenerClienteConMembresias(Integer id);

    List<Cliente> listarTodos();

    List<Cliente> listarPorEstado(String estado);

    Cliente actualizarCliente(Integer id, Cliente clienteActualizar);

    void eliminarCliente(Integer id);
}