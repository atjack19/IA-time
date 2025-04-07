import java.util.HashMap;

public class MealPlan {
    private HashMap<String, Recipe> weeklyMealPlan = new HashMap<>();

    public void addMeal(String day, Recipe recipe) {
        weeklyMealPlan.put(day, recipe);
    }

    public Recipe getMealForDay(String day) {
        return weeklyMealPlan.getOrDefault(day, null);
    }

//    public ShoppingList generateShoppingList(HashMap weeklyMealPlan) {
//        //generate shopping list
//    }
}