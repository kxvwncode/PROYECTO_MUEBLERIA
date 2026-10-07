package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.table.JTableHeader;

final class TemaMuebleria
{
    static final Color CAFE_OSCURO = new Color(74, 48, 34);
    static final Color CAFE = new Color(119, 78, 52);
    static final Color CAFE_CLARO = new Color(164, 119, 83);
    static final Color CREMA = new Color(247, 243, 236);
    static final Color BLANCO = new Color(255, 253, 250);
    static final Color TEXTO = new Color(58, 45, 36);
    static final Color BORDE = new Color(222, 210, 195);

    private TemaMuebleria()
    {
    }

    static void instalar()
    {
        UIManager.put("Panel.background", CREMA);
        UIManager.put("OptionPane.background", CREMA);
        UIManager.put("OptionPane.messageForeground", TEXTO);
        UIManager.put("TextField.background", BLANCO);
        UIManager.put("PasswordField.background", BLANCO);
        UIManager.put("ComboBox.background", BLANCO);
        UIManager.put("Table.background", BLANCO);
        UIManager.put("Table.foreground", TEXTO);
        UIManager.put("Table.selectionBackground", new Color(230, 215, 195));
        UIManager.put("Table.selectionForeground", TEXTO);
    }

    static void aplicar(JFrame frame)
    {
        instalar();
        frame.getContentPane().setBackground(CREMA);
        aplicarA(frame.getContentPane());
    }

    private static void aplicarA(Container container)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JTable)
            {
                JTable table = (JTable) component;
                table.setBackground(BLANCO);
                table.setForeground(TEXTO);
                table.setSelectionBackground(new Color(230, 215, 195));
                table.setSelectionForeground(TEXTO);
                table.setRowHeight(28);
                table.setGridColor(BORDE);
                table.setFillsViewportHeight(true);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                table.setToolTipText("");
                table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer()
                {
                    @Override
                    public Component getTableCellRendererComponent(JTable source, Object value,
                            boolean selected, boolean focused, int row, int column)
                    {
                        Component renderer = super.getTableCellRendererComponent(
                                source, value, selected, focused, row, column);
                        setToolTipText(value == null ? null : value.toString());
                        return renderer;
                    }
                });
                JTableHeader header = table.getTableHeader();
                header.setBackground(CAFE_OSCURO);
                header.setForeground(Color.WHITE);
                header.setFont(new Font("Segoe UI", Font.BOLD, 12));
                header.setReorderingAllowed(false);
            }
            else if (component instanceof AbstractButton)
            {
                AbstractButton button = (AbstractButton) component;
                if (!(button instanceof javax.swing.JRadioButton)
                        && !(button instanceof javax.swing.JCheckBox))
                {
                    button.setBackground(CAFE);
                    button.setForeground(Color.WHITE);
                    button.setFocusPainted(false);
                    button.setBorderPainted(false);
                    button.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                    button.setMargin(new java.awt.Insets(8, 14, 8, 14));
                    if (button.getText().toLowerCase().contains("procesar"))
                    {
                        button.setBackground(CAFE_CLARO);
                    }
                }
            }
            else if (component instanceof JTextField)
            {
                JTextField field = (JTextField) component;
                field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                field.setForeground(TEXTO);
                field.setBackground(BLANCO);
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDE),
                        BorderFactory.createEmptyBorder(5, 8, 5, 8)));
            }
            else if (component instanceof JComboBox)
            {
                JComboBox<?> combo = (JComboBox<?>) component;
                combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                combo.setForeground(TEXTO);
                combo.setBackground(BLANCO);
            }
            else if (component instanceof JScrollPane)
            {
                JScrollPane scroll = (JScrollPane) component;
                scroll.getViewport().setBackground(BLANCO);
                Border border = BorderFactory.createLineBorder(BORDE);
                scroll.setBorder(border);
            }

            if (component instanceof Container)
            {
                aplicarA((Container) component);
            }
        }
    }
}
