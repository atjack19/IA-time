import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        RecipeList recipes = FileHandler.loadAllRecipes();
        Inventory inventory = FileHandler.loadInventory();
        List<LeftoverMeal> leftoverMeals = FileHandler.loadLeftoverMeals();
        RecipeRecommender recommender = new RecipeRecommender();
        HashMap<String, Recipe> weeklyPlan = new HashMap<>();
        MealPlan mealPlan = new MealPlan(weeklyPlan);
        // Load meal plan from file
        FileHandler.loadMealPlan(mealPlan, recipes);
        // Enable auto-save on change
        mealPlan.setOnChange(new Runnable() {
            public void run() {
                FileHandler.saveMealPlan(mealPlan);
            }
        });
        // Add shutdown hook to save meal plan on exit
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                FileHandler.saveMealPlan(mealPlan);
            }
        }));
        GUI myGUI = new GUI(recipes, inventory, leftoverMeals, recommender, mealPlan);
    }
}