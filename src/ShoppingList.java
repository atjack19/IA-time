import java.util.HashMap;
import java.util.Map;

/**
 * Generates shopping list by comparing meal plan ingredients against inventory.
 * Aggregates shortages across all planned meals.
 */
public class ShoppingList {
    private Map<Ingredient, Double> missingIngredients = new HashMap<>();
    
    // Calculate what ingredients are needed for the meal plan
    public void generateList(MealPlan mealPlan, Inventory inventory) {
        missingIngredients.clear();
        
        // Check each recipe in the meal plan
        for (Recipe recipe : mealPlan.getAllMeals()) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                double required = ingredient.getQuantity();
                double available = inventory.getQuantity(ingredient);
                
                // If we need more than we have, add to shopping list
                if (required > available) {
                    double needed = required - available;
                    missingIngredients.merge(ingredient, needed, Double::sum);
                }
            }
        }
    }

    public Map<Ingredient, Double> getMissingIngredients() {
        return new HashMap<>(missingIngredients);
    }

    public void displayShoppingList() {
        if (missingIngredients.isEmpty()) {
            System.out.println("No ingredients needed! You have everything in your inventory.");
            return;
        }
        
        System.out.println("\n=== SHOPPING LIST ===");
        for (Map.Entry<Ingredient, Double> entry : missingIngredients.entrySet()) {
            Ingredient ingredient = entry.getKey();
            double quantity = entry.getValue();
            System.out.printf("- %s: %.1f %s%n", 
                ingredient.getName(), quantity, ingredient.getUnit());
        }
    }

    public void clear() {
        missingIngredients.clear();
    }
}