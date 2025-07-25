import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MealPlan {
    private HashMap<String, Recipe> weeklyMealPlan = new HashMap<>();
    private Runnable onChange;

    public MealPlan(HashMap<String, Recipe> weeklyMealPlan) {
        this.weeklyMealPlan = weeklyMealPlan;
    }

    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    public List<Recipe> getAllMeals() {
        return new ArrayList<>(weeklyMealPlan.values());
    }
    
    public ShoppingList generateShoppingList(Inventory inventory) {
        ShoppingList list = new ShoppingList();
        list.generateList(this, inventory);
        return list;
    }

    public void displayMealPlan() {
        System.out.println("\n=== WEEKLY MEAL PLAN ===");
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        
        for (String day : days) {
            Recipe recipe = weeklyMealPlan.get(day);
            if (recipe != null) {
                System.out.printf("%s: %s%n", day, recipe.getName());
            } else {
                System.out.printf("%s: No meal planned%n", day);
            }
        }
    }

    public void addMeal(String day, Recipe recipe) {
        weeklyMealPlan.put(day, recipe);
        if (onChange != null) onChange.run();
    }

    public Recipe getMeal(String day) {
        return weeklyMealPlan.get(day);
    }

    public void clearDay(String day) {
        weeklyMealPlan.remove(day);
        System.out.println("Cleared meal for " + day);
        if (onChange != null) onChange.run();
    }

    public void clearAllMeals() {
        weeklyMealPlan.clear();
        System.out.println("Cleared all meals from the weekly plan");
        if (onChange != null) onChange.run();
    }

    // Check if a day has a meal planned
    public boolean hasMeal(String day) {
        return weeklyMealPlan.containsKey(day);
    }

    // Get all days that have meals planned
    public List<String> getPlannedDays() {
        return new ArrayList<>(weeklyMealPlan.keySet());
    }
}