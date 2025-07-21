import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class RecipeRecommender {
    public List<Recipe> recommendByInventory(Inventory inventory, RecipeList recipes) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (hasRequiredIngredients(recipe, inventory)) {
                result.add(recipe);
            }
        }
        return result;
    }
    
    public List<Recipe> recommendBySeason(String season, RecipeList recipes) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (isSeasonalRecipe(recipe, season)) {
                result.add(recipe);
            }
        }
        return result;
    }

    private boolean hasRequiredIngredients(Recipe recipe, Inventory inventory) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            double required = ingredient.getQuantity();
            double available = inventory.getQuantity(ingredient);
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
        for (String tag : recipe.getTags()) {
            if (tag.toLowerCase().contains(season.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public List<Recipe> recommendByCalories(int maxCalories, RecipeList recipes) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe.getCalories() <= maxCalories) {
                result.add(recipe);
            }
        }
        return result;
    }

    public List<Recipe> recommendByTag(String tag, RecipeList recipes) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            boolean matchesTag = false;
            for (String t : recipe.getTags()) {
                if (t.toLowerCase().contains(tag.toLowerCase())) {
                    matchesTag = true;
                    break;
                }
            }
            if (matchesTag) {
                result.add(recipe);
            }
        }
        return result;
    }
}