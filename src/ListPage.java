import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public abstract class ListPage<T> extends JPanel {
    protected DefaultTableModel tableModel;
    protected JTable table;
    protected JTextField searchField;
    protected JPanel filtersPanel;
    protected JButton addBtn, editBtn, deleteBtn;
    protected List<T> allItems;

    public ListPage(List<T> items) {
        this.allItems = items;
        setLayout(new BorderLayout());

        filtersPanel = new JPanel();
        filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.Y_AXIS));
        setupFilters(filtersPanel);
        add(filtersPanel, BorderLayout.WEST);

        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        String placeholder = "Search...";
        Color placeholderColor = Color.GRAY;
        Color inputColor = Color.BLACK;
        searchField.setForeground(placeholderColor);
        searchField.setText(placeholder);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(inputColor);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(placeholderColor);
                    searchField.setText(placeholder);
                }
            }
        });
        topPanel.add(searchField, BorderLayout.CENTER);
        JButton returnBtn = new JButton("Return");
        topPanel.add(returnBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(getColumnNames(), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0 && hasDayDropdownForRow(row)) return true;
                return false;
            }
        };
        table = new JTable(tableModel);
        if (hasDayDropdown()) {
            TableColumn dayColumn = table.getColumnModel().getColumn(0);
            // Always-visible dropdown renderer
            dayColumn.setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    if (!hasDayDropdownForRow(row)) {
                        JLabel label = new JLabel("");
                        label.setOpaque(true);
                        label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                        return label;
                    }
                    JComboBox<String> comboBox = new JComboBox<>();
                    String[] days = getAvailableDaysForRow(row);
                    String currentValue = value != null ? value.toString() : "";
                    comboBox.addItem(""); // Blank/None option
                    if (!currentValue.equals("") && !contains(days, currentValue)) {
                        comboBox.addItem(currentValue);
                    }
                    for (String day : days) {
                        if (!day.equals(currentValue)) {
                            comboBox.addItem(day);
                        }
                    }
                    comboBox.setSelectedItem(currentValue);
                    if (comboBox.getSelectedIndex() == -1) {
                        comboBox.setSelectedIndex(0);
                    }
                    // Debug output
                    System.out.print("Renderer row=" + row + " currentValue='" + currentValue + "' items=[");
                    for (int i = 0; i < comboBox.getItemCount(); i++) {
                        System.out.print("'" + comboBox.getItemAt(i) + "'");
                        if (i < comboBox.getItemCount() - 1) System.out.print(", ");
                    }
                    System.out.println("] selectedIndex=" + comboBox.getSelectedIndex());
                    comboBox.setEnabled(false); // Not editable in renderer
                    return comboBox;
                }
            });
            // Editor with blank/None option
            dayColumn.setCellEditor(new DefaultCellEditor(new JComboBox<String>()) {
                @Override
                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                    if (!hasDayDropdownForRow(row)) {
                        return new JLabel("");
                    }
                    JComboBox<String> comboBox = new JComboBox<>();
                    comboBox.addItem(""); // Blank/None option
                    String[] days = getAvailableDaysForRow(row);
                    String currentValue = value != null ? value.toString() : "";
                    if (!currentValue.equals("") && !contains(days, currentValue)) {
                        comboBox.addItem(currentValue);
                    }
                    for (String day : days) {
                        if (!day.equals(currentValue)) {
                            comboBox.addItem(day);
                        }
                    }
                    comboBox.setSelectedItem(currentValue);
                    if (comboBox.getSelectedIndex() == -1) {
                        comboBox.setSelectedIndex(0);
                    }
                    comboBox.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            String selectedDay = (String) comboBox.getSelectedItem();
                            if (selectedDay == null || selectedDay.equals("")) {
                                onDaySelected(row, null); // Unassign
                            } else {
                                onDaySelected(row, selectedDay);
                            }
                        }
                    });
                    return comboBox;
                }
            });
        }
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    openEditDialog(table.getSelectedRow());
                }
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        addBtn = new JButton("+");
        editBtn = new JButton("Edit");
        deleteBtn = new JButton("Delete");
        bottomPanel.add(addBtn, BorderLayout.EAST);
        bottomPanel.add(editBtn, BorderLayout.CENTER);
        bottomPanel.add(deleteBtn, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // By default, no day dropdown. Subclasses can override.
    protected boolean hasDayDropdown() { return false; }
    // By default, use hasDayDropdown for all rows. Subclasses can override for per-row logic.
    protected boolean hasDayDropdownForRow(int row) { return hasDayDropdown(); }
    // Subclasses must provide available days for a given row if dropdown is enabled.
    protected String[] getAvailableDaysForRow(int row) { return new String[0]; }
    // Subclasses can override to handle day selection.
    protected void onDaySelected(int row, String day) {}

    protected abstract String[] getColumnNames();
    protected abstract void setupFilters(JPanel filtersPanel);
    protected abstract void updateTable(List<T> items);
    protected abstract void openEditDialog(int row);

    // Helper to check if an array contains a string
    private boolean contains(String[] arr, String val) {
        for (String s : arr) {
            if (s.equals(val)) return true;
        }
        return false;
    }
} 