// this class is the recipe list page for the GUI
// it shows all recipes and allows filtering, searching, and editing

import javax.swing.*;
import javax.swing.table.DefaultTableModel; // Table model for JTable
import java.awt.*; // Layouts and colors
import java.awt.event.FocusAdapter; // For focus events
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter; // For mouse events
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections; // Utility for sorting
import java.util.Set; // Set interface
import java.util.HashSet; // Set with unique items
import java.util.Map;
import java.util.HashMap;

public class RecipeListPage extends ListPage<Recipe> {
    private JCheckBox calories500Plus;
    private JCheckBox calories250to500;
    private JCheckBox caloriesBelow250;
    private Map<String, JCheckBox> tagCheckboxes;
    private Inventory inventory;
    private Runnable onReturn;

    public RecipeListPage(Runnable onReturn, RecipeList recipes, Inventory inventory) {
        super(recipes); // call base ListPage constructor
        this.inventory = inventory;
        this.onReturn = onReturn;
        // Add listeners for search, add, edit, and return
        searchField.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                updateTable(allItems);
            } else {
                RecipeList results = ((RecipeList) allItems).searchByName(searchTerm);
                updateTable(results);
            }
        });
        addButton.addActionListener(e -> openEditDialog(-1)); // -1 for new recipe
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) openEditDialog(row);
        });
        // Return button
        JButton returnButton = (JButton)((JPanel)getComponent(1)).getComponent(1);
        returnButton.addActionListener(e -> onReturn.run());
        // Now that inventory and all fields are set, update the table
        updateTable(allItems);
    }

    // Provide column names for the recipe table
    @Override
    protected String[] getColumnNames() {
        return new String[]{"Name", "Book", "Page", "Calories", "Protein", "Carbs", "Sugars", "Fats", "Tags", "Stock %"};
    }

    // Set up filters for recipes (calories, tags, etc.)
    @Override
    protected void setupFilters(JPanel filtersPanel) {
        filtersPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        filtersPanel.add(new JLabel("Calories (pp)"));
        calories500Plus = new JCheckBox("500<");
        filtersPanel.add(calories500Plus);
        calories250to500 = new JCheckBox("250< x <500");
        filtersPanel.add(calories250to500);
        caloriesBelow250 = new JCheckBox("<250");
        filtersPanel.add(caloriesBelow250);
        filtersPanel.add(Box.createVerticalStrut(10));
        filtersPanel.add(new JLabel("Tags"));
        tagCheckboxes = new HashMap<>();
        Set<String> uniqueTags = new HashSet<>();
        for (Recipe r : allItems) {
            for (String tag : r.getTags()) {
                if (!tag.trim().isEmpty()) uniqueTags.add(tag.trim());
            }
        }
        BinarySearchTree tagTree = new BinarySearchTree();
        for (String tag : uniqueTags) tagTree.add(tag);
        List<String> sortedTags = tagTree.getAllValues();
        for (String tag : sortedTags) {
            JCheckBox cb = new JCheckBox(tag);
            tagCheckboxes.put(tag, cb);
            filtersPanel.add(cb);
        }
        filtersPanel.add(Box.createVerticalStrut(10));
        filtersPanel.add(new JLabel("Ingredients"));
        filtersPanel.add(new JCheckBox("in stock"));
        filtersPanel.add(new JCheckBox("not in stock"));
        filtersPanel.add(Box.createVerticalStrut(10));
        JButton applyFiltersButton = new JButton("Apply Filters");
        filtersPanel.add(applyFiltersButton);
        applyFiltersButton.addActionListener(e -> {
            RecipeList filtered = filterRecipes();
            updateTable(filtered);
        });
    }

    // Update the table to show the given recipes
    @Override
    protected void updateTable(List<Recipe> recipes) {
        tableModel.setRowCount(0);
        for (Recipe r : recipes) {
            int inStock = 0;
            Ingredient[] ings = r.getIngredients();
            for (Ingredient ing : ings) {
                double have = inventory.getQuantity(ing);
                if (have >= ing.getQuantity()) inStock++;
            }
            int percent = (ings.length == 0) ? 100 : (int) Math.round(100.0 * inStock / ings.length);
            tableModel.addRow(new Object[]{
                r.getName(), r.getBook(), r.getPage(), r.getCalories(), r.getProtein(), r.getCarbs(), r.getSugars(), r.getFats(), String.join(", ", r.getTags()), percent + "%"
            });
        }
    }

    // Open the edit dialog for a recipe (row or new)
    @Override
    protected void openEditDialog(int row) {
        Recipe recipe = (row == -1) ? new Recipe("", "", 0, new Ingredient[0], 0, 0, 0, 0, 0, new String[0]) : allItems.get(row);
        EditRecipeDialog dialog = new EditRecipeDialog((JFrame) SwingUtilities.getWindowAncestor(this), recipe, () -> {
            if (row == -1) allItems.add(recipe);
            FileHandler.saveAllRecipes((RecipeList) allItems);
            updateTable(allItems);
        }, () -> {
            if (row != -1) allItems.remove(recipe);
            FileHandler.saveAllRecipes((RecipeList) allItems);
            updateTable(allItems);
        });
        dialog.setVisible(true);
    }

    // Filter recipes based on selected filters
    private RecipeList filterRecipes() {
        List<Recipe> filtered = new ArrayList<>();
        Set<String> selectedTags = new HashSet<>();
        for (Map.Entry<String, JCheckBox> entry : tagCheckboxes.entrySet()) {
            if (entry.getValue().isSelected()) selectedTags.add(entry.getKey());
        }
        for (Recipe r : allItems) {
            boolean matches = true;
            if (calories500Plus.isSelected() && r.getCalories() <= 500) matches = false;
            if (calories250to500.isSelected() && (r.getCalories() <= 250 || r.getCalories() >= 500)) matches = false;
            if (caloriesBelow250.isSelected() && r.getCalories() >= 250) matches = false;
            if (!selectedTags.isEmpty()) {
                boolean hasAll = true;
                for (String tag : selectedTags) {
                    if (!containsTag(r, tag)) hasAll = false;
                }
                if (!hasAll) matches = false;
            }
            if (matches) filtered.add(r);
        }
        RecipeList result = new RecipeList();
        for (Recipe r : filtered) result.add(r);
        return result;
    }

    // Check if a recipe has a given tag
    private boolean containsTag(Recipe r, String tag) {
        for (String t : r.getTags()) {
            if (t.equalsIgnoreCase(tag)) return true;
        }
        return false;
    }

    // Inner class for editing recipes (same as before)
    private class EditRecipeDialog extends BaseEditDialog {
        private Recipe recipe;
        private JTextField nameField, bookField, pageField, caloriesField, proteinField, carbsField, sugarsField, fatsField, tagsField, ingredientsField;
        public EditRecipeDialog(JFrame parent, Recipe recipe, Runnable onUpdate, Runnable onDelete) {
            super(parent, "Edit Recipe");
            this.recipe = recipe;
            populateFields();
            saveButton.addActionListener(e -> {
                try {
                    recipe.setName(nameField.getText().trim());
                    recipe.setBook(bookField.getText().trim());
                    recipe.setPage(Integer.parseInt(pageField.getText().trim()));
                    recipe.setCalories(Integer.parseInt(caloriesField.getText().trim()));
                    recipe.setProtein(Integer.parseInt(proteinField.getText().trim()));
                    recipe.setCarbs(Integer.parseInt(carbsField.getText().trim()));
                    recipe.setSugars(Integer.parseInt(sugarsField.getText().trim()));
                    recipe.setFats(Integer.parseInt(fatsField.getText().trim()));
                    recipe.setTags(tagsField.getText().split(", ?"));
                    String[] ingParts = ingredientsField.getText().split("; ?");
                    Ingredient[] ings = new Ingredient[ingParts.length];
                    for (int i = 0; i < ingParts.length; i++) {
                        String[] parts = ingParts[i].split(":");
                        if (parts.length == 3) {
                            ings[i] = new Ingredient(parts[0], Double.parseDouble(parts[1]), parts[2]);
                        }
                    }
                    recipe.setIngredients(ings);
                    onUpdate.run();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this recipe?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    onDelete.run();
                    dispose();
                }
            });
        }
        @Override
        protected void populateFields() {
            nameField = new JTextField(recipe.getName());
            bookField = new JTextField(recipe.getBook());
            pageField = new JTextField(String.valueOf(recipe.getPage()));
            caloriesField = new JTextField(String.valueOf(recipe.getCalories()));
            proteinField = new JTextField(String.valueOf(recipe.getProtein()));
            carbsField = new JTextField(String.valueOf(recipe.getCarbs()));
            sugarsField = new JTextField(String.valueOf(recipe.getSugars()));
            fatsField = new JTextField(String.valueOf(recipe.getFats()));
            tagsField = new JTextField(String.join(", ", recipe.getTags()));
            StringBuilder ingStr = new StringBuilder();
            for (Ingredient ing : recipe.getIngredients()) {
                if (ingStr.length() > 0) ingStr.append("; ");
                ingStr.append(ing.getName()).append(":").append(ing.getQuantity()).append(":").append(ing.getUnit());
            }
            ingredientsField = new JTextField(ingStr.toString());
            fieldsPanel.add(new JLabel("Name:"));
            fieldsPanel.add(nameField);
            fieldsPanel.add(new JLabel("Book:"));
            fieldsPanel.add(bookField);
            fieldsPanel.add(new JLabel("Page:"));
            fieldsPanel.add(pageField);
            fieldsPanel.add(new JLabel("Calories:"));
            fieldsPanel.add(caloriesField);
            fieldsPanel.add(new JLabel("Protein:"));
            fieldsPanel.add(proteinField);
            fieldsPanel.add(new JLabel("Carbs:"));
            fieldsPanel.add(carbsField);
            fieldsPanel.add(new JLabel("Sugars:"));
            fieldsPanel.add(sugarsField);
            fieldsPanel.add(new JLabel("Fats:"));
            fieldsPanel.add(fatsField);
            fieldsPanel.add(new JLabel("Tags (comma separated):"));
            fieldsPanel.add(tagsField);
            fieldsPanel.add(new JLabel("Ingredients (name:qty:unit; ...):"));
            fieldsPanel.add(ingredientsField);
        }
        @Override
        protected void onSave() {}
        @Override
        protected void onDelete() {}
    }
} 