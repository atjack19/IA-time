import javax.swing.*;
import java.util.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StockListPage extends ListPage<Object> {
    private Inventory inventory;
    private List<LeftoverMeal> leftoverMeals;
    private JComboBox<String> typeBox;
    private Runnable onReturn;
    private MealPlan mealPlan;
    private List<LeftoverMeal> leftoversRef;

    public StockListPage(Runnable onReturn, Inventory inventory, List<LeftoverMeal> leftoverMeals) {
        super(getAllStock(inventory, leftoverMeals));
        this.inventory = inventory;
        this.leftoverMeals = leftoverMeals;
        this.onReturn = onReturn;
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String search = searchField.getText().trim().toLowerCase();
                if (search.isEmpty()) {
                    updateTable(allItems);
                } else {
                    List<Object> filtered = new ArrayList<>();
                    for (Object item : allItems) {
                        if (item instanceof Ingredient) {
                            Ingredient ing = (Ingredient) item;
                            if (ing.getName().toLowerCase().contains(search)) filtered.add(ing);
                        } else if (item instanceof LeftoverMeal) {
                            LeftoverMeal meal = (LeftoverMeal) item;
                            if (meal.getName().toLowerCase().contains(search)) filtered.add(meal);
                        }
                    }
                    updateTable(filtered);
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
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) return;
                String name = String.valueOf(table.getValueAt(row, 1));
                String type = String.valueOf(table.getValueAt(row, 2));
                int confirm = JOptionPane.showConfirmDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(StockListPage.this),
                        "Delete '" + name + "'?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (confirm != JOptionPane.YES_OPTION) return;
                if ("Ingredient".equals(type)) {
                    String unit = String.valueOf(table.getValueAt(row, 3));
                    Ingredient target = null;
                    for (Ingredient ing : inventory.getAllIngredients().keySet()) {
                        if (ing.getName().equals(name) && ing.getUnit().equals(unit)) {
                            target = ing;
                            break;
                        }
                    }
                    if (target != null) {
                        // Remove entirely from inventory and UI list
                        inventory.deleteIngredient(target);
                        for (Iterator<Object> it = allItems.iterator(); it.hasNext();) {
                            Object obj = it.next();
                            if (obj instanceof Ingredient) {
                                Ingredient ing = (Ingredient) obj;
                                if (ing.getName().equals(name) && ing.getUnit().equals(unit)) {
                                    it.remove();
                                    break;
                                }
                            }
                        }
                        FileHandler.saveInventory(inventory);
                        updateTable(allItems);
                    }
                } else if ("Leftover".equals(type)) {
                    LeftoverMeal target = null;
                    for (LeftoverMeal m : leftoverMeals) {
                        if (m.getName().equals(name)) { target = m; break; }
                    }
                    if (target != null) {
                        leftoverMeals.remove(target);
                        for (Iterator<Object> it = allItems.iterator(); it.hasNext();) {
                            Object obj = it.next();
                            if (obj instanceof LeftoverMeal && ((LeftoverMeal)obj).getName().equals(name)) {
                                it.remove();
                                break;
                            }
                        }
                        FileHandler.saveLeftoverMeals(leftoverMeals);
                        updateTable(allItems);
                    }
                }
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
    }

    @Override
    protected boolean hasDayDropdown() { return true; }

    @Override
    protected boolean hasDayDropdownForRow(int row) {
        Object type = tableModel.getValueAt(row, 2);
        return "Leftover".equals(type);
    }

    private static List<Object> getAllStock(Inventory inventory, List<LeftoverMeal> leftoverMeals) {
        List<Object> all = new ArrayList<>();
        all.addAll(inventory.getAllIngredients().keySet());
        all.addAll(leftoverMeals);
        return all;
    }

    @Override
    protected String[] getColumnNames() {
        // Add "Day" as the first column
        return new String[]{"Day", "Name", "Type", "Unit", "Quantity", "Tags"};
    }

    @Override
    protected void setupFilters(JPanel filtersPanel) {
        filtersPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        filtersPanel.add(new JLabel("Type"));
        typeBox = new JComboBox<>(new String[]{"All", "Ingredient", "Leftover"});
        filtersPanel.add(typeBox);
        // Only add leftover-related checkboxes/filters here if you want them
        // For now, no ingredient checkboxes are added
        JButton applyBtn = new JButton("Apply Filters");
        filtersPanel.add(applyBtn);
        applyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) typeBox.getSelectedItem();
                List<Object> filtered = new ArrayList<>();
                for (Object item : allItems) {
                    if (selected.equals("All")) filtered.add(item);
                    else if (selected.equals("Ingredient") && item instanceof Ingredient) filtered.add(item);
                    else if (selected.equals("Leftover") && item instanceof LeftoverMeal) filtered.add(item);
                }
                updateTable(filtered);
            }
        });
    }

    @Override
    protected void updateTable(List<Object> items) {
        tableModel.setRowCount(0);
        Map<Ingredient, Double> ingredientMap = inventory.getAllIngredients();
        for (Object item : items) {
            if (item instanceof Ingredient) {
                Ingredient ing = (Ingredient) item;
                tableModel.addRow(new Object[]{"", ing.getName(), "Ingredient", ing.getUnit(), ingredientMap.getOrDefault(ing, 0.0), ""});
            } else if (item instanceof LeftoverMeal) {
                LeftoverMeal meal = (LeftoverMeal) item;
                // Find which day (if any) this leftover is assigned to
                String assignedDay = "";
                if (mealPlan != null) {
                    for (String day : getAllDays()) {
                        Recipe assigned = mealPlan.getMeal(day);
                        if (assigned instanceof LeftoverMeal && assigned.getName().equals(meal.getName())) {
                            assignedDay = day;
                            break;
                        }
                    }
                }
                tableModel.addRow(new Object[]{assignedDay, meal.getName(), "Leftover", "", meal.getPortions(), String.join(", ", meal.getTags())});
            }
        }
    }

    @Override
    protected String[] getAvailableDaysForRow(int row) {
        if (mealPlan == null) return new String[0];
        Object type = tableModel.getValueAt(row, 2);
        if (!"Leftover".equals(type)) return new String[0];
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
        Object type = tableModel.getValueAt(row, 2);
        if (!"Leftover".equals(type)) return;
        // Remove this leftover from any previous day
        for (String d : getAllDays()) {
            Recipe assigned = mealPlan.getMeal(d);
            if (assigned instanceof LeftoverMeal) {
                String name = (String) tableModel.getValueAt(row, 1);
                if (assigned.getName().equals(name)) {
                    mealPlan.clearDay(d);
                }
            }
        }
        if (day != null) {
            // Assign this leftover to the selected day
            String name = (String) tableModel.getValueAt(row, 1);
            LeftoverMeal selectedLeftover = null;
            for (Object obj : allItems) {
                if (obj instanceof LeftoverMeal) {
                    LeftoverMeal meal = (LeftoverMeal) obj;
                    if (meal.getName().equals(name)) {
                        selectedLeftover = meal;
                        break;
                    }
                }
            }
            if (selectedLeftover != null) {
                mealPlan.addMeal(day, selectedLeftover);
            }
        }
        updateTable(allItems);
    }

    private String[] getAllDays() {
        return new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    }

        @Override
        protected void openEditDialog(int row) {
        // Handle adding new stock items
        if (row == -1) {
            Object[] options = new Object[]{"Ingredient", "Leftover", "Cancel"};
            int choice = JOptionPane.showOptionDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "What would you like to add?",
                    "Add Stock",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            if (choice == 0) { // Ingredient
                EditIngredientDialog dialog = new EditIngredientDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(this),
                        null,
                        0.0,
                        new IngredientSaveListener() {
                            public void onSave(Ingredient ing, double qty) {
                                inventory.addIngredient(ing, qty);
                                // Rebuild stock list from inventory to avoid duplicate entries
                                allItems = getAllStock(inventory, leftoverMeals);
                                FileHandler.saveInventory(inventory);
                                updateTable(allItems);
                            }
                        });
                dialog.setVisible(true);
            } else if (choice == 1) { // Leftover
                EditLeftoverDialog dialog = new EditLeftoverDialog((JFrame) SwingUtilities.getWindowAncestor(this), null, new LeftoverSaveListener() {
                    public void onSave(LeftoverMeal meal) {
                        leftoverMeals.add(meal);
                        allItems.add(meal);
                        FileHandler.saveLeftoverMeals(leftoverMeals);
                        updateTable(allItems);
                    }
                });
                dialog.setVisible(true);
            }
            return;
        }

        String name = table.getValueAt(row, 1).toString(); // Changed to get name from column 1
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
            double currentQty = inventory.getAllIngredients().getOrDefault(ing, 0.0);
            EditIngredientDialog dialog = new EditIngredientDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    ing,
                    currentQty,
                    new IngredientSaveListener() {
                        public void onSave(Ingredient newIng, double newQty) {
                            // Remove the old ingredient entry completely, then add the edited one
                            inventory.deleteIngredient(ing);
                            inventory.addIngredient(newIng, newQty);
                            allItems.set(allItems.indexOf(ing), newIng);
                            FileHandler.saveInventory(inventory);
                            updateTable(allItems);
                        }
                    });
            dialog.setVisible(true);
        } else if (item instanceof LeftoverMeal) {
            LeftoverMeal meal = (LeftoverMeal) item;
            EditLeftoverDialog dialog = new EditLeftoverDialog((JFrame) SwingUtilities.getWindowAncestor(this), meal, new LeftoverSaveListener() {
                public void onSave(LeftoverMeal meal) {
                    leftoverMeals.set(leftoverMeals.indexOf(meal), meal);
                    allItems.set(allItems.indexOf(meal), meal);
                    FileHandler.saveLeftoverMeals(leftoverMeals);
                    updateTable(allItems);
                }
            });
            dialog.setVisible(true);
        }
    }

    private class EditIngredientDialog extends JDialog {
        private JTextField nameField, unitField, qtyField;
        private JButton saveBtn, cancelBtn;
        public EditIngredientDialog(JFrame parent, Ingredient ing, double currentQty, IngredientSaveListener onSave) {
            super(parent, (ing == null ? "Add Ingredient" : "Edit Ingredient"), true);
            setLayout(new GridLayout(4, 2));
            add(new JLabel("Name:"));
            nameField = new JTextField(ing == null ? "" : ing.getName());
            add(nameField);
            add(new JLabel("Unit:"));
            unitField = new JTextField(ing == null ? "" : ing.getUnit());
            add(unitField);
            add(new JLabel("Quantity:"));
            qtyField = new JTextField(ing == null ? "" : String.valueOf(currentQty));
            add(qtyField);
            saveBtn = new JButton("Save");
            cancelBtn = new JButton("Cancel");
            add(saveBtn);
            add(cancelBtn);
            setSize(300, 200);
            setLocationRelativeTo(parent);
            saveBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        String name = nameField.getText().trim();
                        String unit = unitField.getText().trim();
                        String qtyText = qtyField.getText().trim();
                        
                        // Validate name
                        if (name.isEmpty()) {
                            JOptionPane.showMessageDialog(EditIngredientDialog.this, "Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Validate unit
                        if (unit.isEmpty()) {
                            JOptionPane.showMessageDialog(EditIngredientDialog.this, "Unit cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Validate quantity
                        if (qtyText.isEmpty()) {
                            JOptionPane.showMessageDialog(EditIngredientDialog.this, "Quantity cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        double qty = Double.parseDouble(qtyText);
                        if (qty <= 0) {
                            JOptionPane.showMessageDialog(EditIngredientDialog.this, "Quantity must be greater than zero.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        Ingredient newIng = new Ingredient(name, qty, unit);
                        onSave.onSave(newIng, qty);
                        dispose();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(EditIngredientDialog.this, "Quantity must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(EditIngredientDialog.this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            cancelBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
        }
    }
    private interface IngredientSaveListener {
        void onSave(Ingredient ing, double qty);
    }

    private class EditLeftoverDialog extends JDialog {
        private JTextField nameField, portionsField, tagsField;
        private JButton saveBtn, cancelBtn;
        public EditLeftoverDialog(JFrame parent, LeftoverMeal meal, LeftoverSaveListener onSave) {
            super(parent, (meal == null ? "Add Leftover" : "Edit Leftover"), true);
            setLayout(new GridLayout(4, 2));
            add(new JLabel("Name:"));
            nameField = new JTextField(meal == null ? "" : meal.getName());
            add(nameField);
            add(new JLabel("Portions:"));
            portionsField = new JTextField(meal == null ? "" : String.valueOf(meal.getPortions()));
            add(portionsField);
            add(new JLabel("Tags (comma separated):"));
            tagsField = new JTextField(meal == null ? "" : String.join(", ", meal.getTags()));
            add(tagsField);
            saveBtn = new JButton("Save");
            cancelBtn = new JButton("Cancel");
            add(saveBtn);
            add(cancelBtn);
            setSize(350, 200);
            setLocationRelativeTo(parent);
            saveBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        String name = nameField.getText().trim();
                        String portionsText = portionsField.getText().trim();
                        String tagsText = tagsField.getText().trim();
                        
                        // Validate name
                        if (name.isEmpty()) {
                            JOptionPane.showMessageDialog(EditLeftoverDialog.this, "Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Validate portions
                        if (portionsText.isEmpty()) {
                            JOptionPane.showMessageDialog(EditLeftoverDialog.this, "Portions cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        int portions = Integer.parseInt(portionsText);
                        if (portions <= 0) {
                            JOptionPane.showMessageDialog(EditLeftoverDialog.this, "Portions must be greater than zero.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Validate tags format (optional, but if provided must be valid)
                        String[] tags = tagsText.isEmpty() ? new String[0] : tagsText.split(",\\s*");
                        for (int i = 0; i < tags.length; i++) {
                            tags[i] = tags[i].trim();
                            if (tags[i].isEmpty()) {
                                JOptionPane.showMessageDialog(EditLeftoverDialog.this, "Tags cannot contain empty values. Use comma-separated format: tag1, tag2, tag3", "Validation Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }

                        if (meal == null) {
                            LeftoverMeal newMeal = new LeftoverMeal(name, portions, 0, 0, 0, 0, 0, tags);
                            onSave.onSave(newMeal);
                        } else {
                            meal.setName(name);
                            meal.setPortions(portions);
                            meal.setTags(tags);
                            onSave.onSave(meal);
                        }
                        dispose();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(EditLeftoverDialog.this, "Portions must be a valid integer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(EditLeftoverDialog.this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            cancelBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
        }
    }
    private interface LeftoverSaveListener {
        void onSave(LeftoverMeal meal);
    }
} 