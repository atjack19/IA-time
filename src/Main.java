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
        GUI myGUI = new GUI(recipes, inventory, leftoverMeals, recommender, mealPlan);
    }
}