package com.upc.sgmg_backend.service;

import com.upc.sgmg_backend.entity.Cliente;
import com.upc.sgmg_backend.entity.Membresia;
import com.upc.sgmg_backend.repository.ClienteRepository;
import com.upc.sgmg_backend.repository.MembresiaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MembresiaServiceImpl implements MembresiaService {

    private static final String ESTADO_ACTIVA = "Activa";
    private static final String ESTADO_VENCIDA = "Vencida";
    private static final String ESTADO_BLOQUEADO = "Bloqueado";
    private static final String ESTADO_INACTIVO = "Inactivo";
    private static final String ESTADO_ACTIVO_CLIENTE = "Activo";

    private final MembresiaRepository membresiaRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public MembresiaServiceImpl(MembresiaRepository membresiaRepository, ClienteRepository clienteRepository) {
        this.membresiaRepository = membresiaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Membresia crearMembresia(Integer clienteId, Membresia membresia) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));

        boolean tieneHistorialMembresias = !membresiaRepository.findByClienteId(clienteId).isEmpty();

        if (ESTADO_BLOQUEADO.equalsIgnoreCase(cliente.getEstado())) {
            throw new RuntimeException("No se puede crear la membresia porque el cliente esta bloqueado.");
        }

        verificarVigenciaCliente(clienteId);

        membresiaRepository.findByClienteIdAndEstadoMembresia(clienteId, ESTADO_ACTIVA)
                .ifPresent(membresiaActiva -> {
                    throw new RuntimeException("El cliente ya tiene una membresia activa con ID: " + membresiaActiva.getId());
                });

        if (ESTADO_INACTIVO.equalsIgnoreCase(cliente.getEstado()) && !tieneHistorialMembresias) {
            cliente.setEstado(ESTADO_ACTIVO_CLIENTE);
            clienteRepository.save(cliente);
        }

        if (membresia.getFechaInicio() == null) {
            throw new RuntimeException("La fecha de inicio es obligatoria para crear la membresia.");
        }

        Integer diasCongelados = membresia.getDiasCongelados() == null ? 0 : membresia.getDiasCongelados();
        membresia.setDiasCongelados(diasCongelados);
        membresia.setCliente(cliente);
        membresia.setPrecioMensual(obtenerPrecioSegunPlan(membresia.getTipoPlan()));
        membresia.setFechaVencimiento(membresia.getFechaInicio().plusDays(30L + diasCongelados));
        membresia.setEstadoMembresia(ESTADO_ACTIVA);

        return membresiaRepository.save(membresia);
    }

    @Override
    public Membresia obtenerMembresiaPorId(Integer id) {
        return membresiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membresia no encontrada con ID: " + id));
    }

    @Override
    public Membresia obtenerMembresiaConCliente(Integer id) {
        return membresiaRepository.findByIdWithCliente(id)
                .orElseThrow(() -> new RuntimeException("Membresia no encontrada con ID: " + id));
    }

    @Override
    public Membresia obtenerMembresiaActivaPorCliente(Integer clienteId) {
        return membresiaRepository.findByClienteIdAndEstadoMembresia(clienteId, ESTADO_ACTIVA)
                .orElseThrow(() -> new RuntimeException("El cliente no tiene una membresia activa."));
    }

    @Override
    public List<Membresia> listarTodas() {
        return membresiaRepository.findAll();
    }

    @Override
    public List<Membresia> listarPorEstado(String estadoMembresia) {
        return membresiaRepository.findByEstadoMembresia(estadoMembresia);
    }

    @Override
    public List<Membresia> listarPorTipoPlan(String tipoPlan) {
        return membresiaRepository.findByTipoPlan(tipoPlan);
    }

    @Override
    public List<Membresia> listarPorCliente(Integer clienteId) {
        return membresiaRepository.findByClienteId(clienteId);
    }

    @Override
    public Membresia actualizarMembresia(Integer id, Membresia membresiaActualizar) {
        Membresia membresia = obtenerMembresiaPorId(id);
        membresia.setTipoPlan(membresiaActualizar.getTipoPlan());
        membresia.setFechaInicio(membresiaActualizar.getFechaInicio());
        membresia.setMetodoPago(membresiaActualizar.getMetodoPago());
        membresia.setDiasCongelados(membresiaActualizar.getDiasCongelados() == null ? 0 : membresiaActualizar.getDiasCongelados());
        membresia.setPrecioMensual(obtenerPrecioSegunPlan(membresia.getTipoPlan()));
        membresia.setFechaVencimiento(membresia.getFechaInicio().plusDays(30L + membresia.getDiasCongelados()));

        if (membresiaActualizar.getEstadoMembresia() != null && !membresiaActualizar.getEstadoMembresia().isBlank()) {
            membresia.setEstadoMembresia(membresiaActualizar.getEstadoMembresia());
        }

        return membresiaRepository.save(membresia);
    }

    @Override
    public void eliminarMembresia(Integer id) {
        Membresia membresia = obtenerMembresiaPorId(id);
        membresiaRepository.delete(membresia);
    }

    @Override
    public boolean verificarVigenciaCliente(Integer clienteId) {
        Membresia membresiaActiva = membresiaRepository.findByClienteIdAndEstadoMembresia(clienteId, ESTADO_ACTIVA)
                .orElse(null);

        if (membresiaActiva == null) {
            return false;
        }

        if (membresiaActiva.getFechaVencimiento().isBefore(LocalDate.now())) {
            membresiaActiva.setEstadoMembresia(ESTADO_VENCIDA);
            membresiaRepository.save(membresiaActiva);
            return false;
        }

        return true;
    }

    @Override
    public List<Object[]> obtenerReporteIngresosPorPlan() {
        return membresiaRepository.obtenerReporteIngresosPorPlan(ESTADO_ACTIVA);
    }

    private BigDecimal obtenerPrecioSegunPlan(String tipoPlan) {
        if (tipoPlan == null) {
            throw new RuntimeException("El tipo de plan es obligatorio.");
        }

        return switch (tipoPlan) {
            case "Basico" -> BigDecimal.valueOf(99.00);
            case "Premium" -> BigDecimal.valueOf(149.00);
            case "Elite" -> BigDecimal.valueOf(249.00);
            default -> throw new RuntimeException("Tipo de plan no valido: " + tipoPlan);
        };
    }
}