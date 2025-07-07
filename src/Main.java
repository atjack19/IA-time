import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

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

        // 1. Initialize recipe system with file persistence
        RecipeList recipes = FileHandler.loadAllRecipes();
        Inventory inventory = FileHandler.loadInventory();
        List<LeftoverMeal> leftoverMeals = FileHandler.loadLeftoverMeals();
        RecipeRecommender recommender = new RecipeRecommender();
        HashMap<String, Recipe> weeklyPlan = new HashMap<>();
        GUI myGUI = new GUI(recipes, inventory, leftoverMeals, recommender, weeklyPlan,);

        // 2. Add some ingredients to inventory if empty
        if (inventory.getAllIngredients().isEmpty()) {
            inventory.addIngredient(new Ingredient("flour", 1000, "g"), 1000);
            inventory.addIngredient(new Ingredient("sugar", 500, "g"), 500);
            inventory.addIngredient(new Ingredient("eggs", 12, "large"), 12);
            inventory.addIngredient(new Ingredient("butter", 250, "g"), 250);
            inventory.addIngredient(new Ingredient("cheese", 300, "g"), 300);
            inventory.addIngredient(new Ingredient("milk", 1000, "ml"), 1000);
            FileHandler.saveInventory(inventory);
        }

        // 3. Test duplication checker - try to add a duplicate recipe
        System.out.println("\n=== TESTING DUPLICATION CHECKER ===");
        Recipe duplicateRecipe = new Recipe(
            "Lasagna", "Jamie Oliver Book 3", 325,
            new Ingredient[]{new Ingredient("cheese", 150, "g")},
            345, 24, 12, 23, 43,
            new String[]{"cheese", "pasta", "lasagna", "low-calorie"}
        );
        boolean added = recipes.add(duplicateRecipe);
        System.out.println("Attempted to add duplicate recipe: " + (added ? "SUCCESS" : "BLOCKED (correctly)"));

        // 4. Test adding a recipe with same name but different book (should work)
        Recipe sameNameDifferentBook = new Recipe(
            "Lasagna", "Different Cookbook", 50,
            new Ingredient[]{new Ingredient("cheese", 200, "g")},
            400, 30, 15, 25, 50,
            new String[]{"cheese", "pasta", "lasagna"}
        );
        added = recipes.add(sameNameDifferentBook);
        System.out.println("Attempted to add same name, different book: " + (added ? "SUCCESS" : "BLOCKED"));

        // 5. Create and add some recipes if not already present
        if (recipes.size() < 8) {
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

            recipes.add(cakeRecipe);
            recipes.add(cookieRecipe);
            FileHandler.saveAllRecipes(recipes);
        }

        // 6. Add some leftover meals
        if (leftoverMeals.isEmpty()) {
            LeftoverMeal leftoverPasta = new LeftoverMeal(
                "Leftover Pasta", 2, 400, 12, 60, 8, 15, 
                new String[]{"leftover", "pasta", "quick"}
            );
            leftoverMeals.add(leftoverPasta);
            FileHandler.saveLeftoverMeals(leftoverMeals);
        }

        // 7. Test sorting functionality using BST
        System.out.println("\n=== TESTING BST-BASED SORTING FUNCTIONALITY ===");
        System.out.println("Recipes sorted by name (using BST):");
        RecipeList sortedByName = recipes.getSortedByName();
        for (Recipe recipe : sortedByName) {
            System.out.println("- " + recipe.getName());
        }

        System.out.println("\nRecipes sorted by calories (using BST):");
        RecipeList sortedByCalories = recipes.getSortedByCalories();
        for (Recipe recipe : sortedByCalories) {
            System.out.println("- " + recipe.getName() + " (" + recipe.getCalories() + " cal)");
        }

        // 8. Test BST-based fuzzy search functionality
        System.out.println("\n=== TESTING BST-BASED FUZZY SEARCH ===");
        System.out.println("Searching for recipes containing 'chicken' (using BST):");
        RecipeList chickenResults = recipes.searchByName("chicken");
        for (Recipe recipe : chickenResults) {
            System.out.println("- " + recipe.getName());
        }

        System.out.println("\nSearching for recipes containing 'cake' (using BST):");
        RecipeList cakeResults = recipes.searchByName("cake");
        for (Recipe recipe : cakeResults) {
            System.out.println("- " + recipe.getName());
        }

        System.out.println("\nSearching for recipes from books containing 'Jamie' (using BST):");
        RecipeList jamieResults = recipes.searchByBook("Jamie");
        for (Recipe recipe : jamieResults) {
            System.out.println("- " + recipe.getName() + " (" + recipe.getBook() + ")");
        }

        // 9. Test calorie range search using BST
        System.out.println("\n=== TESTING BST-BASED CALORIE RANGE SEARCH ===");
        System.out.println("Recipes with 200-350 calories (using BST):");
        RecipeList calorieRangeResults = recipes.getRecipesByCalorieRange(200, 350);
        for (Recipe recipe : calorieRangeResults) {
            System.out.println("- " + recipe.getName() + " (" + recipe.getCalories() + " cal)");
        }

        // 10. Display BST information
        System.out.println("\n=== BST INFORMATION ===");
        recipes.displayBSTInfo();

        // 11. Test meal plan management
        System.out.println("\n=== TESTING MEAL PLAN MANAGEMENT ===");

        weeklyPlan.put("Monday", recipes.get(0));
        weeklyPlan.put("Wednesday", recipes.get(1));
        weeklyPlan.put("Friday", recipes.get(2));
        MealPlan mealPlan = new MealPlan(weeklyPlan);

        System.out.println("Initial meal plan:");
        mealPlan.displayMealPlan();

        // Test clearing individual day
        System.out.println("\nClearing Wednesday:");
        mealPlan.clearDay("Wednesday");
        mealPlan.displayMealPlan();

        // Test adding a meal
        System.out.println("\nAdding meal for Saturday:");
        mealPlan.addMeal("Saturday", recipes.get(3));
        mealPlan.displayMealPlan();

        // Test clearing all meals
        System.out.println("\nClearing all meals:");
        mealPlan.clearAllMeals();
        mealPlan.displayMealPlan();

        // 12. Test shopping list generation
        System.out.println("\n=== TESTING SHOPPING LIST GENERATION ===");
        // Re-add some meals for shopping list test
        mealPlan.addMeal("Monday", recipes.get(0));
        mealPlan.addMeal("Wednesday", recipes.get(1));
        
        ShoppingList shoppingList = mealPlan.generateShoppingList(inventory);
        shoppingList.displayShoppingList();

        // 13. Display all information
        System.out.println("\n=== ALL RECIPES ===");
        recipes.displayFormatted();

        System.out.println("\n=== CURRENT INVENTORY ===");
        for (Map.Entry<Ingredient, Double> entry : inventory.getAllIngredients().entrySet()) {
            Ingredient ingredient = entry.getKey();
            double quantity = entry.getValue();
            System.out.printf("- %s: %.1f %s%n", ingredient.getName(), quantity, ingredient.getUnit());
        }

        System.out.println("\n=== LEFTOVER MEALS ===");
        for (LeftoverMeal meal : leftoverMeals) {
            System.out.printf("- %s (%d portions)%n", meal.getName(), meal.getPortions());
        }

        System.out.println("\n=== RECIPE RECOMMENDATIONS ===");
        System.out.println("Recipes we can make with current inventory:");
        List<Recipe> possibleRecipes = recommender.recommendByInventory(inventory, recipes);
        possibleRecipes.forEach(recipe -> System.out.println("- " + recipe.getName()));

        System.out.println("\nSummer recipes:");
        List<Recipe> summerRecipes = recommender.recommendBySeason("summer", recipes);
        summerRecipes.forEach(recipe -> System.out.println("- " + recipe.getName()));

        System.out.println("\nLow-calorie recipes (under 300 calories):");
        List<Recipe> lowCalRecipes = recommender.recommendByCalories(300, recipes);
        lowCalRecipes.forEach(recipe -> System.out.println("- " + recipe.getName()));

        System.out.println("\nVegetarian recipes:");
        List<Recipe> vegRecipes = recommender.recommendByTag("vegetarian", recipes);
        vegRecipes.forEach(recipe -> System.out.println("- " + recipe.getName()));
    }
}