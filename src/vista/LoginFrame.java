package vista;

import control.Manejo_Archivos;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame 
{

    private JComboBox<String> cmbRol;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    public LoginFrame() 
    {
        super("Mueblería Suchil | Iniciar sesión");
        TemaMuebleria.instalar();
        setSize(480, 620);
        setMinimumSize(new Dimension(420, 560));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(TemaMuebleria.CREMA);

        JPanel panelMarca = new JPanel(new GridBagLayout());
        panelMarca.setBackground(TemaMuebleria.CAFE_OSCURO);
        panelMarca.setPreferredSize(new Dimension(480, 175));
        GridBagConstraints marcaGbc = new GridBagConstraints();
        marcaGbc.gridx = 0;
        marcaGbc.gridy = 0;
        marcaGbc.insets = new Insets(8, 12, 4, 12);

        JLabel lbMarca = new JLabel("SUCHIL");
        lbMarca.setForeground(new Color(250, 237, 217));
        lbMarca.setFont(new Font("Serif", Font.BOLD, 38));
        panelMarca.add(lbMarca, marcaGbc);

        marcaGbc.gridy++;
        JLabel lbSubtitulo = new JLabel("MUEBLERÍA  ·  GESTIÓN DE VENTAS");
        lbSubtitulo.setForeground(new Color(222, 200, 175));
        lbSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelMarca.add(lbSubtitulo, marcaGbc);
        add(panelMarca, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(TemaMuebleria.BLANCO);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaMuebleria.BORDE),
                BorderFactory.createEmptyBorder(22, 28, 24, 28)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(7, 0, 7, 0);

        JLabel lbBienvenida = new JLabel("Bienvenido");
        lbBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 23));
        lbBienvenida.setForeground(TemaMuebleria.TEXTO);
        panelFormulario.add(lbBienvenida, gbc);

        gbc.gridy++;
        JLabel lbAyuda = new JLabel("Ingresa tus datos para continuar");
        lbAyuda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbAyuda.setForeground(TemaMuebleria.CAFE_CLARO);
        panelFormulario.add(lbAyuda, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel lbRol = new JLabel("Rol de usuario");
        lbRol.setForeground(TemaMuebleria.TEXTO);
        panelFormulario.add(lbRol, gbc);

        String[] roles = {"ADMINISTRADOR", "VENDEDOR"};
        cmbRol = new JComboBox<>(roles);
        gbc.gridy++;
        gbc.gridwidth = 2;
        panelFormulario.add(cmbRol, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel lbUsuario = new JLabel("Usuario");
        lbUsuario.setForeground(TemaMuebleria.TEXTO);
        panelFormulario.add(lbUsuario, gbc);
        txtUsuario = new JTextField();
        gbc.gridy++;
        gbc.gridwidth = 2;
        panelFormulario.add(txtUsuario, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel lbPassword = new JLabel("Contraseña");
        lbPassword.setForeground(TemaMuebleria.TEXTO);
        panelFormulario.add(lbPassword, gbc);
        txtPassword = new JPasswordField();
        gbc.gridy++;
        gbc.gridwidth = 2;
        panelFormulario.add(txtPassword, gbc);

        btnIngresar = new JButton("Iniciar sesión");
        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 4, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnIngresar.setPreferredSize(new Dimension(0, 44));
        panelFormulario.add(btnIngresar, gbc);

        JPanel panelCentro = new JPanel(new GridBagLayout());
        panelCentro.setOpaque(false);
        GridBagConstraints centroGbc = new GridBagConstraints();
        centroGbc.fill = GridBagConstraints.HORIZONTAL;
        centroGbc.weightx = 1;
        centroGbc.insets = new Insets(25, 24, 25, 24);
        panelCentro.add(panelFormulario, centroGbc);
        add(panelCentro, BorderLayout.CENTER);

        JLabel lbPie = new JLabel("Mueblería Suchil  ·  Sistema de punto de venta", SwingConstants.CENTER);
        lbPie.setForeground(TemaMuebleria.CAFE_CLARO);
        lbPie.setBorder(BorderFactory.createEmptyBorder(0, 8, 16, 8));
        add(lbPie, BorderLayout.SOUTH);

        btnIngresar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                validarCredenciales();
            }
        });
        getRootPane().setDefaultButton(btnIngresar);
        TemaMuebleria.aplicar(this);
    }

    private void validarCredenciales() 
    {
        String rolSeleccionado = cmbRol.getSelectedItem().toString();
        String usuarioIngresado = txtUsuario.getText().trim();
        String passwordIngresado = new String(txtPassword.getPassword()).trim();

        if (usuarioIngresado.isEmpty() || passwordIngresado.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Por favor, llene todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Leemos el archivo CSV de usuarios
        List<String[]> listaUsuarios = Manejo_Archivos.leerCSV("data/Usuarios.csv");

        boolean encontrado = false;

        for (int i = 1; i < listaUsuarios.size(); i++) 
        {
            String[] datos = listaUsuarios.get(i);
            
            if (datos.length >= 3) 
            {
                String usuarioArchivo = datos[0].trim();
                String passwordArchivo = datos[1].trim();
                String rolArchivo = datos[2].trim();

                //Validacion del usuario, la contraseña Y el rol seleccionado
                if (usuarioArchivo.equalsIgnoreCase(usuarioIngresado) 
                        && passwordArchivo.equals(passwordIngresado) 
                        && rolArchivo.equalsIgnoreCase(rolSeleccionado)) 
                {
                    encontrado = true;
                    break;
                }
            }
        }

        if (encontrado) 
        {
            JOptionPane.showMessageDialog(this, "¡Bienvenido! Acceso concedido como: " + rolSeleccionado, "Acceso Exitoso", JOptionPane.INFORMATION_MESSAGE);
            
            //Redirección según el rol seleccionado
            if (rolSeleccionado.equalsIgnoreCase("ADMINISTRADOR")) 
            {
                System.out.println("Abriendo panel de Dueño...");
                new DashboardFrame().setVisible(true);
            } 
            else if (rolSeleccionado.equalsIgnoreCase("VENDEDOR")) 
            {
                System.out.println("Abriendo panel de Vendedor...");
                new PuntoVentaFrame().setVisible(true);
            }
            
            this.dispose(); //Cierra la ventana de login
        } 
        else 
        {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas o el rol no coincide con este usuario.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
