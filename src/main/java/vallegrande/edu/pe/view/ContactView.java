package vallegrande.edu.pe.view;

import vallegrande.edu.pe.controller.ContactController;
import vallegrande.edu.pe.model.Contact;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Objects;

/**
 * ContactView – Versión con diseño moderno (solo Vista)
 * - Tema claro/oscuro
 * - Tipografías modernas
 * - Botones redondeados con hover y sombra
 * - Paneles con bordes suaves y márgenes
 * - Toasts (éxito / error / info)
 * - Iconos embebidos (base64) Agregar/Eliminar
 * Mantiene la lógica y llamadas al Controller.
 */
public class ContactView extends JFrame {
    private final ContactController controller;

    private DefaultTableModel tableModel;
    private JTable table;

    // Tema
    private Theme theme = Theme.LIGHT;
    private final JToggleButton toggleTema = new JToggleButton("Modo oscuro");

    // Botones
    private final RoundedButton addBtn = new RoundedButton("Agregar");
    private final RoundedButton deleteBtn = new RoundedButton("Eliminar");

    // ===== Paleta
    private enum Theme {
        LIGHT(new Color(0x0B6BFF), new Color(0x2576FF),
                Color.WHITE, new Color(0x0E1217),
                new Color(245, 247, 250), new Color(230, 235, 241),
                new Color(0x1F2937), new Color(0x6B7280),
                new Color(0xE5E7EB)),
        DARK(new Color(0x3B82F6), new Color(0x60A5FA),
                new Color(0x0B0F14), new Color(0xF9FAFB),
                new Color(0x121720), new Color(0x1C2430),
                new Color(0xD1D5DB), new Color(0x9CA3AF),
                new Color(0x2A3442));

        final Color brand, brandHover, bg, fg, panelBg, panelBorder, textPrimary, textSecondary, tableStripe;
        Theme(Color brand, Color brandHover, Color bg, Color fg, Color panelBg, Color panelBorder,
              Color textPrimary, Color textSecondary, Color tableStripe) {
            this.brand = brand; this.brandHover = brandHover; this.bg = bg; this.fg = fg; this.panelBg = panelBg;
            this.panelBorder = panelBorder; this.textPrimary = textPrimary; this.textSecondary = textSecondary; this.tableStripe = tableStripe;
        }
    }

