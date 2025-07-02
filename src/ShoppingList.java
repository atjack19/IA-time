import java.util.HashMap;
import java.util.Map;

public class ShoppingList {
    private Map<Ingredient, Double> missingIngredients = new HashMap<>();
    
    public void generateList(MealPlan mealPlan, Inventory inventory) {
        missingIngredients.clear();
        
        for (Recipe recipe : mealPlan.getAllMeals()) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                double required = ingredient.getQuantity();
                double available = inventory.getQuantity(ingredient);
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