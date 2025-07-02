import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<Ingredient, Double> ingredients = new HashMap<>();
    
    public void addIngredient(Ingredient ingredient, double quantity) {
        ingredients.merge(ingredient, quantity, Double::sum);
    }
    
    public boolean removeIngredient(Ingredient ingredient, double quantity) {
        Double currentQty = ingredients.get(ingredient);
        if (currentQty == null || currentQty < quantity) {
            return false;
        }
        ingredients.put(ingredient, currentQty - quantity);
        return true;
    }
    
    public double getQuantity(Ingredient ingredient) {
        return ingredients.getOrDefault(ingredient, 0.0);
    }

    public Map<Ingredient, Double> getAllIngredients() {
        return new HashMap<>(ingredients);
    }
}