    public ContactView(ContactController controller) {
        super("Agenda MVC Swing - Vallegrande");
        this.controller = controller;
        initUI();
        loadContacts();
        SwingUtilities.invokeLater(() -> Toast.info(this, "👋 Bienvenido: doble clic para ver detalles"));
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa

        installFonts();

        // Layout principal
        JPanel content = new JPanel(new BorderLayout(14, 14));
        content.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(content);

        // Header con título + toggle tema
        JPanel header = new ShadowPanel();
        header.setLayout(new BorderLayout(12, 8));
        header.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel title = new JLabel("Agenda de Contactos");
        title.setFont(new Font(getPrimaryFontName(), Font.BOLD, 22));

        JLabel subtitle = new JLabel("Gestiona tus contactos de forma rápida y bonita.");
        subtitle.setFont(new Font(getPrimaryFontName(), Font.PLAIN, 13));
        subtitle.setForeground(theme.textSecondary);

        JPanel titleBox = new JPanel(new BorderLayout(0, 2));
        titleBox.setOpaque(false);
        titleBox.add(title, BorderLayout.NORTH);
        titleBox.add(subtitle, BorderLayout.SOUTH);

        styleToggleTema(toggleTema);
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightHeader.setOpaque(false);
        rightHeader.add(toggleTema);

        header.add(titleBox, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        // Centro: tabla dentro de panel estilizado
        JPanel center = new ShadowPanel();
        center.setLayout(new BorderLayout(10, 10));
        center.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Modelo y tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Email", "Teléfono"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        center.add(scrollPane, BorderLayout.CENTER);

        // Footer con botones (derecha)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        buttonsPanel.setOpaque(false);

        decoratePrimary(addBtn, loadIcon("add", "➕"));      // ICONOS EMBEBIDOS
        decorateDanger(deleteBtn, loadIcon("delete", "🗑")); // ICONOS EMBEBIDOS
        buttonsPanel.add(addBtn);
        buttonsPanel.add(deleteBtn);

        center.add(buttonsPanel, BorderLayout.SOUTH);

        content.add(center, BorderLayout.CENTER);

        // Temas
        applyTheme();

        toggleTema.addActionListener(e -> {
            theme = toggleTema.isSelected() ? Theme.DARK : Theme.LIGHT;
            toggleTema.setText(toggleTema.isSelected() ? "Modo claro" : "Modo oscuro");
            applyTheme();
            SwingUtilities.invokeLater(() ->
                    Toast.info(this, "Tema " + (toggleTema.isSelected() ? "oscuro" : "claro") + " activado")
            );
        });

        // Eventos botones (misma lógica que ya tenías)
        addBtn.addActionListener(e -> showAddContactDialog());
        deleteBtn.addActionListener(e -> deleteSelectedContact());

        // UX: doble clic fila
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    Toast.info(ContactView.this, "Contacto seleccionado");
                }
            }
        });
    }

    // ===== Funcionalidad existente (sin cambios en lógica de negocio) =====
    private void loadContacts() {
        tableModel.setRowCount(0);
        List<Contact> contacts = controller.list();
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{c.id(), c.name(), c.email(), c.phone()});
        }
    }

    private void showAddContactDialog() {
        AddContactDialog dialog = new AddContactDialog(this, controller);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadContacts();
            Toast.success(this, "Contacto agregado");
        }
    }

    private void deleteSelectedContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Toast.error(this, "Seleccione un contacto para eliminar");
            return;
        }
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este contacto?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadContacts();
            Toast.success(this, "Contacto eliminado");
        }
    }

    // ================== Estilos y utilidades ==================
    private void applyTheme() {
        getContentPane().setBackground(theme.bg);
        UIManager.put("Table.gridColor", theme.panelBorder);
        UIManager.put("Table.foreground", theme.textPrimary);
        UIManager.put("TextField.caretForeground", theme.textPrimary);
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }

    private void styleTable(JTable tbl) {
        tbl.setFont(new Font(getPrimaryFontName(), Font.PLAIN, 16));
        tbl.setRowHeight(30);
        tbl.setFillsViewportHeight(true);
        tbl.setShowHorizontalLines(false);
        tbl.setShowVerticalLines(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader th = tbl.getTableHeader();
        th.setPreferredSize(new Dimension(th.getWidth(), 36));
        th.setFont(new Font(getPrimaryFontName(), Font.BOLD, 16));
        th.setBorder(new MatteBorder(0, 0, 1, 0, theme.panelBorder));

        // Zebra + selección brand
        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                comp.setFont(new Font(getPrimaryFontName(), Font.PLAIN, 15));
                if (!sel) {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : theme.tableStripe);
                    comp.setForeground(theme.textPrimary);
                } else {
                    comp.setBackground(theme.brand);
                    comp.setForeground(Color.WHITE);
                }
                setBorder(new EmptyBorder(8, 10, 8, 10));
                return comp;
            }
        });
    }

    private void styleToggleTema(JToggleButton t) {
        t.setFont(new Font(getPrimaryFontName(), Font.PLAIN, 13));
        t.setBorder(new CompoundBorder(new RoundedLineBorder(theme.panelBorder, 14), new EmptyBorder(6, 12, 6, 12)));
        t.setFocusPainted(false);
        t.setContentAreaFilled(false);
        t.setOpaque(true);
        t.setBackground(theme.panelBg);
        t.setForeground(theme.textPrimary);
        t.addItemListener(e -> t.setBackground(theme.panelBg));
    }

    private void decoratePrimary(RoundedButton b, Icon icon) {
        baseRounded(b, icon);
        b.setBackground(theme.brand);
        b.setForeground(Color.WHITE);
        b.setHoverColor(theme.brandHover);
    }

    private void decorateDanger(RoundedButton b, Icon icon) {
        baseRounded(b, icon);
        Color danger = new Color(0xEF4444);
        Color dangerHover = new Color(0xF87171);
        b.setBackground(danger);
        b.setForeground(Color.WHITE);
        b.setHoverColor(dangerHover);
    }

    private void baseRounded(RoundedButton b, Icon icon) {
        b.setIcon(icon);
        b.setFont(new Font(getPrimaryFontName(), Font.BOLD, 16));
        b.setBorder(null);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setShadow(true);
        b.setMargin(new Insets(10, 16, 10, 16));
    }

    /** Carga iconos embebidos (base64). Fallback: emoji como icono. */
    private Icon loadIcon(String name, String fallbackEmoji) {
        try {
            String base64 = switch (name) {
                case "add" -> "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABjUlEQVQ4jZXSsU4CURQG4O+NMSQlIuYEBISQwgW+BBQElzjCiZUsFPlAzmBBozAQKRA7OEaHUN4hIh6YQh0hGf0DWy2x7+X5Zqba7q1/v93+o0QdCFjgAAJb4RzQk5dK8rTGeIw0ofQBdUdfoRkYQmmQ6Yj8FLm1gP0EFv0MLotb6i63RJReUO0saUZr9Rojxh5qjvIalXzX0TL2z0RjEIvHu0XscsP5itKXRCaOiZpdRVxz0R6eQG+0bE+hhGlmwDbpUHXNOBofpFd2XgVSPM93qBd4A3u/9h3jZJwOqOQxO1k1C6Eo5MMsQtfIF1ziMxnxSeMo5QtIDa3gk1E/kzGgnyEDUkpIAvPYl74gAfDquuOa1L6p3jF34GgFiDP9zxE9NnUOsl3D7gqQAAAABJRU5ErkJggg==";
                case "delete" -> "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABKklEQVQ4jZXSsUoDQRSG4V8hLQ3FJ6AQ1BQFQa2NoUuIqKOqgLqBUqVfIDoU3sDPwAQVFBFGLlK6Q2wgXoCd3rxg5S3Yj+/5fdjD3GL0wsFmBI48QY8DGOrAawhsI0jGo3WncfkP8K+AvkRV2fB1cUAWCHnQdRu4AhrcPcDeoAFjGQj3EuG5AkR++8YZ/Jhv0uEbqD4y8AFH2QOKsV91hH4FrgKfnMNcAtGEeZjIXeYWfX6G7cNTo3dpR0BMKZ1bgx2/QFgXpaI8mjYvbgzRnks0mU5FZUKqTYeLKCCaHMTT6fksV9yxzZ4PYMSxjZbPNvc2VROqg4Hh7QXonhmfgvIC0lCBvIAAAAASUVORK5CYII=";
                default -> null;
            };
            if (base64 != null) {
                byte[] data = java.util.Base64.getDecoder().decode(base64);
                Image img = new ImageIcon(data).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception ignored) {}
        return new TextIcon(fallbackEmoji);
    }

    private String getPrimaryFontName() {
        for (String f : new String[]{"Inter", "Segoe UI", "Roboto", "SF Pro Text", "Noto Sans"}) {
            if (isFontAvailable(f)) return f;
        }
        return "SansSerif";
    }
    private boolean isFontAvailable(String name) {
        for (Font f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts())
            if (Objects.equals(f.getName(), name)) return true;
        return false;
    }
    private void installFonts() { /* opcional: registrar fuentes custom en /fonts */ }

    // ================== Clases de soporte ==================
    /** Botón redondeado con hover y sombra ligera */
    static class RoundedButton extends JButton {
        private Color hoverColor = new Color(0,0,0,30);
        private boolean hovering = false;
        private boolean shadow = false;
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
            });
        }
        public void setHoverColor(Color c) { this.hoverColor = c; }
        public void setShadow(boolean s) { this.shadow = s; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = 18;
            if (shadow) {
                g2.setColor(new Color(0, 0, 0, 25));
                g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-6, arc+4, arc+4);
            }
            g2.setColor(getBackground() != null ? getBackground() : new Color(0,0,0,0));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            if (hovering) {
                g2.setColor(hoverColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            }
            // icono + texto
            FontMetrics fm = g2.getFontMetrics(getFont());
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            int x = 16;
            if (getIcon() != null) {
                int ih = getIcon().getIconHeight();
                int iw = getIcon().getIconWidth();
                int iy = (getHeight() - ih) / 2;
                getIcon().paintIcon(this, g2, x, iy);
                x += iw + 8;
            }
            g2.setColor(getForeground());
            g2.setFont(getFont());
            g2.drawString(getText(), x, textY);
            g2.dispose();
        }
    }

    /** Panel con sombra y borde suave */
    static class ShadowPanel extends JPanel {
        public ShadowPanel() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = 16;
            g2.setColor(new Color(0, 0, 0, 28));
            g2.fillRoundRect(6, 8, getWidth()-12, getHeight()-14, arc+4, arc+4);
            Container top = getTopLevelAncestor();
            Color panelBg = (top instanceof ContactView) ? ((ContactView) top).theme.panelBg : Color.WHITE;
            Color border = (top instanceof ContactView) ? ((ContactView) top).theme.panelBorder : new Color(230,235,241);
            g2.setColor(panelBg);
            g2.fillRoundRect(0, 2, getWidth()-6, getHeight()-8, arc, arc);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 2, getWidth()-6, getHeight()-8, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
        @Override public boolean isOpaque() { return false; }
    }

    /** Borde redondeado para inputs/toggles */
    static class RoundedLineBorder extends LineBorder {
        private final int radius;
        public RoundedLineBorder(Color color, int radius) { super(color, 1, true); this.radius = radius; }
        @Override public boolean isBorderOpaque() { return false; }
        @Override public Insets getBorderInsets(Component c) { return new Insets(8, 10, 8, 10); }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(lineColor);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
    }

    /** Toast simple (info/éxito/error) */
    static class Toast {
        public static void info(Component parent, String msg) { show(parent, msg, new Color(0x2563EB), new Color(0xDBEAFE)); }
        public static void success(Component parent, String msg) { show(parent, msg, new Color(0x059669), new Color(0xD1FAE5)); }
        public static void error(Component parent, String msg) { show(parent, msg, new Color(0xDC2626), new Color(0xFEE2E2)); }
        private static void show(Component parent, String msg, Color accent, Color bg) {
            Window w = SwingUtilities.getWindowAncestor(parent);
            if (w == null) w = JOptionPane.getRootFrame();
            JWindow toast = new JWindow(w);
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new CompoundBorder(new RoundedLineBorder(new Color(0,0,0,40), 14),
                    new EmptyBorder(10, 14, 10, 14)));
            panel.setBackground(bg);
            JLabel dot = new JLabel("● "); dot.setForeground(accent);
            JLabel text = new JLabel(msg);
            text.setFont(new Font("SansSerif", Font.BOLD, 13));
            text.setForeground(new Color(0x111827));
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            row.setOpaque(false); row.add(dot); row.add(text);
            panel.add(row, BorderLayout.CENTER);
            toast.setContentPane(panel); toast.pack();
            Rectangle b = w.getBounds();
            toast.setLocation(b.x + b.width - toast.getWidth() - 24, b.y + b.height - toast.getHeight() - 24);
            Timer timer = new Timer(16, null);
            final long start = System.currentTimeMillis(); final int duration = 2200;
            timer.addActionListener(e -> {
                long t = System.currentTimeMillis() - start;
                if (t >= duration) { toast.dispose(); ((Timer) e.getSource()).stop(); }
                else { float p = Math.min(1f, t / 250f); toast.setOpacity(Math.min(0.95f, p)); }
            });
            toast.setOpacity(0f); toast.setVisible(true); timer.start();
        }
    }

    /** Icono de texto (fallback con emoji) */
    static class TextIcon implements Icon {
        private final String text; public TextIcon(String text) { this.text = text; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.drawString(text, x, y + getIconHeight() - 4); g2.dispose();
        }
        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }
    }
}