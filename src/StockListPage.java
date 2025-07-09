// StockListPage shows all stock items (ingredients and leftovers) in a list
// Extends ListPage<Object> to reuse the generic list layout and logic
import javax.swing.*;
import java.util.*;
import java.awt.GridLayout;

public class StockListPage extends ListPage<Object> {
    private Inventory inventory; // Inventory for ingredients
    private List<LeftoverMeal> leftoverMeals; // List of leftover meals
    private JComboBox<String> typeFilter; // Dropdown to filter by type
    private Runnable onReturn;

    // Constructor takes inventory and leftover meals
    public StockListPage(Runnable onReturn, Inventory inventory, List<LeftoverMeal> leftoverMeals) {
        super(collectAllStock(inventory, leftoverMeals)); // call base ListPage constructor
        this.inventory = inventory;
        this.leftoverMeals = leftoverMeals;
        this.onReturn = onReturn;
        // Add listener for type filter (handled in setupFilters)
        // Add listener for search, add, edit, and return
        searchField.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (searchTerm.isEmpty()) {
                updateTable(allItems);
            } else {
                List<Object> filtered = new ArrayList<>();
                for (Object item : allItems) {
                    if (item instanceof Ingredient) {
                        Ingredient ing = (Ingredient) item;
                        if (ing.getName().toLowerCase().contains(searchTerm)) filtered.add(ing);
                    } else if (item instanceof LeftoverMeal) {
                        LeftoverMeal meal = (LeftoverMeal) item;
                        if (meal.getName().toLowerCase().contains(searchTerm)) filtered.add(meal);
                    }
                }
                updateTable(filtered);
            }
        });
        addButton.addActionListener(e -> openEditDialog(-1)); // -1 for new item
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) openEditDialog(row);
        });
        JButton returnButton = (JButton)((JPanel)getComponent(1)).getComponent(1);
        returnButton.addActionListener(e -> onReturn.run());
        // Now that all fields are set, update the table
        updateTable(allItems);
    }

    // Helper to collect all stock items into a single list
    private static List<Object> collectAllStock(Inventory inventory, List<LeftoverMeal> leftoverMeals) {
        List<Object> all = new ArrayList<>();
        all.addAll(inventory.getAllIngredients().keySet());
        all.addAll(leftoverMeals);
        return all;
    }

    // Provide column names for the stock table
    @Override
    protected String[] getColumnNames() {
        // Show tags for leftovers, blank for ingredients
        return new String[]{"Name", "Type", "Unit", "Quantity", "Tags"};
    }

    // Set up filters for stock (type: ingredient/leftover)
    @Override
    protected void setupFilters(JPanel filtersPanel) {
        filtersPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        filtersPanel.add(new JLabel("Type"));
        typeFilter = new JComboBox<>(new String[]{"All", "Ingredient", "Leftover"});
        filtersPanel.add(typeFilter);
        JButton applyFiltersButton = new JButton("Apply Filters");
        filtersPanel.add(applyFiltersButton);
        applyFiltersButton.addActionListener(e -> {
            String selected = (String) typeFilter.getSelectedItem();
            List<Object> filtered = new ArrayList<>();
            for (Object item : allItems) {
                if (selected.equals("All")) filtered.add(item);
                else if (selected.equals("Ingredient") && item instanceof Ingredient) filtered.add(item);
                else if (selected.equals("Leftover") && item instanceof LeftoverMeal) filtered.add(item);
            }
            updateTable(filtered);
        });
    }

    // Update the table to show the given stock items
    @Override
    protected void updateTable(List<Object> items) {
        tableModel.setRowCount(0);
        Map<Ingredient, Double> ingredientMap = inventory.getAllIngredients();
        for (Object item : items) {
            if (item instanceof Ingredient) {
                Ingredient ing = (Ingredient) item;
                // Ingredient: show name, type, unit, quantity, blank for tags
                tableModel.addRow(new Object[]{
                    ing.getName(),
                    "Ingredient",
                    ing.getUnit(),
                    ingredientMap.getOrDefault(ing, 0.0),
                    "" // blank for tags
                });
            } else if (item instanceof LeftoverMeal) {
                LeftoverMeal meal = (LeftoverMeal) item;
                // LeftoverMeal: show name, type, blank for unit, portions as quantity, tags from Recipe
                tableModel.addRow(new Object[]{
                    meal.getName(),
                    "Leftover",
                    "",
                    meal.getPortions(),
                    String.join(", ", meal.getTags())
                });
            }
        }
    }

    // Open the edit dialog for a stock item (row or new)
    @Override
    protected void openEditDialog(int row) {
        // Get the name from the selected row in the table
        String name = table.getValueAt(row, 0).toString();
        Object item = null;
        for (Object obj : allItems) {
            if (obj instanceof Ingredient && ((Ingredient)obj).getName().equals(name)) {
                item = obj;
                break;
            } else if (obj instanceof LeftoverMeal && ((LeftoverMeal)obj).getName().equals(name)) {
                item = obj;
                break;
            }
        }
        if (item == null) return;
        if (item instanceof Ingredient) {
            Ingredient ing = (Ingredient) item;
            double qty = inventory.getAllIngredients().getOrDefault(ing, 0.0);
            EditIngredientDialog dialog = new EditIngredientDialog((JFrame) SwingUtilities.getWindowAncestor(this), ing, (newIng, newQty) -> {
                inventory.removeIngredient(ing, qty); // Remove old
                inventory.addIngredient(newIng, newQty); // Add new
                allItems.set(allItems.indexOf(ing), newIng);
                updateTable(allItems);
            });
            dialog.setVisible(true);
        } else if (item instanceof LeftoverMeal) {
            LeftoverMeal meal = (LeftoverMeal) item;
            EditLeftoverDialog dialog = new EditLeftoverDialog((JFrame) SwingUtilities.getWindowAncestor(this), meal, newMeal -> {
                leftoverMeals.set(leftoverMeals.indexOf(meal), newMeal);
                allItems.set(allItems.indexOf(meal), newMeal);
                updateTable(allItems);
            });
            dialog.setVisible(true);
        }
    }

    // Dialog for editing/adding an Ingredient
    private class EditIngredientDialog extends JDialog {
        private JTextField nameField, unitField, qtyField;
        private JButton saveButton, cancelButton;
        public EditIngredientDialog(JFrame parent, Ingredient ing, IngredientSaveListener onSave) {
            super(parent, (ing == null ? "Add Ingredient" : "Edit Ingredient"), true);
            setLayout(new GridLayout(4, 2));
            add(new JLabel("Name:"));
            nameField = new JTextField(ing == null ? "" : ing.getName());
            add(nameField);
            add(new JLabel("Unit:"));
            unitField = new JTextField(ing == null ? "" : ing.getUnit());
            add(unitField);
            add(new JLabel("Quantity:"));
            qtyField = new JTextField(ing == null ? "" : String.valueOf(ing.getQuantity()));
            add(qtyField);
            saveButton = new JButton("Save");
            cancelButton = new JButton("Cancel");
            add(saveButton);
            add(cancelButton);
            setSize(300, 200);
            setLocationRelativeTo(parent);
            saveButton.addActionListener(e -> {
                try {
                    String name = nameField.getText().trim();
                    String unit = unitField.getText().trim();
                    double qty = Double.parseDouble(qtyField.getText().trim());
                    Ingredient newIng = new Ingredient(name, qty, unit);
                    onSave.onSave(newIng, qty);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            cancelButton.addActionListener(e -> dispose());
        }
    }
    // Listener for saving Ingredient
    private interface IngredientSaveListener {
        void onSave(Ingredient ing, double qty);
    }

    // Dialog for editing/adding a LeftoverMeal
    private class EditLeftoverDialog extends JDialog {
        private JTextField nameField, portionsField;
        private JButton saveButton, cancelButton;
        public EditLeftoverDialog(JFrame parent, LeftoverMeal meal, LeftoverSaveListener onSave) {
            super(parent, (meal == null ? "Add Leftover" : "Edit Leftover"), true);
            setLayout(new GridLayout(3, 2));
            add(new JLabel("Name:"));
            nameField = new JTextField(meal == null ? "" : meal.getName());
            add(nameField);
            add(new JLabel("Portions:"));
            portionsField = new JTextField(meal == null ? "" : String.valueOf(meal.getPortions()));
            add(portionsField);
            saveButton = new JButton("Save");
            cancelButton = new JButton("Cancel");
            add(saveButton);
            add(cancelButton);
            setSize(250, 150);
            setLocationRelativeTo(parent);
            saveButton.addActionListener(e -> {
                try {
                    String name = nameField.getText().trim();
                    int portions = Integer.parseInt(portionsField.getText().trim());
                    LeftoverMeal newMeal = new LeftoverMeal(name, portions, 0, 0, 0, 0, 0, new String[0]);
                    onSave.onSave(newMeal);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            cancelButton.addActionListener(e -> dispose());
        }
    }
    // Listener for saving LeftoverMeal
    private interface LeftoverSaveListener {
        void onSave(LeftoverMeal meal);
    }
} 