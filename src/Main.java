import java.util.HashMap;
import java.util.List;

/**
 * Main entry point for the meal planning application.
 * Loads all data from files and starts the GUI.
 */
public class Main {
    public static void main(String[] args) {
        // Load all persistent data from text files
        RecipeList recipes = FileHandler.loadAllRecipes();
        Inventory inventory = FileHandler.loadInventory();
        List<LeftoverMeal> leftoverMeals = FileHandler.loadLeftoverMeals();
        RecipeRecommender recommender = new RecipeRecommender();
        HashMap<String, Recipe> weeklyPlan = new HashMap<>();
        MealPlan mealPlan = new MealPlan(weeklyPlan);
        
        // Load any previously saved meal plan
        FileHandler.loadMealPlan(mealPlan, recipes);
        
        // Auto-save meal plan whenever it changes
        mealPlan.setOnChange(new Runnable() {
            public void run() {
                FileHandler.saveMealPlan(mealPlan);
            }
        });
        
        // Save meal plan when application closes
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                FileHandler.saveMealPlan(mealPlan);
            }
        }));
        
        // Start the main GUI
        GUI myGUI = new GUI(recipes, inventory, leftoverMeals, recommender, mealPlan);
    }
}