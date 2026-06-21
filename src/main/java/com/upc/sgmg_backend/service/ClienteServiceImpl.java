package com.upc.sgmg_backend.service;

import com.upc.sgmg_backend.entity.Cliente;
import com.upc.sgmg_backend.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente obtenerClientePorId(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    @Override
    public Cliente obtenerClientePorDni(String dni) {
        return clienteRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con DNI: " + dni));
    }

    @Override
    public Cliente obtenerClienteConMembresias(Integer id) {
        return clienteRepository.findByIdWithMembresias(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public List<Cliente> listarPorEstado(String estado) {
        return clienteRepository.findByEstado(estado);
    }

    @Override
    public Cliente actualizarCliente(Integer id, Cliente clienteActualizar) {
        Cliente cliente = obtenerClientePorId(id);
        cliente.setDni(clienteActualizar.getDni());
        cliente.setNombres(clienteActualizar.getNombres());
        cliente.setApellidos(clienteActualizar.getApellidos());
        cliente.setFechaNacimiento(clienteActualizar.getFechaNacimiento());
        cliente.setTelefono(clienteActualizar.getTelefono());
        cliente.setEmail(clienteActualizar.getEmail());
        cliente.setFechaRegistro(clienteActualizar.getFechaRegistro());
        cliente.setEstado(clienteActualizar.getEstado());
        return clienteRepository.save(cliente);
    }

    @Override
    public void eliminarCliente(Integer id) {
        Cliente cliente = obtenerClientePorId(id);
        clienteRepository.delete(cliente);
    }
}