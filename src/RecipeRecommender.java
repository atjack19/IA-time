import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeRecommender {
    public List<Recipe> recommendByInventory(Inventory inventory, RecipeList recipes) {
        return recipes.stream()
            .filter(recipe -> hasRequiredIngredients(recipe, inventory))
            .collect(Collectors.toList());
    }
    
    public List<Recipe> recommendBySeason(String season, RecipeList recipes) {
        return recipes.stream()
            .filter(recipe -> isSeasonalRecipe(recipe, season))
            .collect(Collectors.toList());
    }

    private boolean hasRequiredIngredients(Recipe recipe, Inventory inventory) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            double required = ingredient.getQuantity();
            double available = inventory.getQuantity(ingredient);
            
            // Debug: Print ingredient matching info
            System.out.println("     Checking ingredient: " + ingredient.getName() + 
                             " - Required: " + required + " " + ingredient.getUnit() + 
                             " - Available: " + available + " " + ingredient.getUnit());
            
            if (available < required) {
                System.out.println("     Not enough " + ingredient.getName());
                return false;
            }
        }
        System.out.println("     All ingredients available for " + recipe.getName());
        return true;
    }
    
    private boolean isSeasonalRecipe(Recipe recipe, String season) {
        // Implement seasonal logic based on ingredients and tags
        return Arrays.stream(recipe.getTags())
            .anyMatch(tag -> tag.toLowerCase().contains(season.toLowerCase()));
    }

    public List<Recipe> recommendByCalories(int maxCalories, RecipeList recipes) {
        return recipes.stream()
            .filter(recipe -> recipe.getCalories() <= maxCalories)
            .collect(Collectors.toList());
    }

    public List<Recipe> recommendByTag(String tag, RecipeList recipes) {
        return recipes.stream()
            .filter(recipe -> Arrays.stream(recipe.getTags())
                .anyMatch(t -> t.toLowerCase().contains(tag.toLowerCase())))
            .collect(Collectors.toList());
    }
}