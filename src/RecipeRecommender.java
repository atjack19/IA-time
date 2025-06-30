import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeRecommender {
//    public List<Recipe> recommendByInventory(Inventory inventory, RecipeList recipes) {
//        return recipes.stream()
//            .filter(recipe -> hasRequiredIngredients(recipe, inventory))
//            .collect(Collectors.toList());
//    }
    
    public List<Recipe> recommendBySeason(String season, RecipeList recipes) {
        return recipes.stream()
            .filter(recipe -> isSeasonalRecipe(recipe, season))
            .collect(Collectors.toList());
    }

//    private boolean hasRequiredIngredients(Recipe recipe, Inventory inventory) {
//        for (Map.Entry<Ingredient, Double> entry : recipe.ingredientQuantities.entrySet()) {
//            Ingredient ingredient = entry.getKey();
//            double requiredAmount = entry.getValue();
//            if (inventory.getQuantity(ingredient) < requiredAmount) {
//                return false;
//            }
//        }
//        return true;
//    }
    
    private boolean isSeasonalRecipe(Recipe recipe, String season) {
        // Implement seasonal logic based on ingredients and tags
        return Arrays.stream(recipe.getTags())
            .anyMatch(tag -> tag.toLowerCase().contains(season.toLowerCase()));
    }
}