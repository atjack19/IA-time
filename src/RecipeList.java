import java.util.ArrayList;
import java.util.List;

public class RecipeList extends ArrayList<Recipe> {

    private BinarySearchTree nameBST;
    private BinarySearchTree caloriesBST;

    public RecipeList() {
        super();
        // Initialize BSTs when RecipeList is created
        updateBSTs();
    }

    // Update BSTs when recipes are modified
    private void updateBSTs() {
        nameBST = new BinarySearchTree();
        caloriesBST = new BinarySearchTree();
        
        for (Recipe recipe : this) {
            nameBST.add(recipe.getName());
            caloriesBST.add(String.valueOf(recipe.getCalories()));
        }
    }

    @Override
    public boolean add(Recipe recipe) {
        // Check for duplicates before adding - same name AND same book = duplicate
        if (this.stream().anyMatch(existing -> 
            existing.getName().equals(recipe.getName()) && 
            existing.getBook().equals(recipe.getBook()))) {
            System.out.println("Recipe '" + recipe.getName() + "' from book '" + recipe.getBook() + "' already exists in the recipe book!");
            return false;
        }
        boolean added = super.add(recipe);
        if (added) {
            // Update BSTs when recipe is added
            nameBST.add(recipe.getName());
            caloriesBST.add(String.valueOf(recipe.getCalories()));
        }
        return added;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = super.remove(o);
        if (removed) {
            // Update BSTs when recipe is removed
            updateBSTs();
        }
        return removed;
    }

    public void display() {
        for (Recipe recipe : this) {
            System.out.println(recipe.getName());
        }
    }

    public void displayFormatted() {
        for (Recipe recipe : this) {
            System.out.println("Recipe: " + recipe.getName());
            System.out.println("Book: " + recipe.getBook() + " (Page " + recipe.getPage() + ")");
            System.out.println("Ingredients:");
            for (Ingredient ing : recipe.getIngredients()) {
                System.out.println("- " + ing.getName()+" ("+ing.getQuantity()+ing.getUnit()+")" );
            }
            System.out.println();
        }
    }

    // Method to get recipes sorted by name using BST
    public RecipeList getSortedByName() {
        RecipeList sorted = new RecipeList();
        List<String> sortedNames = nameBST.getAllValues();
        
        // Create a map for quick recipe lookup by name
        java.util.Map<String, Recipe> recipeMap = new java.util.HashMap<>();
        for (Recipe recipe : this) {
            recipeMap.put(recipe.getName(), recipe);
        }
        
        // Add recipes in BST-sorted order
        for (String name : sortedNames) {
            Recipe recipe = recipeMap.get(name);
            if (recipe != null) {
                sorted.add(recipe);
            }
        }
        
        return sorted;
    }

    // Method to get recipes sorted by calories using BST
    public RecipeList getSortedByCalories() {
        RecipeList sorted = new RecipeList();
        List<String> sortedCalories = caloriesBST.getAllValues();
        
        // Create a map for quick recipe lookup by calories (as string)
        java.util.Map<String, Recipe> recipeMap = new java.util.HashMap<>();
        for (Recipe recipe : this) {
            recipeMap.put(String.valueOf(recipe.getCalories()), recipe);
        }
        
        // Add recipes in BST-sorted order
        for (String calories : sortedCalories) {
            Recipe recipe = recipeMap.get(calories);
            if (recipe != null) {
                sorted.add(recipe);
            }
        }
        
        return sorted;
    }

    // Method to search recipes by name using BST fuzzy search
    public RecipeList searchByName(String searchTerm) {
        RecipeList results = new RecipeList();
        List<String> matchingNames = nameBST.searchFor("name", searchTerm);
        
        // Create a map for quick recipe lookup by name
        java.util.Map<String, Recipe> recipeMap = new java.util.HashMap<>();
        for (Recipe recipe : this) {
            recipeMap.put(recipe.getName(), recipe);
        }
        
        // Add recipes that match the search
        for (String name : matchingNames) {
            Recipe recipe = recipeMap.get(name);
            if (recipe != null) {
                results.add(recipe);
            }
        }
        
        return results;
    }

    // Method to search recipes by book using BST
    public RecipeList searchByBook(String searchTerm) {
        RecipeList results = new RecipeList();
        
        // Simple linear search for book names (more reliable for partial matches)
        for (Recipe recipe : this) {
            if (recipe.getBook().toLowerCase().contains(searchTerm.toLowerCase())) {
                results.add(recipe);
            }
        }
        
        return results;
    }

    // Method to get recipes within a calorie range using BST
    public RecipeList getRecipesByCalorieRange(int minCalories, int maxCalories) {
        RecipeList results = new RecipeList();
        List<String> sortedCalories = caloriesBST.getAllValues();
        
        // Create a map for quick recipe lookup by calories
        java.util.Map<String, Recipe> recipeMap = new java.util.HashMap<>();
        for (Recipe recipe : this) {
            recipeMap.put(String.valueOf(recipe.getCalories()), recipe);
        }
        
        // Add recipes within the calorie range
        for (String caloriesStr : sortedCalories) {
            int calories = Integer.parseInt(caloriesStr);
            if (calories >= minCalories && calories <= maxCalories) {
                Recipe recipe = recipeMap.get(caloriesStr);
                if (recipe != null) {
                    results.add(recipe);
                }
            }
        }
        
        return results;
    }

    // Display BST information for debugging
    public void displayBSTInfo() {
        System.out.println("Name BST contains " + nameBST.getAllValues().size() + " entries");
        System.out.println("Calories BST contains " + caloriesBST.getAllValues().size() + " entries");
    }
}
