import java.util.HashMap;
import java.util.Map;

public class ShoppingList {
    private Map<Ingredient, Double> missingIngredients = new HashMap<>();
    
    public void generateList(MealPlan mealPlan, Inventory inventory) {
        for (Recipe recipe : mealPlan.getAllMeals()) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                double required = recipe.getRequiredQuantity(ingredient);
                double available = inventory.getQuantity(ingredient);
                if (required > available) {
                    missingIngredients.merge(ingredient, 
                        required - available, Double::sum);
                }
            }
        }
    }
}