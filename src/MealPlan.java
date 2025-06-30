import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MealPlan {
    private HashMap<String, Recipe> weeklyMealPlan;

    public MealPlan(HashMap<String, Recipe> weeklyMealPlan) {
        this.weeklyMealPlan = weeklyMealPlan;
    }

    public List<Recipe> getAllMeals() {
        return new ArrayList<>(weeklyMealPlan.values());
    }
    
//    public ShoppingList generateShoppingList(Inventory inventory) {
//        ShoppingList list = new ShoppingList();
//        list.generateList(this, inventory);
//        return list;
//    }
}