package com.upc.sgmg_backend.config;

import com.upc.sgmg_backend.entity.Cliente;
import com.upc.sgmg_backend.entity.Membresia;
import com.upc.sgmg_backend.service.ClienteService;
import com.upc.sgmg_backend.service.MembresiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleMenu implements CommandLineRunner {

    private final ClienteService clienteService;
    private final MembresiaService membresiaService;
    private final Scanner scanner;

    @Autowired
    public ConsoleMenu(ClienteService clienteService, MembresiaService membresiaService) {
        this.clienteService = clienteService;
        this.membresiaService = membresiaService;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run(String... args) {
        mostrarBienvenida();
        boolean salir = false;

        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opcion: ");

            switch (opcion) {
                case 1 -> menuClientes();
                case 2 -> menuMembresias();
                case 3 -> menuVigencia();
                case 4 -> menuReportes();
                case 0 -> {
                    salir = true;
                    System.out.println("\nHasta luego.");
                }
                default -> System.out.println("\nOpcion no valida.");
            }
        }

        scanner.close();
    }

    private void mostrarBienvenida() {
        System.out.println("============================================================");
        System.out.println("   SISTEMA DE GESTION DE MEMBRESIAS DE GIMNASIO BOUTIQUE");
        System.out.println("            Spring Boot + PostgreSQL + JPA");
        System.out.println("============================================================");
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n-------------------- MENU PRINCIPAL --------------------");
        System.out.println("[1] Gestion de clientes");
        System.out.println("[2] Gestion de membresias");
        System.out.println("[3] Vigencia y renovacion");
        System.out.println("[4] Reportes");
        System.out.println("[0] Salir");
        System.out.println("--------------------------------------------------------");
    }

    private void menuClientes() {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n----------------- GESTION DE CLIENTES ------------------");
            System.out.println("[1] Listar todos los clientes");
            System.out.println("[2] Buscar cliente por ID");
            System.out.println("[3] Buscar cliente por DNI");
            System.out.println("[4] Listar clientes por estado");
            System.out.println("[5] Crear nuevo cliente");
            System.out.println("[6] Actualizar cliente");
            System.out.println("[7] Eliminar cliente");
            System.out.println("[8] Ver cliente con membresias");
            System.out.println("[0] Volver al menu principal");
            System.out.println("--------------------------------------------------------");

            switch (leerEntero("Opcion: ")) {
                case 1 -> listarClientes();
                case 2 -> buscarClientePorId();
                case 3 -> buscarClientePorDni();
                case 4 -> listarClientesPorEstado();
                case 5 -> crearCliente();
                case 6 -> actualizarCliente();
                case 7 -> eliminarCliente();
                case 8 -> verClienteConMembresias();
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private void menuMembresias() {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n---------------- GESTION DE MEMBRESIAS -----------------");
            System.out.println("[1] Listar todas las membresias");
            System.out.println("[2] Buscar membresia por ID");
            System.out.println("[3] Listar membresias por estado");
            System.out.println("[4] Listar membresias por tipo de plan");
            System.out.println("[5] Listar membresias por cliente");
            System.out.println("[6] Crear nueva membresia");
            System.out.println("[7] Actualizar membresia");
            System.out.println("[8] Eliminar membresia");
            System.out.println("[9] Ver membresia con cliente");
            System.out.println("[0] Volver al menu principal");
            System.out.println("--------------------------------------------------------");

            switch (leerEntero("Opcion: ")) {
                case 1 -> listarMembresias();
                case 2 -> buscarMembresiaPorId();
                case 3 -> listarMembresiasPorEstado();
                case 4 -> listarMembresiasPorPlan();
                case 5 -> listarMembresiasPorCliente();
                case 6 -> crearMembresia();
                case 7 -> actualizarMembresia();
                case 8 -> eliminarMembresia();
                case 9 -> verMembresiaConCliente();
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private void menuVigencia() {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n---------------- VIGENCIA Y RENOVACION -----------------");
            System.out.println("[1] Verificar vigencia de cliente");
            System.out.println("[2] Ver membresia activa de cliente");
            System.out.println("[0] Volver al menu principal");
            System.out.println("--------------------------------------------------------");

            switch (leerEntero("Opcion: ")) {
                case 1 -> verificarVigenciaCliente();
                case 2 -> verMembresiaActivaCliente();
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private void menuReportes() {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n---------------- REPORTES Y CONSULTAS ------------------");
            System.out.println("[1] Proyeccion de ingresos por plan");
            System.out.println("[0] Volver al menu principal");
            System.out.println("--------------------------------------------------------");

            switch (leerEntero("Opcion: ")) {
                case 1 -> reporteIngresosPorPlan();
                case 0 -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private void listarClientes() {
        imprimirClientes(clienteService.listarTodos());
    }

    private void buscarClientePorId() {
        int id = leerEntero("Ingrese ID del cliente: ");
        try {
            imprimirCliente(clienteService.obtenerClientePorId(id));
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void buscarClientePorDni() {
        String dni = leerTexto("Ingrese DNI del cliente: ");
        try {
            imprimirCliente(clienteService.obtenerClientePorDni(dni));
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void listarClientesPorEstado() {
        String estado = leerTexto("Ingrese estado del cliente: ");
        imprimirClientes(clienteService.listarPorEstado(estado));
    }

    private void crearCliente() {
        System.out.println("\nCREAR NUEVO CLIENTE");
        String dni = leerTexto("DNI: ");
        String nombres = leerTexto("Nombres: ");
        String apellidos = leerTexto("Apellidos: ");
        LocalDate fechaNacimiento = leerFecha("Fecha de nacimiento (YYYY-MM-DD): ");
        String telefono = leerTexto("Telefono: ");
        String email = leerTexto("Email: ");
        LocalDate fechaRegistro = leerFecha("Fecha de registro (YYYY-MM-DD): ");
        String estado = leerTexto("Estado (Activo/Inactivo/Bloqueado): ");

        Cliente cliente = new Cliente(dni, nombres, apellidos, fechaNacimiento, telefono, email, fechaRegistro, estado);

        try {
            Cliente creado = clienteService.crearCliente(cliente);
            System.out.println("Cliente creado con ID: " + creado.getId());
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void actualizarCliente() {
        int id = leerEntero("ID del cliente a actualizar: ");

        try {
            Cliente existente = clienteService.obtenerClientePorId(id);
            String dni = leerTextoOpcional("Nuevo DNI [" + existente.getDni() + "]: ");
            String nombres = leerTextoOpcional("Nuevos nombres [" + existente.getNombres() + "]: ");
            String apellidos = leerTextoOpcional("Nuevos apellidos [" + existente.getApellidos() + "]: ");
            String fechaNacimiento = leerTextoOpcional("Nueva fecha de nacimiento [" + existente.getFechaNacimiento() + "]: ");
            String telefono = leerTextoOpcional("Nuevo telefono [" + valorTexto(existente.getTelefono()) + "]: ");
            String email = leerTextoOpcional("Nuevo email [" + valorTexto(existente.getEmail()) + "]: ");
            String fechaRegistro = leerTextoOpcional("Nueva fecha de registro [" + existente.getFechaRegistro() + "]: ");
            String estado = leerTextoOpcional("Nuevo estado [" + existente.getEstado() + "]: ");

            Cliente actualizado = new Cliente();
            actualizado.setDni(dni.isBlank() ? existente.getDni() : dni);
            actualizado.setNombres(nombres.isBlank() ? existente.getNombres() : nombres);
            actualizado.setApellidos(apellidos.isBlank() ? existente.getApellidos() : apellidos);
            actualizado.setFechaNacimiento(fechaNacimiento.isBlank() ? existente.getFechaNacimiento() : parsearFecha(fechaNacimiento));
            actualizado.setTelefono(telefono.isBlank() ? existente.getTelefono() : telefono);
            actualizado.setEmail(email.isBlank() ? existente.getEmail() : email);
            actualizado.setFechaRegistro(fechaRegistro.isBlank() ? existente.getFechaRegistro() : parsearFecha(fechaRegistro));
            actualizado.setEstado(estado.isBlank() ? existente.getEstado() : estado);

            Cliente resultado = clienteService.actualizarCliente(id, actualizado);
            System.out.println("Cliente actualizado: " + resultado.getNombres() + " " + resultado.getApellidos());
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void eliminarCliente() {
        int id = leerEntero("ID del cliente a eliminar: ");
        String confirmar = leerTexto("Confirma eliminacion? (s/n): ");

        if (confirmar.equalsIgnoreCase("s")) {
            try {
                clienteService.eliminarCliente(id);
                System.out.println("Cliente eliminado.");
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
    }

    private void verClienteConMembresias() {
        int id = leerEntero("Ingrese ID del cliente: ");

        try {
            Cliente cliente = clienteService.obtenerClienteConMembresias(id);
            imprimirCliente(cliente);
            if (cliente.getMembresias().isEmpty()) {
                System.out.println("No hay membresias registradas para este cliente.");
            } else {
                System.out.println("\nMembresias del cliente:");
                imprimirMembresias(cliente.getMembresias());
            }
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void listarMembresias() {
        imprimirMembresias(membresiaService.listarTodas());
    }

    private void buscarMembresiaPorId() {
        int id = leerEntero("Ingrese ID de la membresia: ");
        try {
            imprimirMembresia(membresiaService.obtenerMembresiaPorId(id));
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void listarMembresiasPorEstado() {
        String estado = leerTexto("Ingrese estado de la membresia: ");
        imprimirMembresias(membresiaService.listarPorEstado(estado));
    }

    private void listarMembresiasPorPlan() {
        String tipoPlan = leerTexto("Ingrese tipo de plan: ");
        imprimirMembresias(membresiaService.listarPorTipoPlan(tipoPlan));
    }

    private void listarMembresiasPorCliente() {
        int clienteId = leerEntero("Ingrese ID del cliente: ");
        imprimirMembresias(membresiaService.listarPorCliente(clienteId));
    }

    private void crearMembresia() {
        System.out.println("\nCREAR NUEVA MEMBRESIA");
        int clienteId = leerEntero("ID del cliente: ");
        String tipoPlan = leerTexto("Tipo de plan (Basico/Premium/Elite): ");
        LocalDate fechaInicio = leerFecha("Fecha de inicio (YYYY-MM-DD): ");
        String metodoPago = leerTexto("Metodo de pago (Efectivo/Tarjeta/Transferencia): ");
        int diasCongelados = leerEntero("Dias congelados: ");

        Membresia membresia = new Membresia();
        membresia.setTipoPlan(tipoPlan);
        membresia.setFechaInicio(fechaInicio);
        membresia.setMetodoPago(metodoPago);
        membresia.setDiasCongelados(diasCongelados);

        try {
            Membresia creada = membresiaService.crearMembresia(clienteId, membresia);
            System.out.println("Membresia creada con ID: " + creada.getId());
            System.out.println("Precio asignado: S/ " + creada.getPrecioMensual());
            System.out.println("Fecha de vencimiento: " + creada.getFechaVencimiento());
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void actualizarMembresia() {
        int id = leerEntero("ID de la membresia a actualizar: ");

        try {
            Membresia existente = membresiaService.obtenerMembresiaPorId(id);
            String tipoPlan = leerTextoOpcional("Nuevo tipo de plan [" + existente.getTipoPlan() + "]: ");
            String fechaInicio = leerTextoOpcional("Nueva fecha de inicio [" + existente.getFechaInicio() + "]: ");
            String metodoPago = leerTextoOpcional("Nuevo metodo de pago [" + existente.getMetodoPago() + "]: ");
            String diasCongelados = leerTextoOpcional("Nuevos dias congelados [" + existente.getDiasCongelados() + "]: ");
            String estado = leerTextoOpcional("Nuevo estado de membresia [" + existente.getEstadoMembresia() + "]: ");

            Membresia actualizada = new Membresia();
            actualizada.setTipoPlan(tipoPlan.isBlank() ? existente.getTipoPlan() : tipoPlan);
            actualizada.setFechaInicio(fechaInicio.isBlank() ? existente.getFechaInicio() : parsearFecha(fechaInicio));
            actualizada.setMetodoPago(metodoPago.isBlank() ? existente.getMetodoPago() : metodoPago);
            actualizada.setDiasCongelados(diasCongelados.isBlank() ? existente.getDiasCongelados() : Integer.parseInt(diasCongelados));
            actualizada.setEstadoMembresia(estado.isBlank() ? existente.getEstadoMembresia() : estado);

            Membresia resultado = membresiaService.actualizarMembresia(id, actualizada);
            System.out.println("Membresia actualizada. Nuevo vencimiento: " + resultado.getFechaVencimiento());
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void eliminarMembresia() {
        int id = leerEntero("ID de la membresia a eliminar: ");
        String confirmar = leerTexto("Confirma eliminacion? (s/n): ");

        if (confirmar.equalsIgnoreCase("s")) {
            try {
                membresiaService.eliminarMembresia(id);
                System.out.println("Membresia eliminada.");
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
    }

    private void verMembresiaConCliente() {
        int id = leerEntero("Ingrese ID de la membresia: ");

        try {
            Membresia membresia = membresiaService.obtenerMembresiaConCliente(id);
            imprimirMembresia(membresia);
            System.out.println("Cliente asociado: " + membresia.getCliente().getNombres() + " " + membresia.getCliente().getApellidos());
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void verificarVigenciaCliente() {
        int clienteId = leerEntero("Ingrese ID del cliente: ");
        try {
            boolean vigente = membresiaService.verificarVigenciaCliente(clienteId);
            if (vigente) {
                System.out.println("El cliente tiene una membresia activa y vigente.");
            } else {
                System.out.println("El cliente no tiene una membresia activa vigente.");
            }
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void verMembresiaActivaCliente() {
        int clienteId = leerEntero("Ingrese ID del cliente: ");
        try {
            imprimirMembresia(membresiaService.obtenerMembresiaActivaPorCliente(clienteId));
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void reporteIngresosPorPlan() {
        List<Object[]> resultado = membresiaService.obtenerReporteIngresosPorPlan();

        if (resultado.isEmpty()) {
            System.out.println("No hay membresias activas registradas.");
            return;
        }

        System.out.println("\nPROYECCION DE INGRESOS POR PLAN");
        System.out.printf("%-15s | %-10s | %-15s%n", "Plan", "Clientes", "Ingreso mensual");
        System.out.println("----------------+------------+-----------------");

        for (Object[] fila : resultado) {
            System.out.printf("%-15s | %-10s | S/ %-12s%n",
                    truncar(String.valueOf(fila[0]), 15),
                    fila[1],
                    formatearBigDecimal((BigDecimal) fila[2]));
        }
    }

    private void imprimirClientes(List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }

        System.out.printf("%-4s | %-12s | %-20s | %-20s | %-12s | %-15s%n",
                "ID", "DNI", "Nombres", "Apellidos", "Registro", "Estado");
        System.out.println("-----+--------------+----------------------+----------------------+--------------+-----------------");

        for (Cliente cliente : clientes) {
            System.out.printf("%-4d | %-12s | %-20s | %-20s | %-12s | %-15s%n",
                    cliente.getId(),
                    truncar(cliente.getDni(), 12),
                    truncar(cliente.getNombres(), 20),
                    truncar(cliente.getApellidos(), 20),
                    cliente.getFechaRegistro(),
                    truncar(cliente.getEstado(), 15));
        }
    }

    private void imprimirCliente(Cliente cliente) {
        System.out.println("\nCliente encontrado:");
        System.out.println("ID: " + cliente.getId());
        System.out.println("DNI: " + cliente.getDni());
        System.out.println("Nombres: " + cliente.getNombres());
        System.out.println("Apellidos: " + cliente.getApellidos());
        System.out.println("Fecha de nacimiento: " + cliente.getFechaNacimiento());
        System.out.println("Telefono: " + valorTexto(cliente.getTelefono()));
        System.out.println("Email: " + valorTexto(cliente.getEmail()));
        System.out.println("Fecha de registro: " + cliente.getFechaRegistro());
        System.out.println("Estado: " + cliente.getEstado());
    }

    private void imprimirMembresias(List<Membresia> membresias) {
        if (membresias.isEmpty()) {
            System.out.println("No hay membresias registradas.");
            return;
        }

        System.out.printf("%-4s | %-10s | %-12s | %-12s | %-10s | %-13s | %-10s%n",
                "ID", "Plan", "Inicio", "Vence", "Precio", "Estado", "Congelado");
        System.out.println("-----+------------+--------------+--------------+------------+---------------+------------");

        for (Membresia membresia : membresias) {
            System.out.printf("%-4d | %-10s | %-12s | %-12s | %-10s | %-13s | %-10d%n",
                    membresia.getId(),
                    truncar(membresia.getTipoPlan(), 10),
                    membresia.getFechaInicio(),
                    membresia.getFechaVencimiento(),
                    formatearBigDecimal(membresia.getPrecioMensual()),
                    truncar(membresia.getEstadoMembresia(), 13),
                    membresia.getDiasCongelados());
        }
    }

    private void imprimirMembresia(Membresia membresia) {
        System.out.println("\nMembresia encontrada:");
        System.out.println("ID: " + membresia.getId());
        System.out.println("Tipo de plan: " + membresia.getTipoPlan());
        System.out.println("Fecha de inicio: " + membresia.getFechaInicio());
        System.out.println("Fecha de vencimiento: " + membresia.getFechaVencimiento());
        System.out.println("Precio mensual: S/ " + formatearBigDecimal(membresia.getPrecioMensual()));
        System.out.println("Estado de membresia: " + membresia.getEstadoMembresia());
        System.out.println("Metodo de pago: " + membresia.getMetodoPago());
        System.out.println("Dias congelados: " + membresia.getDiasCongelados());
    }

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero valido.");
            }
        }
    }

    private LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return LocalDate.parse(scanner.nextLine().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                System.out.println("Ingrese una fecha valida con formato YYYY-MM-DD.");
            }
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private String leerTextoOpcional(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private LocalDate parsearFecha(String valor) {
        try {
            return LocalDate.parse(valor, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("La fecha debe tener formato YYYY-MM-DD.");
        }
    }

    private String truncar(String texto, int maximo) {
        if (texto == null) {
            return "";
        }
        return texto.length() > maximo ? texto.substring(0, maximo - 3) + "..." : texto;
    }

    private String valorTexto(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }

    private String formatearBigDecimal(BigDecimal valor) {
        return valor == null ? "0.00" : valor.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private void mostrarError(Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}