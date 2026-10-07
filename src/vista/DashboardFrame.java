package vista;

import control.Manejo_Archivos;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DashboardFrame extends JFrame 
{

    public static void refrescarDashboardAbierto() 
    {
        for (Window window : Window.getWindows()) 
        {
            if (window instanceof DashboardFrame) 
            {
                ((DashboardFrame) window).recargarTodo();
            }
        }
        PuntoVentaFrame.refrescarPuntoVentaAbierto();
    }

    public void recargarTodo() 
    {
        cargarDatosDashboard();
        cargarDatosInventario();
        cargarDatosVentas();
        cargarDatosFinanzas();
    }

    private JPanel panelContenidoPrincipal;
    private CardLayout cardLayout;
    private JTextField txtNombre, txtCosto, txtVenta, txtStock;
    private DefaultTableModel modeloVentasDashboard;
    private DefaultTableModel modeloInventario;
    private DefaultTableModel modeloVentasGeneral;
    private JLabel lbValorVentasHoy;
    private JLabel lbValorGanancias;
    private JLabel lbValorIngresosBrutos;
    private JLabel lbValorCostosTotales;
    private JLabel lbValorGananciaNetaReal;
    private DefaultTableModel modeloFinanzasDetalle;

    public DashboardFrame() 
    {
        super("Mueblería 'Suchil' - Panel de Control Dueño");
        TemaMuebleria.instalar();
        setSize(1120, 740);
        setMinimumSize(new Dimension(820, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        //Menu lateral(SIDEBAR)
        JPanel panelSidebar = new JPanel();
        panelSidebar.setPreferredSize(new Dimension(230, getHeight()));
        panelSidebar.setBackground(TemaMuebleria.CAFE_OSCURO);
        panelSidebar.setBorder(BorderFactory.createEmptyBorder(22, 14, 18, 14));
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));

        //Título en el menu lateral
        JLabel lblTituloSidebar = new JLabel("<html>MUEBLERÍA<br><b>'Suchil'</b></html>");
        lblTituloSidebar.setForeground(Color.WHITE);
        lblTituloSidebar.setFont(new Font("Serif", Font.BOLD, 23));
        lblTituloSidebar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSidebar.add(lblTituloSidebar);

        JLabel lblSubSidebar = new JLabel("Panel de Control - Dueño");
        lblSubSidebar.setForeground(new Color(224, 207, 188));
        lblSubSidebar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubSidebar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSidebar.add(lblSubSidebar);
        panelSidebar.add(Box.createVerticalStrut(35));

        //Botones del menu lateral
        JButton btnDashboard = crearBotonMenu("📊 Dashboard", 110);
        JButton btnInventario = crearBotonMenu("📦 Inventario", 160);
        JButton btnVentas = crearBotonMenu("📄 Ventas", 210);
        JButton btnFinanzas = crearBotonMenu("📈 Finanzas y Ganancias", 260);
        JButton btnAgregarStock = crearBotonMenu("➕ Agregar Stock", 310);

        panelSidebar.add(btnDashboard);
        panelSidebar.add(btnInventario);
        panelSidebar.add(btnVentas);
        panelSidebar.add(btnFinanzas);
        panelSidebar.add(btnAgregarStock);
        panelSidebar.add(Box.createVerticalGlue());

        //Cerrar sesion en la parte inferior del menu lateral
        JLabel lbAdmin = new JLabel("👤 Administrador");
        lbAdmin.setForeground(Color.WHITE);
        lbAdmin.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbAdmin.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        panelSidebar.add(lbAdmin);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCerrarSesion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        panelSidebar.add(btnCerrarSesion);

        add(panelSidebar, BorderLayout.WEST);

        //Panel central
        cardLayout = new CardLayout();
        panelContenidoPrincipal = new JPanel(cardLayout);

        //Vistas de cada Boton
        panelContenidoPrincipal.add(crearVistaDashboard(), "DASHBOARD");
        panelContenidoPrincipal.add(crearVistaInventario(), "INVENTARIO");
        panelContenidoPrincipal.add(crearVistaVentas(), "VENTAS");
        panelContenidoPrincipal.add(crearVistaFinanzas(), "FINANZAS");
        panelContenidoPrincipal.add(crearVistaAgregarStock(), "AGREGAR_STOCK");

        add(panelContenidoPrincipal, BorderLayout.CENTER);

        //Accionees de los bottones
        btnDashboard.addActionListener(e -> {
            cargarDatosDashboard();
            cardLayout.show(panelContenidoPrincipal, "DASHBOARD");
        });

        btnInventario.addActionListener(e -> {
            cargarDatosInventario();
            cardLayout.show(panelContenidoPrincipal, "INVENTARIO");
        });

        btnVentas.addActionListener(e -> {
            cargarDatosVentas();
            cardLayout.show(panelContenidoPrincipal, "VENTAS");
        });

        btnFinanzas.addActionListener(e -> {
            cargarDatosFinanzas();
            cardLayout.show(panelContenidoPrincipal, "FINANZAS");
        });
        
        btnAgregarStock.addActionListener(e -> {
            cardLayout.show(panelContenidoPrincipal, "AGREGAR_STOCK");
        });

        btnCerrarSesion.addActionListener(e -> 
        {
            new LoginFrame().setVisible(true);
            dispose();
        });
        TemaMuebleria.aplicar(this);
    }

    //Estilo para los botones del menu
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

    //Vista del Dashboard
    private JPanel crearVistaDashboard() 
    {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));
        panel.setBackground(TemaMuebleria.CREMA);

        JLabel lbTitulo = new JLabel("Dashboard General");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbTitulo.setForeground(TemaMuebleria.TEXTO);

        JPanel panelMetricas = new JPanel(new GridLayout(1, 2, 16, 0));
        panelMetricas.setOpaque(false);
        JPanel cardVentas = crearTarjetaMetrica("Ventas Totales", "$0.00 MXN", 0, 0, 0, 0);
        lbValorVentasHoy = (JLabel) cardVentas.getComponent(1); //Obtenemos el JLabel del valor
        panelMetricas.add(cardVentas);

        JPanel cardGanancias = crearTarjetaMetrica("Ganancia Neta Estimada", "$0.00 MXN", 0, 0, 0, 0);
        lbValorGanancias = (JLabel) cardGanancias.getComponent(1);
        panelMetricas.add(cardGanancias);

        JPanel encabezado = new JPanel(new BorderLayout(0, 16));
        encabezado.setOpaque(false);
        encabezado.add(lbTitulo, BorderLayout.NORTH);
        encabezado.add(panelMetricas, BorderLayout.CENTER);
        panel.add(encabezado, BorderLayout.NORTH);

        JLabel lbTablaTitulo = new JLabel("Ventas Recientes");
        lbTablaTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbTablaTitulo.setForeground(TemaMuebleria.TEXTO);

        String[] columnas = {"ID Venta", "Producto", "Cantidad", "Total", "Fecha", "Cliente", "Método de pago", "Número de cliente"};
        modeloVentasDashboard = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloVentasDashboard);
        JScrollPane scroll = new JScrollPane(tabla);
        JPanel ventas = new JPanel(new BorderLayout(0, 10));
        ventas.setOpaque(false);
        ventas.add(lbTablaTitulo, BorderLayout.NORTH);
        ventas.add(scroll, BorderLayout.CENTER);
        panel.add(ventas, BorderLayout.CENTER);

        cargarDatosDashboard(); //Carga inicial de los datos
        return panel;
    }

    //Método para cargar los datos
    private void cargarDatosDashboard() 
    {
        if (modeloVentasDashboard == null) return;
        modeloVentasDashboard.setRowCount(0);

        double totalVentas = 0.0;
        List<String[]> listaVentas = Manejo_Archivos.leerDatosCSV("data/Ventas.csv", "ID_Venta");
        for (String[] fila : listaVentas)
        {
            if (fila == null || fila.length < 5) continue;
            modeloVentasDashboard.addRow(fila);
            try {
                totalVentas += Double.parseDouble(fila[3].trim()); //Columna del total de ventas
            } 
            catch (NumberFormatException e) 
            {
                //Ignora errores
            }
        }

        //Actualizacion de textos en las tarjetas a tiempo real
        if (lbValorVentasHoy != null) lbValorVentasHoy.setText("$" + String.format(Locale.US, "%.2f", totalVentas) + " MXN");
        if (lbValorGanancias != null) lbValorGanancias.setText("$" + String.format(Locale.US, "%.2f", (totalVentas * 0.4)) + " MXN");
    }

    private int obtenerUltimoIdInventario(List<String[]> inventario) 
    {
        int ultimoId = 0;
        if (inventario == null || inventario.isEmpty()) return ultimoId;

        for (int i = 1; i < inventario.size(); i++) 
        {
            String[] fila = inventario.get(i);
            if (fila == null || fila.length == 0) continue;
            try {
                int idActual = Integer.parseInt(fila[0].trim());
                if (idActual > ultimoId) 
                {
                    ultimoId = idActual;
                }
            } 
            catch (NumberFormatException e) 
            {
                //Ignora filas con formato invalido
            }
        }

        return ultimoId;
    }

    //Vista de Inventario
    private JPanel crearVistaInventario() 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TemaMuebleria.CREMA);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel lbTitulo = new JLabel("Inventario General");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbTitulo.setForeground(TemaMuebleria.TEXTO);
        lbTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(lbTitulo, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre", "Costo", "Precio de Venta", "Stock"};
        modeloInventario = new DefaultTableModel(columnas, 0);
        
        cargarDatosInventario(); //Metodo para cargar los datos de la tabla

        JTable tablaInventario = new JTable(modeloInventario);
        JScrollPane scrollTabla = new JScrollPane(tablaInventario);
        panel.add(scrollTabla, BorderLayout.CENTER);

        JButton btnEditar = new JButton("Editar producto");
        btnEditar.addActionListener(e -> editarProductoSeleccionado(tablaInventario));
        JButton btnEliminar = new JButton("Eliminar producto");
        btnEliminar.addActionListener(e -> eliminarProductoSeleccionado(tablaInventario));
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        panelAcciones.setOpaque(false);
        panelAcciones.add(btnEditar);
        panelAcciones.add(btnEliminar);
        panel.add(panelAcciones, BorderLayout.SOUTH);

        return panel;
    }

    //Metodo para cargar los datos
    private void cargarDatosInventario() 
    {
        if (modeloInventario == null) return;
        modeloInventario.setRowCount(0); //Limpiar tabla actual
        List<String[]> datosInventario = Manejo_Archivos.leerCSV("data/Inventarios.csv");

        for (int i = 1; i < datosInventario.size(); i++) {
            String[] fila = datosInventario.get(i);
            if (fila == null || fila.length == 0) continue;
            modeloInventario.addRow(fila);
        }
    }

    private void editarProductoSeleccionado(JTable tablaInventario) 
    {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada < 0) 
        {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para editar.", "Producto no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = tablaInventario.convertRowIndexToModel(filaSeleccionada);
        String[] etiquetas = {"Nombre", "Costo", "Precio de venta", "Stock"};
        JTextField[] campos = new JTextField[etiquetas.length];
        JPanel formulario = new JPanel(new GridLayout(etiquetas.length, 2, 8, 8));
        for (int i = 0; i < etiquetas.length; i++) 
        {
            campos[i] = new JTextField(String.valueOf(modeloInventario.getValueAt(filaModelo, i + 1)));
            formulario.add(new JLabel(etiquetas[i] + ":"));
            formulario.add(campos[i]);
        }

        int resultado = JOptionPane.showConfirmDialog(this, formulario, "Editar producto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado != JOptionPane.OK_OPTION) return;

        String nombre = campos[0].getText().trim();
        if (nombre.isEmpty() || nombre.contains(",")) 
        {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio y no puede contener comas.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try 
        {
            double costo = Double.parseDouble(campos[1].getText().trim());
            double precioVenta = Double.parseDouble(campos[2].getText().trim());
            int stock = Integer.parseInt(campos[3].getText().trim());
            if (costo < 0 || precioVenta < 0 || stock < 0) 
            {
                JOptionPane.showMessageDialog(this, "Costo, precio y stock no pueden ser negativos.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = String.valueOf(modeloInventario.getValueAt(filaModelo, 0));
            actualizarRegistroInventario(id, new String[] {
                id, nombre, String.valueOf(costo), String.valueOf(precioVenta), String.valueOf(stock)
            });
            DashboardFrame.refrescarDashboardAbierto();
            JOptionPane.showMessageDialog(this, "Producto actualizado.", "Inventario", JOptionPane.INFORMATION_MESSAGE);
        } 
        catch (NumberFormatException e) 
        {
            JOptionPane.showMessageDialog(this, "Costo y precio deben ser números; stock debe ser un entero.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
        } 
        catch (IOException e) 
        {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el cambio: " + e.getMessage(), "Error de archivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarProductoSeleccionado(JTable tablaInventario) 
    {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada < 0) 
        {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.", "Producto no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = tablaInventario.convertRowIndexToModel(filaSeleccionada);
        String id = String.valueOf(modeloInventario.getValueAt(filaModelo, 0));
        String nombre = String.valueOf(modeloInventario.getValueAt(filaModelo, 1));
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el producto \"" + nombre + "\" del inventario?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try{
            List<String[]> inventario = Manejo_Archivos.leerCSV("data/Inventarios.csv");
            if (inventario.isEmpty()) throw new IOException("El inventario no se pudo leer.");
            List<String> registrosActualizados = new ArrayList<>();
            for (String[] registro : inventario) 
            {
                if (registro.length == 0 || !registro[0].trim().equals(id)) 
                {
                    registrosActualizados.add(String.join(",", registro));
                }
            }
            guardarInventario(registrosActualizados);
            DashboardFrame.refrescarDashboardAbierto();
            JOptionPane.showMessageDialog(this, "Producto eliminado.", "Inventario", JOptionPane.INFORMATION_MESSAGE);
        } 
        catch (IOException e) 
        {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el producto: " + e.getMessage(), "Error de archivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarRegistroInventario(String id, String[] registroActualizado) throws IOException 
    {
        List<String[]> inventario = Manejo_Archivos.leerCSV("data/Inventarios.csv");
        if (inventario.isEmpty()) throw new IOException("El inventario no se pudo leer.");
        List<String> registrosActualizados = new ArrayList<>();
        boolean actualizado = false;
        for (String[] registro : inventario) 
        {
            if (registro.length > 0 && registro[0].trim().equals(id)) 
            {
                registrosActualizados.add(String.join(",", registroActualizado));
                actualizado = true;
            } else {
                registrosActualizados.add(String.join(",", registro));
            }
        }
        if (!actualizado) throw new IOException("El producto ya no está en el archivo.");
        guardarInventario(registrosActualizados);
    }

    private void guardarInventario(List<String> registros) throws IOException 
    {
        Files.write(Paths.get("data/Inventarios.csv"), registros, StandardCharsets.UTF_8);
    }

    //Vista de Ventas
    private JPanel crearVistaVentas() 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TemaMuebleria.CREMA);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel lbTitulo = new JLabel("Historial General de Ventas");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbTitulo.setForeground(TemaMuebleria.TEXTO);
        lbTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(lbTitulo, BorderLayout.NORTH);

        String[] columnas = {"ID Venta", "Producto", "Cantidad", "Total", "Fecha", "Cliente", "Método de pago", "Número de cliente"};
        modeloVentasGeneral = new DefaultTableModel(columnas, 0);

        cargarDatosVentas(); //Carga inicial

        JTable tablaVentas = new JTable(modeloVentasGeneral);
        JScrollPane scrollTabla = new JScrollPane(tablaVentas);
        panel.add(scrollTabla, BorderLayout.CENTER);

        return panel;
    }

    private void cargarDatosVentas() 
    {
        if (modeloVentasGeneral == null) return;
        modeloVentasGeneral.setRowCount(0);
        
        List<String[]> datosVentas = Manejo_Archivos.leerDatosCSV("data/Ventas.csv", "ID_Venta");
        for (String[] fila : datosVentas)
        {
            if (fila == null || fila.length == 0) continue;
            modeloVentasGeneral.addRow(fila);
        }
    }

    //Vista de Finanzas
    private JPanel crearVistaFinanzas() 
    {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));
        panel.setBackground(TemaMuebleria.CREMA);

        JLabel lbTitulo = new JLabel("Reporte Financiero y Ganancias Netas");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbTitulo.setForeground(TemaMuebleria.TEXTO);

        JPanel panelMetricas = new JPanel(new GridLayout(1, 3, 14, 0));
        panelMetricas.setOpaque(false);
        JPanel cardIngresos = crearTarjetaMetrica("Ingresos Totales (Ventas)", "$0.00 MXN", 0, 0, 0, 0);
        lbValorIngresosBrutos = (JLabel) cardIngresos.getComponent(1);
        panelMetricas.add(cardIngresos);

        JPanel cardCostos = crearTarjetaMetrica("Costos Totales de Inventario", "$0.00 MXN", 0, 0, 0, 0);
        lbValorCostosTotales = (JLabel) cardCostos.getComponent(1);
        panelMetricas.add(cardCostos);

        JPanel cardGanancias = crearTarjetaMetrica("Ganancia Neta Real", "$0.00 MXN", 0, 0, 0, 0);
        lbValorGananciaNetaReal = (JLabel) cardGanancias.getComponent(1);
        panelMetricas.add(cardGanancias);

        JPanel encabezado = new JPanel(new BorderLayout(0, 16));
        encabezado.setOpaque(false);
        encabezado.add(lbTitulo, BorderLayout.NORTH);
        encabezado.add(panelMetricas, BorderLayout.CENTER);
        panel.add(encabezado, BorderLayout.NORTH);

        JLabel lbTablaTitulo = new JLabel("Resumen de Rendimiento Financiero");
        lbTablaTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbTablaTitulo.setForeground(TemaMuebleria.TEXTO);

        String[] columnas = {"ID Venta", "Producto", "Cantidad", "Costo Unit.", "Precio Venta", "Ganancia Neta"};
        modeloFinanzasDetalle = new DefaultTableModel(columnas, 0);
        JTable tablaFinanzas = new JTable(modeloFinanzasDetalle);
        JScrollPane scroll = new JScrollPane(tablaFinanzas);
        JPanel detalle = new JPanel(new BorderLayout(0, 10));
        detalle.setOpaque(false);
        detalle.add(lbTablaTitulo, BorderLayout.NORTH);
        detalle.add(scroll, BorderLayout.CENTER);
        panel.add(detalle, BorderLayout.CENTER);

        cargarDatosFinanzas(); //Carga inicial
        return panel;
    }

    //Metodo para calcular cruces financieros entre Ventas e Inventario
    private void cargarDatosFinanzas() 
    {
        if (modeloFinanzasDetalle == null) return;
        modeloFinanzasDetalle.setRowCount(0);

        double ingresosTotales = 0.0;
        double costosTotales = 0.0;
        double gananciaTotalNeta = 0.0;

        List<String[]> listaVentas = Manejo_Archivos.leerDatosCSV("data/Ventas.csv", "ID_Venta");
        List<String[]> listaInventario = Manejo_Archivos.leerCSV("data/Inventarios.csv");

        //Buscar el costo del producto en el inventario por su nombre o id
        for (String[] venta : listaVentas)
        {
            if (venta == null || venta.length < 5) continue;

            try {
                String nombreProducto = venta[1].trim();
                int cantidadVendida = Integer.parseInt(venta[2].trim());
                double totalVenta = Double.parseDouble(venta[3].trim());

                double costoUnitario = 0.0;
                for (int j = 1; j < listaInventario.size(); j++)
                {
                    String[] mueble = listaInventario.get(j);
                    if (mueble == null || mueble.length < 5) continue;
                    if (mueble[1].equalsIgnoreCase(nombreProducto))
                    {
                        costoUnitario = Double.parseDouble(mueble[2].trim());
                        break;
                    }
                }
                double costoTotalLinea = costoUnitario * cantidadVendida;
                double gananciaLinea = totalVenta - costoTotalLinea;

                ingresosTotales += totalVenta;
                costosTotales += costoTotalLinea;
                gananciaTotalNeta += gananciaLinea;

                modeloFinanzasDetalle.addRow(new Object[] 
                {
                    venta[0],
                    nombreProducto,
                    cantidadVendida,
                    "$" + String.format(Locale.US, "%.2f", costoUnitario),
                    "$" + String.format(Locale.US, "%.2f", totalVenta),
                    "$" + String.format(Locale.US, "%.2f", gananciaLinea)
                });

            }
            catch (Exception e)
            {
                //Ignorar filas con formato incompleto
            }
        }
        if (lbValorIngresosBrutos != null) lbValorIngresosBrutos.setText("$" + String.format(Locale.US, "%.2f", ingresosTotales) + " MXN");
        if (lbValorCostosTotales != null) lbValorCostosTotales.setText("$" + String.format(Locale.US, "%.2f", costosTotales) + " MXN");
        if (lbValorGananciaNetaReal != null) lbValorGananciaNetaReal.setText("$" + String.format(Locale.US, "%.2f", gananciaTotalNeta) + " MXN");
    }

    //Metodo para dibujar las tarjetas metricas de las esquinas
    private JPanel crearTarjetaMetrica(String titulo, String valor, int x, int y, int ancho, int alto) 
    {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setBackground(TemaMuebleria.BLANCO);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderLineCustom(),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JLabel lbTit = new JLabel(titulo);
        lbTit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbTit.setForeground(TemaMuebleria.CAFE);
        tarjeta.add(lbTit, BorderLayout.NORTH);

        JLabel lbVal = new JLabel(valor);
        lbVal.setFont(new Font("Segoe UI", Font.BOLD, 23));
        lbVal.setForeground(TemaMuebleria.CAFE_OSCURO);
        tarjeta.add(lbVal, BorderLayout.CENTER);

        return tarjeta;
    }

    //Vista para agregar stock
    private JPanel crearVistaAgregarStock() 
    {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));
        panel.setBackground(TemaMuebleria.CREMA);

        JLabel lbTitulo = new JLabel("Registrar Nuevo Mueble");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lbTitulo.setForeground(TemaMuebleria.TEXTO);
        panel.add(lbTitulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(TemaMuebleria.BLANCO);
        formulario.setBorder(BorderFactory.createCompoundBorder(
                BorderLineCustom(),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 8, 9, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel lbNombre = new JLabel("Nombre:");
        lbNombre.setForeground(TemaMuebleria.TEXTO);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formulario.add(lbNombre, gbc);
        txtNombre = new JTextField();
        gbc.gridx = 1;
        formulario.add(txtNombre, gbc);

        JLabel lbCosto = new JLabel("Costo:");
        lbCosto.setForeground(TemaMuebleria.TEXTO);
        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(lbCosto, gbc);
        txtCosto = new JTextField();
        gbc.gridx = 1;
        formulario.add(txtCosto, gbc);

        JLabel lbVenta = new JLabel("Precio de venta:");
        lbVenta.setForeground(TemaMuebleria.TEXTO);
        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(lbVenta, gbc);
        txtVenta = new JTextField();
        gbc.gridx = 1;
        formulario.add(txtVenta, gbc);

        JLabel lbStock = new JLabel("Stock:");
        lbStock.setForeground(TemaMuebleria.TEXTO);
        gbc.gridx = 0;
        gbc.gridy++;
        formulario.add(lbStock, gbc);
        txtStock = new JTextField();
        gbc.gridx = 1;
        formulario.add(txtStock, gbc);

        JButton btnGuardar = new JButton("Guardar mueble");
        btnGuardar.addActionListener(e -> registrarNuevoMueble());
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        btnGuardar.setPreferredSize(new Dimension(180, 42));
        formulario.add(btnGuardar, gbc);
        panel.add(formulario, BorderLayout.CENTER);

        return panel;
    }

    private void registrarNuevoMueble() 
    {
        if (txtNombre == null || txtCosto == null || txtVenta == null || txtStock == null) 
        {
            JOptionPane.showMessageDialog(this, "El formulario aún no está listo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombre = txtNombre.getText().trim();
        String costo = txtCosto.getText().trim();
        String venta = txtVenta.getText().trim();
        String stock = txtStock.getText().trim();

        if (nombre.isEmpty() || costo.isEmpty() || venta.isEmpty() || stock.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try{
            double precioCosto = Double.parseDouble(costo);
            double precioVenta = Double.parseDouble(venta);
            int cantidadStock = Integer.parseInt(stock);

            if (precioCosto < 0 || precioVenta < 0 || cantidadStock < 0) 
            {
                JOptionPane.showMessageDialog(this, "Los valores no pueden ser negativos.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<String[]> inventarioActual = Manejo_Archivos.leerCSV("data/Inventarios.csv");
            int nuevoId = obtenerUltimoIdInventario(inventarioActual) + 1;
            String nuevaLinea = nuevoId + "," + nombre + "," + precioCosto + "," + precioVenta + "," + cantidadStock;

            Manejo_Archivos.escribirCSV("data/Inventarios.csv", nuevaLinea);
            DashboardFrame.refrescarDashboardAbierto();

            JOptionPane.showMessageDialog(this, "¡Mueble registrado y stock agregado con éxito!", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);

            txtNombre.setText("");
            txtCosto.setText("");
            txtVenta.setText("");
            txtStock.setText("");
        } 
        catch (NumberFormatException e) 
        {
            JOptionPane.showMessageDialog(this, "El costo, precio de venta y stock deben ser valores numéricos válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Borde para las tarjetas
    private javax.swing.border.Border BorderLineCustom() 
    {
        return BorderFactory.createLineBorder(new Color(220, 221, 225), 1);
    }
}
