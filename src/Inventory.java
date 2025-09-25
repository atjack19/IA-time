import java.util.HashMap;
import java.util.Map;

/**
 * Manages pantry inventory - tracks what ingredients you have and how much.
 * Uses Ingredient as key (by name+unit) with quantity as value.
 */
public class Inventory {
    private Map<Ingredient, Double> ingredients = new HashMap<>();
    
    // Add to existing quantity or create new entry
    public void addIngredient(Ingredient ingredient, double quantity) {
        ingredients.merge(ingredient, quantity, Double::sum);
    }
    
    // Remove quantity if available, return false if not enough
    public boolean removeIngredient(Ingredient ingredient, double quantity) {
        Double currentQty = ingredients.get(ingredient);
        if (currentQty == null || currentQty < quantity) {
            return false;
        }
        double newQty = currentQty - quantity;
        if (newQty <= 0) {
            ingredients.remove(ingredient);
        } else {
            ingredients.put(ingredient, newQty);
        }
        return true;
    }
    
    // Get current quantity (0 if not in inventory)
    public double getQuantity(Ingredient ingredient) {
        return ingredients.getOrDefault(ingredient, 0.0);
    }

    public Map<Ingredient, Double> getAllIngredients() {
        return new HashMap<>(ingredients);
    }

    // Remove ingredient entry entirely
    public void deleteIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
    }
}