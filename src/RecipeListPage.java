import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class RecipeListPage extends ListPage<Recipe> {
    private JCheckBox cal500Plus;
    private JCheckBox cal250to500;
    private JCheckBox calBelow250;
    private JCheckBox inStockBox;
    private JCheckBox notInStockBox;
    private Map<String, JCheckBox> tagBoxes; //for tag based filters
    private Inventory inventory;
    private Runnable onReturn; //for returning to main page
    private MealPlan mealPlan;
    private RecipeList recipesRef;

    public RecipeListPage(Runnable onReturn, RecipeList recipes, Inventory inventory) {
        super(recipes);
        this.inventory = inventory;
        this.onReturn = onReturn;
        this.mealPlan = null;
        this.recipesRef = recipes;
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String search = searchField.getText().trim();
                if (search.isEmpty()) {
                    updateTable(allItems);
                } else {
                    RecipeList results = ((RecipeList) allItems).searchByName(search);
                    updateTable(results);
                }
            }
        });
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openEditDialog(-1);
            }
        });
        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) openEditDialog(row);
            }
        });
        JButton returnBtn = (JButton)((JPanel)getComponent(1)).getComponent(1);
        returnBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onReturn.run();
            }
        });
        updateTable(allItems);
    }

    public void setMealPlan(MealPlan mealPlan) {
        this.mealPlan = mealPlan;
        updateTable(allItems);
    }

    @Override
    protected boolean hasDayDropdown() { return true; }

    @Override
    protected String[] getColumnNames() {
        // Add "Day" as the first column
        return new String[]{"Day", "Name", "Book", "Page", "Calories", "Protein", "Carbs", "Sugars", "Fats", "Tags", "Stock %"};
    }

    @Override
    protected void setupFilters(JPanel filtersPanel) {
        filtersPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        filtersPanel.add(new JLabel("Calories (pp)"));
        cal500Plus = new JCheckBox("500<");
        filtersPanel.add(cal500Plus);
        cal250to500 = new JCheckBox("250< x <500");
        filtersPanel.add(cal250to500);
        calBelow250 = new JCheckBox("<250");
        filtersPanel.add(calBelow250);
        filtersPanel.add(Box.createVerticalStrut(10));
        filtersPanel.add(new JLabel("Tags"));
        tagBoxes = new HashMap<>();
        Set<String> uniqueTags = new HashSet<>();
        for (Recipe r : allItems) {
            for (String tag : r.getTags()) {
                if (!tag.trim().isEmpty()) uniqueTags.add(tag.trim());
            }
        }
        BinarySearchTree tagTree = new BinarySearchTree();
        for (String tag : uniqueTags) tagTree.add(tag);
        java.util.List<String> sortedTags = tagTree.getAllValues();
        for (String tag : sortedTags) {
            JCheckBox cb = new JCheckBox(tag);
            tagBoxes.put(tag, cb);
            filtersPanel.add(cb);
        }
        filtersPanel.add(Box.createVerticalStrut(10));
        filtersPanel.add(new JLabel("Ingredients"));
        inStockBox = new JCheckBox("in stock");
        notInStockBox = new JCheckBox("not in stock");
        filtersPanel.add(inStockBox);
        filtersPanel.add(notInStockBox);
        filtersPanel.add(Box.createVerticalStrut(10));
        JButton applyBtn = new JButton("Apply Filters");
        filtersPanel.add(applyBtn);
        applyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RecipeList filtered = filterRecipes();
                updateTable(filtered);
            }
        });
    }

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
            // Find which day (if any) this recipe is assigned to
            String assignedDay = "";
            if (mealPlan != null) {
                for (String day : getAllDays()) {
                    Recipe assigned = mealPlan.getMeal(day);
                    if (assigned != null && assigned.getName().equals(r.getName()) && assigned.getBook().equals(r.getBook()) && assigned.getPage() == r.getPage()) {
                        assignedDay = day;
                        break;
                    }
                }
            }
            System.out.println("updateTable: " + r.getName() + " assignedDay=" + assignedDay);
            tableModel.addRow(new Object[]{assignedDay, r.getName(), r.getBook(), r.getPage(), r.getCalories(), r.getProtein(), r.getCarbs(), r.getSugars(), r.getFats(), String.join(", ", r.getTags()), percent + "%"});
        }
    }

    @Override
    protected String[] getAvailableDaysForRow(int row) {
        // Only days not already assigned to another recipe
        if (mealPlan == null) return new String[0];
        java.util.List<String> allDays = java.util.Arrays.asList(getAllDays());
        java.util.List<String> usedDays = mealPlan.getPlannedDays();
        String currentDay = (String) tableModel.getValueAt(row, 0);
        java.util.List<String> available = new java.util.ArrayList<>();
        if (currentDay != null && !currentDay.equals("") && allDays.contains(currentDay)) {
            available.add(currentDay); // Ensure assigned day is first and selected
        }
        for (String day : allDays) {
            if ((!usedDays.contains(day) || day.equals(currentDay)) && !available.contains(day)) {
                available.add(day);
            }
        }
        return available.toArray(new String[0]);
    }

    @Override
    protected void onDaySelected(int row, String day) {
        if (mealPlan == null) return;
        // Remove this recipe from any previous day
        for (String d : getAllDays()) {
            Recipe assigned = mealPlan.getMeal(d);
            if (assigned != null) {
                String name = (String) tableModel.getValueAt(row, 1);
                String book = (String) tableModel.getValueAt(row, 2);
                int page = Integer.parseInt(tableModel.getValueAt(row, 3).toString());
                if (assigned.getName().equals(name) && assigned.getBook().equals(book) && assigned.getPage() == page) {
                    mealPlan.clearDay(d);
                }
            }
        }
        if (day != null) {
            // Assign this recipe to the selected day
            String name = (String) tableModel.getValueAt(row, 1);
            String book = (String) tableModel.getValueAt(row, 2);
            int page = Integer.parseInt(tableModel.getValueAt(row, 3).toString());
            Recipe selectedRecipe = null;
            for (Recipe r : recipesRef) {
                if (r.getName().equals(name) && r.getBook().equals(book) && r.getPage() == page) {
                    selectedRecipe = r;
                    break;
                }
            }
            if (selectedRecipe != null) {
                mealPlan.addMeal(day, selectedRecipe);
            }
        }
        updateTable(allItems);
    }

    private String[] getAllDays() {
        return new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    }

    @Override
    protected void openEditDialog(int row) {
        Recipe recipe = (row == -1) ? new Recipe("", "", 0, new Ingredient[0], 0, 0, 0, 0, 0, new String[0]) : allItems.get(row);
        final Recipe recipeToDelete = recipe; // Store reference for deletion
        EditRecipeDialog dialog = new EditRecipeDialog((JFrame) SwingUtilities.getWindowAncestor(this), recipe, new Runnable() {
            public void run() {
                if (row == -1) {
                    recipesRef.add(recipe);
                }
                FileHandler.saveAllRecipes(recipesRef);
                updateTable(recipesRef);
            }
        }, new Runnable() {
            public void run() {
                if (row != -1 && recipeToDelete != null) {
                    // Always remove by matching name/book/page to ensure we find the right recipe
                    // even if the object reference doesn't match (e.g., from filtered view)
                    String nameToDelete = recipeToDelete.getName();
                    String bookToDelete = recipeToDelete.getBook();
                    int pageToDelete = recipeToDelete.getPage();
                    
                    boolean found = false;
                    for (int i = 0; i < recipesRef.size(); i++) {
                        Recipe r = recipesRef.get(i);
                        if (r.getName().equals(nameToDelete) && 
                            r.getBook().equals(bookToDelete) && 
                            r.getPage() == pageToDelete) {
                            recipesRef.remove(i);
                            found = true;
                            break;
                        }
                    }
                    
                    if (!found) {
                        JOptionPane.showMessageDialog(null, "Recipe not found in list. It may have already been deleted.", "Delete Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
                FileHandler.saveAllRecipes(recipesRef);
                // Update allItems to point to recipesRef and refresh table
                allItems = recipesRef;
                updateTable(recipesRef);
            }
        });
        dialog.setVisible(true);
    }

    private RecipeList filterRecipes() {
        java.util.List<Recipe> filtered = new java.util.ArrayList<>();
        Set<String> selectedTags = new HashSet<>();
        for (Map.Entry<String, JCheckBox> entry : tagBoxes.entrySet()) {
            if (entry.getValue().isSelected()) selectedTags.add(entry.getKey());
        }
        boolean filterInStock = inStockBox.isSelected();
        boolean filterNotInStock = notInStockBox.isSelected();
        for (Recipe r : allItems) {
            boolean matches = true;
            if (cal500Plus.isSelected() && r.getCalories() <= 500) matches = false;
            if (cal250to500.isSelected() && (r.getCalories() <= 250 || r.getCalories() >= 500)) matches = false;
            if (calBelow250.isSelected() && r.getCalories() >= 250) matches = false;
            if (!selectedTags.isEmpty()) {
                boolean hasAll = true;
                for (String tag : selectedTags) {
                    if (!hasTag(r, tag)) hasAll = false;
                }
                if (!hasAll) matches = false;
            }
            // Ingredient stock filtering
            if (filterInStock || filterNotInStock) {
                boolean allInStock = true;
                for (Ingredient ing : r.getIngredients()) {
                    double have = inventory.getQuantity(ing);
                    if (have < ing.getQuantity()) {
                        allInStock = false;
                        break;
                    }
                }
                if (filterInStock && !allInStock) matches = false;
                if (filterNotInStock && allInStock) matches = false;
            }
            if (matches) filtered.add(r);
        }
        RecipeList result = new RecipeList();
        for (Recipe r : filtered) result.add(r);
        return result;
    }

    private boolean hasTag(Recipe r, String tag) {
        for (String t : r.getTags()) {
            if (t.equalsIgnoreCase(tag)) return true;
        }
        return false;
    }

    private class EditRecipeDialog extends BaseEditDialog {
        private Recipe recipe;
        private Runnable onUpdateCallback;
        private Runnable onDeleteCallback;
        private JTextField nameField, bookField, pageField, calField, proteinField, carbField, sugarField, fatField, tagsField, ingsField;
        public EditRecipeDialog(JFrame parent, Recipe recipe, Runnable onUpdate, Runnable onDelete) {
            super(parent, "Edit Recipe");
            this.recipe = recipe;
            this.onUpdateCallback = onUpdate;
            this.onDeleteCallback = onDelete;
            fillFields();
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        String name = nameField.getText().trim();
                        String book = bookField.getText().trim();
                        String pageText = pageField.getText().trim();
                        String calText = calField.getText().trim();
                        String proteinText = proteinField.getText().trim();
                        String carbText = carbField.getText().trim();
                        String sugarText = sugarField.getText().trim();
                        String fatText = fatField.getText().trim();
                        String tagsText = tagsField.getText().trim();
                        String ingsText = ingsField.getText().trim();
                        
                        // Validate name
                        if (name.isEmpty()) {
                            JOptionPane.showMessageDialog(EditRecipeDialog.this, "Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Validate book
                        if (book.isEmpty()) {
                            JOptionPane.showMessageDialog(EditRecipeDialog.this, "Book cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Validate page (must be > 0)
                        if (pageText.isEmpty()) {
                            JOptionPane.showMessageDialog(EditRecipeDialog.this, "Page cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        int page = Integer.parseInt(pageText);
                        if (page <= 0) {
                            JOptionPane.showMessageDialog(EditRecipeDialog.this, "Page must be greater than zero.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Validate nutrition values (must be >= 0)
                        if (calText.isEmpty() || proteinText.isEmpty() || carbText.isEmpty() || sugarText.isEmpty() || fatText.isEmpty()) {
                            JOptionPane.showMessageDialog(EditRecipeDialog.this, "All nutrition values (calories, protein, carbs, sugars, fats) must be provided.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        int calories = Integer.parseInt(calText);
                        int protein = Integer.parseInt(proteinText);
                        int carbs = Integer.parseInt(carbText);
                        int sugars = Integer.parseInt(sugarText);
                        int fats = Integer.parseInt(fatText);
                        
                        if (calories <= 0 || protein <= 0 || carbs <= 0 || sugars <= 0 || fats <= 0) {
                            JOptionPane.showMessageDialog(EditRecipeDialog.this, "All nutrition values (calories, protein, carbs, sugars, fats) must be greater than zero.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Validate tags format (optional, but if provided must be valid)
                        String[] tags = tagsText.isEmpty() ? new String[0] : tagsText.split(",\\s*");
                        for (int i = 0; i < tags.length; i++) {
                            tags[i] = tags[i].trim();
                            if (tags[i].isEmpty()) {
                                JOptionPane.showMessageDialog(EditRecipeDialog.this, "Tags cannot contain empty values. Use comma-separated format: tag1, tag2, tag3", "Validation Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        
                        // Validate ingredients format
                        java.util.List<Ingredient> ingList = new java.util.ArrayList<>();
                        if (!ingsText.isEmpty()) {
                            String[] ingParts = ingsText.split(";");
                            for (String ingPart : ingParts) {
                                ingPart = ingPart.trim();
                                if (ingPart.isEmpty()) continue;
                                
                                String[] parts = ingPart.split(":");
                                if (parts.length != 3) {
                                    JOptionPane.showMessageDialog(EditRecipeDialog.this, "Invalid ingredient format. Use: name:quantity:unit\nExample: flour:500:g", "Validation Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                
                                String ingName = parts[0].trim();
                                String qtyText = parts[1].trim();
                                String unit = parts[2].trim();
                                
                                if (ingName.isEmpty() || unit.isEmpty()) {
                                    JOptionPane.showMessageDialog(EditRecipeDialog.this, "Ingredient name and unit cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                
                                if (qtyText.isEmpty()) {
                                    JOptionPane.showMessageDialog(EditRecipeDialog.this, "Ingredient quantity cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                
                                double qty = Double.parseDouble(qtyText);
                                if (qty <= 0) {
                                    JOptionPane.showMessageDialog(EditRecipeDialog.this, "Ingredient quantity must be greater than zero.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                
                                ingList.add(new Ingredient(ingName, qty, unit));
                            }
                        }
                        
                        recipe.setName(name);
                        recipe.setBook(book);
                        recipe.setPage(page);
                        recipe.setCalories(calories);
                        recipe.setProtein(protein);
                        recipe.setCarbs(carbs);
                        recipe.setSugars(sugars);
                        recipe.setFats(fats);
                        recipe.setTags(tags);
                        recipe.setIngredients(ingList.toArray(new Ingredient[0]));
                        onUpdateCallback.run();
                        dispose();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(EditRecipeDialog.this, "Invalid number format. Please check all numeric fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(EditRecipeDialog.this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        @Override
        protected void populateFields() { fillFields(); }
        private void fillFields() {
            nameField = new JTextField(recipe.getName());
            bookField = new JTextField(recipe.getBook());
            pageField = new JTextField(String.valueOf(recipe.getPage()));
            calField = new JTextField(String.valueOf(recipe.getCalories()));
            proteinField = new JTextField(String.valueOf(recipe.getProtein()));
            carbField = new JTextField(String.valueOf(recipe.getCarbs()));
            sugarField = new JTextField(String.valueOf(recipe.getSugars()));
            fatField = new JTextField(String.valueOf(recipe.getFats()));
            tagsField = new JTextField(String.join(", ", recipe.getTags()));
            StringBuilder ingStr = new StringBuilder();
            for (Ingredient ing : recipe.getIngredients()) {
                if (ingStr.length() > 0) ingStr.append("; ");
                ingStr.append(ing.getName()).append(":").append(ing.getQuantity()).append(":").append(ing.getUnit());
            }
            ingsField = new JTextField(ingStr.toString());
            fieldsPanel.add(new JLabel("Name:"));
            fieldsPanel.add(nameField);
            fieldsPanel.add(new JLabel("Book:"));
            fieldsPanel.add(bookField);
            fieldsPanel.add(new JLabel("Page:"));
            fieldsPanel.add(pageField);
            fieldsPanel.add(new JLabel("Calories:"));
            fieldsPanel.add(calField);
            fieldsPanel.add(new JLabel("Protein:"));
            fieldsPanel.add(proteinField);
            fieldsPanel.add(new JLabel("Carbs:"));
            fieldsPanel.add(carbField);
            fieldsPanel.add(new JLabel("Sugars:"));
            fieldsPanel.add(sugarField);
            fieldsPanel.add(new JLabel("Fats:"));
            fieldsPanel.add(fatField);
            fieldsPanel.add(new JLabel("Tags (comma separated):"));
            fieldsPanel.add(tagsField);
            fieldsPanel.add(new JLabel("Ingredients (name:qty:unit; ...):"));
            fieldsPanel.add(ingsField);
        }
        @Override
        protected void onSave() {
            // Save is handled by the custom saveButton listener above
        }
        @Override
        protected void onDelete() {
            if (onDeleteCallback == null) {
                JOptionPane.showMessageDialog(EditRecipeDialog.this, "Delete callback is not set.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(EditRecipeDialog.this, "Are you sure you want to delete this recipe?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    onDeleteCallback.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(EditRecipeDialog.this, "Error deleting recipe: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
                dispose();
            }
        }
    }
} 