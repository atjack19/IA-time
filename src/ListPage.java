// Abstract base class for list-based pages (e.g., recipes, stock, shopping list)
// T is the type of item in the list (e.g., Recipe, Ingredient)
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public abstract class ListPage<T> extends JPanel {
    protected DefaultTableModel tableModel; // Table model for the list
    protected JTable table; // Table to display items
    protected JTextField searchField; // Search bar
    protected JPanel filtersPanel; // Panel for filters
    protected JButton addButton, editButton, deleteButton; // Action buttons
    protected List<T> allItems; // All items to display

    // Constructor sets up the layout and components
    public ListPage(List<T> items) {
        this.allItems = items;
        setLayout(new BorderLayout());

        // Set up filters panel (left side)
        filtersPanel = new JPanel();
        filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.Y_AXIS));
        setupFilters(filtersPanel); // Subclass provides filters
        add(filtersPanel, BorderLayout.WEST);

        // Set up top bar with search
        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        // Placeholder logic for search bar
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
        JButton returnButton = new JButton("Return");
        topPanel.add(returnButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Set up table
        tableModel = new DefaultTableModel(getColumnNames(), 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        // Double-click to edit row
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    openEditDialog(table.getSelectedRow());
                }
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Set up bottom bar with add/edit/delete
        JPanel bottomPanel = new JPanel(new BorderLayout());
        addButton = new JButton("+");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        bottomPanel.add(addButton, BorderLayout.EAST);
        bottomPanel.add(editButton, BorderLayout.CENTER);
        bottomPanel.add(deleteButton, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Subclasses should call updateTable(allItems) after their own fields are initialized

        // Listeners for search, add, edit, delete can be added in subclass
    }

    // Subclass must provide column names for the table
    protected abstract String[] getColumnNames();
    // Subclass must set up filters in the filtersPanel
    protected abstract void setupFilters(JPanel filtersPanel);
    // Subclass must update the table with the given items
    protected abstract void updateTable(List<T> items);
    // Subclass must open the edit dialog for the selected row
    protected abstract void openEditDialog(int row);
} 