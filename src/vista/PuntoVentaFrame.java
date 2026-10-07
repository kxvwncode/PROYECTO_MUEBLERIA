package vista;

import control.Manejo_Archivos;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PuntoVentaFrame extends JFrame 
{
    private JPanel panelContenidoPrincipal;
    private CardLayout cardLayout;
    private JPanel panelCatalogoProductos;
    private DefaultTableModel modeloCarrito;
    private DefaultTableModel modeloCatalogo;
    private DefaultTableModel modeloVentas;
    private DefaultTableModel modeloCotizaciones;
    private DefaultTableModel modeloApartados;
    private JLabel lbTotal;
    private JTextField txtNumCliente;
    private final Map<String, Integer> carrito = new LinkedHashMap<>();

    public static void refrescarDashboardAbierto() 
    {
        for (Window window : Window.getWindows()) 
        {
            if (window instanceof DashboardFrame) 
            {
                ((DashboardFrame) window).recargarTodo();
            }
        }
    }

    public static void refrescarPuntoVentaAbierto() 
    {
        for (Window window : Window.getWindows()) 
        {
            if (window instanceof PuntoVentaFrame) 
            {
                ((PuntoVentaFrame) window).recargarTodo();
            }
        }
    }

    private void recargarTodo() 
    {
        cargarCatalogoTabla();
        cargarVentas();
        cargarCotizaciones();
        cargarApartados();
        if (panelCatalogoProductos != null) 
        {
            cargarCatalogoProductos(panelCatalogoProductos);
        }
    }

    public PuntoVentaFrame() 
    {
        super("Mueblería 'Suchil' - Panel de Control Venta");
        TemaMuebleria.instalar();
        setSize(1240, 780);
        setMinimumSize(new Dimension(940, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        JPanel panelSidebar = new JPanel();
        panelSidebar.setPreferredSize(new Dimension(230, getHeight()));
        panelSidebar.setBackground(TemaMuebleria.CAFE_OSCURO);
        panelSidebar.setBorder(BorderFactory.createEmptyBorder(22, 14, 18, 14));
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));

        JLabel lbTituloSidebar = new JLabel("<html>MUEBLERÍA<br><b>'Suchil'</b></html>");
        lbTituloSidebar.setForeground(Color.WHITE);
        lbTituloSidebar.setFont(new Font("Serif", Font.BOLD, 23));
        lbTituloSidebar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSidebar.add(lbTituloSidebar);

        JLabel lbSubSidebar = new JLabel("Panel de Control - Venta");
        lbSubSidebar.setForeground(new Color(224, 207, 188));
        lbSubSidebar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbSubSidebar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSidebar.add(lbSubSidebar);
        panelSidebar.add(Box.createVerticalStrut(35));

        JButton btnVenta = crearBotonMenu("🛒 Venta", 110);
        JButton btnCatalogo = crearBotonMenu("📦 Catálogo", 160);
        JButton btnMisVentas = crearBotonMenu("📄 Mis Ventas", 210);
        JButton btnCotizaciones = crearBotonMenu("📋 Cotizaciones / Presupuestos", 260);
        JButton btnApartados = crearBotonMenu("☑️ Apartados / Abonos", 310);

        panelSidebar.add(btnVenta);
        panelSidebar.add(btnCatalogo);
        panelSidebar.add(btnMisVentas);
        panelSidebar.add(btnCotizaciones);
        panelSidebar.add(btnApartados);
        panelSidebar.add(Box.createVerticalGlue());

        JLabel lblVendedor = new JLabel("👤 Vendedor");
        lblVendedor.setForeground(Color.WHITE);
        lblVendedor.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblVendedor.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        panelSidebar.add(lblVendedor);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCerrarSesion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        panelSidebar.add(btnCerrarSesion);

        add(panelSidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelContenidoPrincipal = new JPanel(cardLayout);

        panelContenidoPrincipal.add(crearVistaPuntoDeVenta(), "VENTA");
        panelContenidoPrincipal.add(crearVistaCatalogo(), "CATALOGO");
        panelContenidoPrincipal.add(crearVistaMisVentas(), "MIS_VENTAS");
        panelContenidoPrincipal.add(crearVistaCotizaciones(), "COTIZACIONES");
        panelContenidoPrincipal.add(crearVistaApartados(), "APARTADOS");

        add(panelContenidoPrincipal, BorderLayout.CENTER);

        btnVenta.addActionListener(e -> {
            cargarCatalogoProductos(panelCatalogoProductos);
            cardLayout.show(panelContenidoPrincipal, "VENTA");
        });
        btnCatalogo.addActionListener(e -> {
            cargarCatalogoTabla();
            cardLayout.show(panelContenidoPrincipal, "CATALOGO");
        });
        btnMisVentas.addActionListener(e -> {
            cargarVentas();
            cardLayout.show(panelContenidoPrincipal, "MIS_VENTAS");
        });
        btnCotizaciones.addActionListener(e -> {
            cargarCotizaciones();
            cardLayout.show(panelContenidoPrincipal, "COTIZACIONES");
        });
        btnApartados.addActionListener(e -> {
            cargarApartados();
            cardLayout.show(panelContenidoPrincipal, "APARTADOS");
        });

        btnCerrarSesion.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        TemaMuebleria.aplicar(this);
    }

    private JButton crearBotonMenu(String texto, int y) 
    {
        JButton boton = new JButton(texto);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        boton.setPreferredSize(new Dimension(200, 42));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 8));
        boton.setMargin(new Insets(8, 12, 8, 8));
        return boton;
    }

    private JPanel crearVistaPuntoDeVenta() 
    {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        panel.setBackground(TemaMuebleria.CREMA);

        JLabel lbTitulo = new JLabel("Punto de Venta Rápido");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbTitulo.setForeground(TemaMuebleria.TEXTO);
        panel.add(lbTitulo, BorderLayout.NORTH);

        JPanel panelBusqueda = crearTarjetaPuntoVenta("Catálogo");
        panelBusqueda.setLayout(new BorderLayout(0, 12));
        JTextField txtBuscar = new JTextField();
        txtBuscar.setToolTipText("Buscar producto");
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> cargarCatalogoProductos(panelCatalogoProductos, txtBuscar.getText().trim()));
        JComboBox<String> cmbCategoria = new JComboBox<>(new String[] {"Todas", "Salas", "Recámaras", "Comedores", "General"});
        cmbCategoria.addActionListener(e -> cargarCatalogoProductos(panelCatalogoProductos, txtBuscar.getText().trim(), (String) cmbCategoria.getSelectedItem()));

        JPanel controlesBusqueda = new JPanel(new BorderLayout(8, 0));
        controlesBusqueda.setOpaque(false);
        controlesBusqueda.add(cmbCategoria, BorderLayout.WEST);
        controlesBusqueda.add(txtBuscar, BorderLayout.CENTER);
        controlesBusqueda.add(btnBuscar, BorderLayout.EAST);
        panelBusqueda.add(controlesBusqueda, BorderLayout.NORTH);

        panelCatalogoProductos = new JPanel(new GridLayout(0, 3, 10, 10));
        panelCatalogoProductos.setBackground(TemaMuebleria.CREMA);
        cargarCatalogoProductos(panelCatalogoProductos);
        JScrollPane scrollCatalogo = new JScrollPane(panelCatalogoProductos);
        scrollCatalogo.setBorder(BorderFactory.createEmptyBorder());
        panelBusqueda.add(scrollCatalogo, BorderLayout.CENTER);

        JPanel panelCarrito = crearTarjetaPuntoVenta("Carrito de compra");
        panelCarrito.setLayout(new BorderLayout(0, 12));
        modeloCarrito = new DefaultTableModel(new String[] {"Cant", "Producto", "Subtotal"}, 0);
        JTable tablaCarrito = new JTable(modeloCarrito);
        panelCarrito.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);

        lbTotal = new JLabel("Total: $0.00 MXN");
        lbTotal.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbTotal.setForeground(TemaMuebleria.CAFE_OSCURO);
        JButton btnVaciar = new JButton("Vaciar Carrito");
        btnVaciar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnVaciar.addActionListener(e -> {
            carrito.clear();
            modeloCarrito.setRowCount(0);
            actualizarTotalCarrito();
        });

        JButton btnDescuento = new JButton("Aplicar Descuento 10%");
        btnDescuento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnDescuento.addActionListener(e -> {
            if (carrito.isEmpty()) 
            {
                JOptionPane.showMessageDialog(this, "No hay productos en el carrito.", "Carrito vacío", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double subtotal = calcularTotalCarrito();
            double descuento = subtotal * 0.10;
            lbTotal.setText("Total: $" + String.format("%.2f", subtotal - descuento) + " MXN");
            JOptionPane.showMessageDialog(this, "Se aplicó un descuento de $" + String.format("%.2f", descuento) + " MXN.", "Descuento", JOptionPane.INFORMATION_MESSAGE);
        });

        JLabel lbNotaDesc = new JLabel("Descuento del 10% sobre el total");
        lbNotaDesc.setForeground(TemaMuebleria.CAFE_CLARO);
        JPanel accionesCarrito = new JPanel();
        accionesCarrito.setOpaque(false);
        accionesCarrito.setLayout(new BoxLayout(accionesCarrito, BoxLayout.Y_AXIS));
        for (JComponent component : new JComponent[] {lbTotal, btnVaciar, btnDescuento, lbNotaDesc}) 
        {
            component.setAlignmentX(Component.LEFT_ALIGNMENT);
            accionesCarrito.add(component);
            accionesCarrito.add(Box.createVerticalStrut(8));
        }
        panelCarrito.add(accionesCarrito, BorderLayout.SOUTH);

        JPanel panelCobro = crearTarjetaPuntoVenta("Cliente y cobro");
        panelCobro.setLayout(new BorderLayout(0, 12));
        JLabel lbDatosCliente = new JLabel("Datos del cliente");
        lbDatosCliente.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbDatosCliente.setForeground(TemaMuebleria.TEXTO);
        JLabel lbNumeroCliente = new JLabel("No. de cliente (aleatorio)");
        lbNumeroCliente.setForeground(TemaMuebleria.CAFE);
        txtNumCliente = new JTextField(generarNumeroCliente());
        txtNumCliente.setEditable(false);
        JTextField txtNombreCliente = new JTextField();
        txtNombreCliente.setToolTipText("Nombre del cliente");

        JRadioButton rbEfectivo = new JRadioButton("Efectivo", true);
        JRadioButton rbTarjeta = new JRadioButton("Tarjeta");
        JRadioButton rbApartado = new JRadioButton("Apartado");
        for (JRadioButton radio : new JRadioButton[]{rbEfectivo, rbTarjeta, rbApartado}) 
        {
            radio.setBackground(TemaMuebleria.BLANCO);
        }
        ButtonGroup grupoPago = new ButtonGroup();
        grupoPago.add(rbEfectivo);
        grupoPago.add(rbTarjeta);
        grupoPago.add(rbApartado);
        JPanel opcionesPago = new JPanel(new GridLayout(0, 1, 0, 2));
        opcionesPago.setOpaque(false);
        opcionesPago.add(rbEfectivo);
        opcionesPago.add(rbTarjeta);
        opcionesPago.add(rbApartado);

        JPanel datosCobro = new JPanel();
        datosCobro.setOpaque(false);
        datosCobro.setLayout(new BoxLayout(datosCobro, BoxLayout.Y_AXIS));
        datosCobro.add(lbDatosCliente);
        datosCobro.add(Box.createVerticalStrut(16));
        datosCobro.add(lbNumeroCliente);
        datosCobro.add(Box.createVerticalStrut(5));
        datosCobro.add(txtNumCliente);
        datosCobro.add(Box.createVerticalStrut(12));
        datosCobro.add(new JLabel("Nombre del cliente"));
        datosCobro.add(Box.createVerticalStrut(5));
        datosCobro.add(txtNombreCliente);
        datosCobro.add(Box.createVerticalStrut(18));
        JLabel lbOpcPago = new JLabel("Opción de pago");
        lbOpcPago.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbOpcPago.setForeground(TemaMuebleria.TEXTO);
        datosCobro.add(lbOpcPago);
        datosCobro.add(Box.createVerticalStrut(5));
        datosCobro.add(opcionesPago);
        panelCobro.add(datosCobro, BorderLayout.NORTH);

        JButton btnProcesar = new JButton("Procesar Venta y Generar Ticket");
        btnProcesar.setPreferredSize(new Dimension(0, 48));
        btnProcesar.addActionListener(e -> procesarVenta(
                txtNombreCliente.getText(),
                txtNumCliente.getText(),
                rbEfectivo.isSelected() ? "Efectivo" : (rbTarjeta.isSelected() ? "Tarjeta" : "Apartado")));
        panelCobro.add(btnProcesar, BorderLayout.SOUTH);

        JPanel secciones = new JPanel(new GridBagLayout());
        secciones.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 12);
        gbc.gridx = 0;
        gbc.weightx = 0.42;
        secciones.add(panelBusqueda, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.27;
        secciones.add(panelCarrito, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.31;
        gbc.insets = new Insets(0, 0, 0, 0);
        secciones.add(panelCobro, gbc);
        panel.add(secciones, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTarjetaPuntoVenta(String titulo)
    {
        JPanel panel = new JPanel();
        panel.setBackground(TemaMuebleria.BLANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(TemaMuebleria.BORDE),
                        titulo,
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13),
                        TemaMuebleria.CAFE_OSCURO),
                BorderFactory.createEmptyBorder(10, 12, 12, 12)));
        return panel;
    }

    private JPanel crearVistaCatalogo() 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TemaMuebleria.CREMA);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel lbTitulo = new JLabel("Catálogo de Muebles");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbTitulo.setForeground(TemaMuebleria.TEXTO);
        lbTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(lbTitulo, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre", "Costo", "Precio de Venta", "Stock"};
        modeloCatalogo = new DefaultTableModel(columnas, 0);
        JTable tablaCatalogo = new JTable(modeloCatalogo);
        cargarCatalogoTabla();

        panel.add(new JScrollPane(tablaCatalogo), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearVistaMisVentas() 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TemaMuebleria.CREMA);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel lbTitulo = new JLabel("Mis Ventas");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbTitulo.setForeground(TemaMuebleria.TEXTO);
        lbTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(lbTitulo, BorderLayout.NORTH);

        String[] columnas = {"ID Venta", "Producto", "Cantidad", "Total", "Fecha", "Cliente", "Método de pago", "Número de cliente"};
        modeloVentas = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloVentas);
        cargarVentas();

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearVistaCotizaciones() 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TemaMuebleria.CREMA);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel lbTitulo = new JLabel("Cotizaciones / Presupuestos");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbTitulo.setForeground(TemaMuebleria.TEXTO);
        lbTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(lbTitulo, BorderLayout.NORTH);

        String[] columnas = {"ID", "Cliente", "Producto", "Total", "Fecha"};
        modeloCotizaciones = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloCotizaciones);
        cargarCotizaciones();

        JButton btnAgregar = new JButton("Guardar cotización de prueba");
        btnAgregar.addActionListener(e -> {
            String[] fila = {"COT-1", "Cliente Demo", "Sofá Moderno", "$12500.00", new SimpleDateFormat("yyyy-MM-dd").format(new Date())};
            guardarRegistroCsv("data/Cotizaciones.csv", "ID,Cliente,Producto,Total,Fecha", fila);
            PuntoVentaFrame.refrescarPuntoVentaAbierto();
            JOptionPane.showMessageDialog(this, "Cotización registrada.", "Cotización", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        panelBotones.setOpaque(false);
        panelBotones.add(btnAgregar);
        panel.add(panelBotones, BorderLayout.SOUTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearVistaApartados() 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TemaMuebleria.CREMA);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel lbTitulo = new JLabel("Apartados / Abonos");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbTitulo.setForeground(TemaMuebleria.TEXTO);
        lbTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(lbTitulo, BorderLayout.NORTH);

        String[] columnas = {"ID Apartado", "ID Venta", "Cliente", "No. Cliente", "Producto", "Total", "Abono inicial (25%)", "Saldo pendiente (75%)", "Fecha"};
        modeloApartados = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloApartados);
        cargarApartados();

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void cargarCatalogoTabla() 
    {
        if (modeloCatalogo == null) return;
        modeloCatalogo.setRowCount(0);
        for (String[] fila : Manejo_Archivos.leerDatosCSV("data/Inventarios.csv", "ID")) 
        {
            if (fila != null && fila.length >= 5) modeloCatalogo.addRow(fila);
        }
    }

    private void cargarVentas() 
    {
        if (modeloVentas == null) return;
        modeloVentas.setRowCount(0);
        for (String[] fila : Manejo_Archivos.leerDatosCSV("data/Ventas.csv", "ID_Venta")) 
        {
            if (fila != null && fila.length > 0) modeloVentas.addRow(fila);
        }
    }

    private void cargarCotizaciones() 
    {
        if (modeloCotizaciones == null) return;
        modeloCotizaciones.setRowCount(0);
        for (String[] fila : Manejo_Archivos.leerDatosCSV("data/Cotizaciones.csv", "ID")) 
        {
            if (fila != null && fila.length >= 5) modeloCotizaciones.addRow(fila);
        }
    }

    private void cargarApartados() 
    {
        if (modeloApartados == null) return;
        modeloApartados.setRowCount(0);
        for (String[] fila : Manejo_Archivos.leerDatosCSV("data/Apartados.csv", "ID")) 
        {
            if (fila != null && fila.length >= 6) modeloApartados.addRow(normalizarApartado(fila));
        }
    }

    private String[] normalizarApartado(String[] fila) 
    {
        if (fila.length >= 9) return fila;
        if (fila.length < 6) return fila;

        double abono = parseImporte(fila[3]);
        double saldo = parseImporte(fila[4]);
        return new String[] {
            fila[0], "", fila[1], "", fila[2],
            String.format(java.util.Locale.US, "%.2f", abono + saldo),
            fila[3], fila[4], fila[5]
        };
    }

    private double parseImporte(String importe) 
    {
        return Double.parseDouble(importe.trim().replace("$", ""));
    }

    private String generarNumeroCliente() 
    {
        Set<String> numerosExistentes = new HashSet<>();
        for (String[] venta : Manejo_Archivos.leerDatosCSV("data/Ventas.csv", "ID_Venta")) 
        {
            if (venta != null && venta.length > 7) numerosExistentes.add(venta[7].trim());
        }

        String numero;
        do 
        {
            numero = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        } while (numerosExistentes.contains(numero));
        return numero;
    }

    private int obtenerSiguienteIdApartado() 
    {
        int ultimoId = 0;
        for (String[] apartado : Manejo_Archivos.leerDatosCSV("data/Apartados.csv", "ID")) 
        {
            if (apartado == null || apartado.length == 0) continue;
            String id = apartado[0].trim().toUpperCase();
            if (!id.startsWith("AP-")) continue;
            try{
                ultimoId = Math.max(ultimoId, Integer.parseInt(id.substring(3)));
            } 
            catch (NumberFormatException e) 
            {
                //Ignorar identificadores que no sigan el formato AP
            }
        }
        return ultimoId + 1;
    }

    private void cargarCatalogoProductos(JPanel contenedor) {
        cargarCatalogoProductos(contenedor, "", "Todas");
    }

    private void cargarCatalogoProductos(JPanel contenedor, String textoBusqueda) 
    {
        cargarCatalogoProductos(contenedor, textoBusqueda, "Todas");
    }

    private void cargarCatalogoProductos(JPanel contenedor, String textoBusqueda, String categoriaSeleccionada) 
    {
        if (contenedor == null) return;
        contenedor.removeAll();

        List<String[]> inventario = Manejo_Archivos.leerCSV("data/Inventarios.csv");
        for (int i = 1; i < inventario.size(); i++) {
            String[] producto = inventario.get(i);
            if (producto == null || producto.length < 5) continue;

            String nombreProducto = producto[1].trim();
            String categoria = categoriaDelProducto(nombreProducto);
            boolean coincideBusqueda = textoBusqueda == null || textoBusqueda.trim().isEmpty() || nombreProducto.toLowerCase().contains(textoBusqueda.toLowerCase());
            boolean coincideCategoria = "Todas".equals(categoriaSeleccionada) || categoriaSeleccionada.equalsIgnoreCase(categoria);

            if (!coincideBusqueda || !coincideCategoria) continue;

            JPanel cardProducto = new JPanel(new GridLayout(4, 1, 0, 4));
            cardProducto.setBackground(TemaMuebleria.BLANCO);
            cardProducto.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(TemaMuebleria.BORDE),
                    BorderFactory.createEmptyBorder(9, 9, 9, 9)));

            JLabel lblNombre = new JLabel("<html>" + nombreProducto + "</html>", SwingConstants.CENTER);
            lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblNombre.setForeground(TemaMuebleria.TEXTO);
            cardProducto.add(lblNombre);

            JLabel lblPrecio = new JLabel("$" + producto[3].trim(), SwingConstants.CENTER);
            lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblPrecio.setForeground(TemaMuebleria.CAFE);
            cardProducto.add(lblPrecio);

            JLabel lblStock = new JLabel("Disponibles: " + producto[4].trim(), SwingConstants.CENTER);
            lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblStock.setForeground(TemaMuebleria.CAFE_CLARO);
            cardProducto.add(lblStock);

            JButton btnAgregar = new JButton("Agregar");
            btnAgregar.addActionListener(e -> agregarAlCarrito(producto));
            cardProducto.add(btnAgregar);

            contenedor.add(cardProducto);
        }

        contenedor.revalidate();
        contenedor.repaint();
    }

    private String categoriaDelProducto(String nombreProducto) 
    {
        String nombre = nombreProducto.toLowerCase();
        if (nombre.contains("sofa") || nombre.contains("sala") || nombre.contains("mueble")) return "Salas";
        if (nombre.contains("recamara") || nombre.contains("cama") || nombre.contains("armario")) return "Recámaras";
        if (nombre.contains("comedor") || nombre.contains("mesa") || nombre.contains("silla")) return "Comedores";
        return "General";
    }

    private void agregarAlCarrito(String[] producto) 
    {
        if (producto == null || producto.length < 5) return;
        String nombre = producto[1].trim();
        int cantidadActual = carrito.getOrDefault(nombre, 0);
        carrito.put(nombre, cantidadActual + 1);
        actualizarCarrito();
    }

    private void actualizarCarrito() 
    {
        if (modeloCarrito == null) return;
        modeloCarrito.setRowCount(0);

        for (Map.Entry<String, Integer> entry : carrito.entrySet()) 
        {
            String nombre = entry.getKey();
            int cantidad = entry.getValue();
            List<String[]> inventario = Manejo_Archivos.leerCSV("data/Inventarios.csv");
            double precioUnitario = 0.0;
            for (int i = 1; i < inventario.size(); i++) 
            {
                String[] producto = inventario.get(i);
                if (producto != null && producto.length >= 5 && producto[1].trim().equalsIgnoreCase(nombre)) 
                {
                    precioUnitario = Double.parseDouble(producto[3].trim());
                    break;
                }
            }
            double subtotal = cantidad * precioUnitario;
            modeloCarrito.addRow(new Object[] {cantidad, nombre, "$" + String.format("%.2f", subtotal)});
        }

        actualizarTotalCarrito();
    }

    private double calcularTotalCarrito() 
    {
        double total = 0.0;
        List<String[]> inventario = Manejo_Archivos.leerCSV("data/Inventarios.csv");

        for (Map.Entry<String, Integer> entry : carrito.entrySet()) 
        {
            String nombre = entry.getKey();
            int cantidad = entry.getValue();
            for (int i = 1; i < inventario.size(); i++) 
            {
                String[] producto = inventario.get(i);
                if (producto != null && producto.length >= 5 && producto[1].trim().equalsIgnoreCase(nombre)) 
                {
                    total += Double.parseDouble(producto[3].trim()) * cantidad;
                    break;
                }
            }
        }
        return total;
    }

    private void actualizarTotalCarrito() 
    {
        if (lbTotal == null) return;
        lbTotal.setText("Total: $" + String.format("%.2f", calcularTotalCarrito()) + " MXN");
    }

    private void procesarVenta(String nombreCliente, String numeroCliente, String metodoPago) 
    {
        if (carrito.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Carrito vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        nombreCliente = nombreCliente.trim();
        if (nombreCliente.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Ingresa el nombre del cliente.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        int nuevoId = obtenerUltimoIdVenta() + 1;
        int siguienteIdApartado = obtenerSiguienteIdApartado();

        List<String[]> inventario = Manejo_Archivos.leerCSV("data/Inventarios.csv");
        List<String[]> ventasPendientes = new ArrayList<>();
        List<String[]> apartadosPendientes = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : carrito.entrySet()) 
        {
            String nombreProducto = entry.getKey();
            int cantidadVendida = entry.getValue();
            String[] productoEncontrado = null;

            for (int i = 1; i < inventario.size(); i++) 
            {
                String[] producto = inventario.get(i);
                if (producto == null || producto.length < 5) continue;
                if (producto[1].trim().equalsIgnoreCase(nombreProducto)) 
                {
                    int stockActual = Integer.parseInt(producto[4].trim());
                    if (stockActual < cantidadVendida) 
                    {
                        JOptionPane.showMessageDialog(this, "No hay suficiente stock para: " + nombreProducto, "Stock insuficiente", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    productoEncontrado = producto;
                    break;
                }
            }

            if (productoEncontrado == null) 
            {
                JOptionPane.showMessageDialog(this, "No se encontró el producto: " + nombreProducto, "Producto no encontrado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double subtotal = Double.parseDouble(productoEncontrado[3].trim()) * cantidadVendida;
            ventasPendientes.add(new String[] 
            {
                String.valueOf(nuevoId), nombreProducto, String.valueOf(cantidadVendida),
                String.valueOf(subtotal), fecha, nombreCliente.trim(), metodoPago, numeroCliente.trim()
            });
            if ("Apartado".equals(metodoPago)) 
            {
                double abonoInicial = Math.round(subtotal * 0.25 * 100.0) / 100.0;
                double saldoPendiente = Math.round((subtotal - abonoInicial) * 100.0) / 100.0;
                apartadosPendientes.add(new String[] 
                {
                    "AP-" + siguienteIdApartado++,
                    String.valueOf(nuevoId),
                    nombreCliente.trim(),
                    numeroCliente.trim(),
                    nombreProducto,
                    String.format(java.util.Locale.US, "%.2f", subtotal),
                    String.format(java.util.Locale.US, "%.2f", abonoInicial),
                    String.format(java.util.Locale.US, "%.2f", saldoPendiente),
                    fecha
                });
            }
        }

        List<String> inventarioActualizado = new ArrayList<>();
        if (!inventario.isEmpty()) 
        {
            inventarioActualizado.add(String.join(",", inventario.get(0)));
        } else {
            inventarioActualizado.add("ID,Nombre,Precio_Costo,Precio_Venta,Stock");
        }
        for (int i = 1; i < inventario.size(); i++) 
        {
            String[] producto = inventario.get(i);
            if (producto == null || producto.length < 5) continue;
            int cantidadVendida = carrito.getOrDefault(producto[1].trim(), 0);
            int stockNuevo = Integer.parseInt(producto[4].trim()) - cantidadVendida;
            inventarioActualizado.add(
                    producto[0] + "," + producto[1] + "," + producto[2] + "," + producto[3] + "," + stockNuevo);
        }

        try {
            guardarRegistrosCsv("data/Ventas.csv",
                    "ID_Venta,Producto,Cantidad,Total,Fecha,Cliente,MetodoPago,NumeroCliente",
                    ventasPendientes);
            if (!apartadosPendientes.isEmpty()) 
            {
                guardarApartadosCsv(apartadosPendientes);
            }
            Manejo_Archivos.sobrescribirCSV("data/Inventarios.csv", inventarioActualizado);
        } 
        catch (IOException e) 
        {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la venta y sus registros: " + e.getMessage(),
                    "Error al guardar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        carrito.clear();
        modeloCarrito.setRowCount(0);
        actualizarTotalCarrito();
        txtNumCliente.setText(generarNumeroCliente());
        JOptionPane.showMessageDialog(this, "Venta registrada correctamente.", "Venta finalizada", JOptionPane.INFORMATION_MESSAGE);
        PuntoVentaFrame.refrescarPuntoVentaAbierto();
        PuntoVentaFrame.refrescarDashboardAbierto();
    }

    private void guardarRegistrosCsv(String ruta, String cabecera, List<String[]> registros) throws IOException 
    {
        File archivo = new File(ruta);
        File directorio = archivo.getParentFile();
        if (directorio != null && !directorio.exists() && !directorio.mkdirs()) 
        {
            throw new IOException("No se pudo crear el directorio " + directorio.getPath());
        }

        boolean escribirCabecera = !archivo.exists() || archivo.length() == 0;
        try (FileWriter fileWriter = new FileWriter(archivo, true); PrintWriter writer = new PrintWriter(fileWriter)) 
        {
            if (escribirCabecera) writer.println(cabecera);
            for (String[] registro : registros) 
            {
                writer.println(String.join(",", registro));
            }
            if (writer.checkError()) 
            {
                throw new IOException("Error escribiendo el archivo " + ruta);
            }
        }
    }

    private void guardarApartadosCsv(List<String[]> nuevosApartados) throws IOException 
    {
        Path archivo = Paths.get("data/Apartados.csv");
        Path directorio = archivo.getParent();
        if (directorio != null) Files.createDirectories(directorio);

        List<String[]> apartadosActuales = Manejo_Archivos.leerDatosCSV("data/Apartados.csv", "ID");
        List<String[]> apartadosActualizados = new ArrayList<>();
        for (String[] apartado : apartadosActuales) 
        {
            if (apartado != null && apartado.length >= 6) 
            {
                apartadosActualizados.add(normalizarApartado(apartado));
            }
        }
        apartadosActualizados.addAll(nuevosApartados);

        Path temporal = Files.createTempFile(directorio, "Apartados-", ".tmp");
        try {
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(temporal))) 
            {
                writer.println("ID,ID_Venta,Cliente,NumeroCliente,Producto,Total,Abono,Saldo,Fecha");
                for (String[] apartado : apartadosActualizados) 
                {
                    writer.println(String.join(",", apartado));
                }
                if (writer.checkError()) 
                {
                    throw new IOException("Error escribiendo el archivo de apartados.");
                }
            }

            try {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } 
            catch (AtomicMoveNotSupportedException e) 
            {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    private void guardarRegistroCsv(String ruta, String cabecera, String[] fila) 
    {
        File directorio = new File("data");
        if (!directorio.exists()) 
        {
            directorio.mkdirs();
        }

        File archivo = new File(ruta);
        if (!archivo.exists()) 
        {
            try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) 
            {
                pw.println(cabecera);
            } 
            catch (Exception e) 
            {
                System.out.println("Error al crear archivo: " + e.getMessage());
            }
        }

        try (FileWriter fileWriter = new FileWriter(archivo, true); PrintWriter pw = new PrintWriter(fileWriter)) 
        {
            StringBuilder linea = new StringBuilder();
            for (int i = 0; i < fila.length; i++) 
            {
                if (i > 0) linea.append(",");
                linea.append(fila[i]);
            }
            pw.println(linea.toString());
        } 
        catch (Exception e) 
        {
            System.out.println("Error al guardar registro: " + e.getMessage());
        }
    }

    private int obtenerUltimoIdVenta() 
    {
        List<String[]> ventas = Manejo_Archivos.leerDatosCSV("data/Ventas.csv", "ID_Venta");
        int ultimoId = 0;
        for (String[] fila : ventas) 
        {
            if (fila != null && fila.length > 0) 
            {
                try {
                    int id = Integer.parseInt(fila[0].trim());
                    if (id > ultimoId) ultimoId = id;
                } 
                catch (NumberFormatException e) 
                {
                }
            }
        }
        return ultimoId;
    }
}
