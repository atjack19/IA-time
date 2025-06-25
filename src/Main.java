import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        GUI myGUI = new GUI();

        //BinarySearchTree nameBST = FileHandler.createBST(0);
        //nameBST.display();
        //nameBST.searchFor("name", "A");

//        RecipeList recipes = FileHandler.loadAllRecipes();
//
//        Ingredient[] ingredients = {
//                new Ingredient("flour", 200, "g"),
//                new Ingredient("sugar", 150, "g"),
//                new Ingredient("eggs", 3, "large"),
//        };
//        String[] tags = {"dessert", "baking", "sweet"};
//
//        Recipe newRecipe = new Recipe(
//                "Cake", "Baking Basics", 42,
//                ingredients, 300, 5, 45, 30, 10, tags
//        );
//
//        recipes.add(newRecipe);
//        FileHandler.saveAllRecipes(recipes);
//        //recipes = FileHandler.removeRecipe("Cake");
//        recipes = FileHandler.loadAllRecipes();
//
//        recipes.display();
//        recipes.displayFormatted();

        // 1. Initialize recipe system
        RecipeList recipes = FileHandler.loadAllRecipes();
        Inventory inventory = new Inventory();
        RecipeRecommender recommender = new RecipeRecommender();

        // 2. Add some ingredients to inventory
        inventory.addIngredient(new Ingredient("flour", 1000, "g"), 1000);
        inventory.addIngredient(new Ingredient("sugar", 500, "g"), 500);
        inventory.addIngredient(new Ingredient("eggs", 12, "large"), 12);
        inventory.addIngredient(new Ingredient("butter", 250, "g"), 250);

        // 3. Create and add some recipes
        // Cake recipe
        Ingredient[] cakeIngredients = {
                new Ingredient("flour", 200, "g"),
                new Ingredient("sugar", 150, "g"),
                new Ingredient("eggs", 3, "large"),
                new Ingredient("butter", 100, "g")
        };
        String[] cakeTags = {"dessert", "baking", "sweet", "summer"};

        Recipe cakeRecipe = new Recipe(
                "Vanilla Cake",
                "Baking Basics",
                42,
                cakeIngredients,
                300, 5, 45, 30, 10,
                cakeTags
        );

        // Cookie recipe
        Ingredient[] cookieIngredients = {
                new Ingredient("flour", 150, "g"),
                new Ingredient("sugar", 100, "g"),
                new Ingredient("butter", 80, "g")
        };
        String[] cookieTags = {"dessert", "baking", "sweet", "winter"};

        Recipe cookieRecipe = new Recipe(
                "Sugar Cookies",
                "Cookie Book",
                15,
                cookieIngredients,
                200, 2, 30, 25, 8,
                cookieTags
        );

        // 4. Add recipes to the recipe list and save
        recipes.add(cakeRecipe);
        recipes.add(cookieRecipe);
        FileHandler.saveAllRecipes(recipes);

        // 5. Create a meal plan for the week
        HashMap<String, Recipe> weeklyPlan = new HashMap<>();
        weeklyPlan.put("Monday", cakeRecipe);
        weeklyPlan.put("Wednesday", cookieRecipe);
        MealPlan mealPlan = new MealPlan(weeklyPlan);

        // 6. Generate shopping list
        ShoppingList shoppingList = mealPlan.generateShoppingList(inventory);

        // 7. Display various information
        System.out.println("\n=== All Recipes ===");
        recipes.displayFormatted();

        System.out.println("\n=== Recipe Recommendations ===");
        System.out.println("Recipes we can make with current inventory:");
        List<Recipe> possibleRecipes = recommender.recommendByInventory(inventory, recipes);
        possibleRecipes.forEach(recipe -> System.out.println("- " + recipe.getName()));

        System.out.println("\nSummer recipes:");
        List<Recipe> summerRecipes = recommender.recommendBySeason("summer", recipes);
        summerRecipes.forEach(recipe -> System.out.println("- " + recipe.getName()));


    }
}