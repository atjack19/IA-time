import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//Recipe Name,Book,Page,ingredient1:quantity1:unit1;ingredient2:quantity2:unit2,calories,...

public class FileHandler {
    public static ArrayList<String> recipeFileRead() {
        try {
            FileReader fr = new FileReader("recipes.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            ArrayList<String> recipes = new ArrayList<>();
            while (line != null) {
                recipes.add(line.split(",")[0]);
                line = br.readLine();
            }
            return recipes;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BinarySearchTree createBST(int type) {
        RecipeList recipes = loadAllRecipes();
        BinarySearchTree bst = new BinarySearchTree();
        
        for (Recipe recipe : recipes) {
            switch (type) {
                case 0: // name
                    bst.add(recipe.getName());
                    break;
                case 1: // book
                    bst.add(recipe.getBook());
                    break;
                case 2: // page (convert to string for BST)
                    bst.add(String.valueOf(recipe.getPage()));
                    break;
                case 3: // calories (convert to string for BST)
                    bst.add(String.valueOf(recipe.getCalories()));
                    break;
                default:
                    bst.add(recipe.getName()); // default to name
            }
        }
        return bst;
    }

    // Create BST for recipe names (most common use case)
    public static BinarySearchTree createNameBST() {
        return createBST(0);
    }

    // Create BST for recipe calories
    public static BinarySearchTree createCaloriesBST() {
        return createBST(3);
    }

    public static void writeToFile(String fileName, String text, boolean append) {
        // write text to fileName, overwriting (append = false) or appending (append = true)
        try (
                FileWriter fw = new FileWriter(fileName, append);
                PrintWriter pw = new PrintWriter(fw)
        ) {
            pw.println(text);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void addRecipe(String name, String book, int page, Ingredient[] ingredients, int calories, int protein, int carbs, int sugars, int fats, String[] tags) {
//        String line = name + "," + book + "," + page + ",";
//        for (Ingredient ingredient : ingredients) {
//            line += ingredient.getName() + ";";
//        }
//        line = line + "," + calories + "," + protein + "," + carbs + "," + sugars + "," + fats + ",";
//        for (String tag : tags) {
//            line += tag + ";";
//        }
//
//        try {
//            if (FileHandler.recipeFileRead() != null && !FileHandler.recipeFileRead().contains(name)) {
//                FileHandler.writeToFile("recipes.txt", line, true); // add an arraylist of recipe names to ensure no duplicates
//            }
//        } catch (NullPointerException e) {
//            FileHandler.writeToFile("recipes.txt", line, true);
//        }
//    }



    public static RecipeList loadAllRecipes() {
        RecipeList recipes = new RecipeList();
        try (BufferedReader br = new BufferedReader(new FileReader("recipes.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                Recipe recipe = parseRecipeLine(line);
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    private static Recipe parseRecipeLine(String line) {
        try {
            // 0- name | 1- book | 2- page | 3- ingredients | 4--8 nutrition | 9- tags
            String[] parts = line.split(",");
            if (parts.length < 10) return null;

            // Parse basic information
            String name = parts[0];
            String book = parts[1];
            int page = Integer.parseInt(parts[2]);

            // Parse ingredients
            String[] ingredientStrings = parts[3].split(";");
            List<Ingredient> ingredientList = new ArrayList<>();

            for (String ing : ingredientStrings) {
                if (!ing.isEmpty()) {
                    String[] ingParts = ing.split(":");
                    if (ingParts.length >= 3) {
                        Ingredient ingredient = new Ingredient(
                                ingParts[0],                    // name
                                Double.parseDouble(ingParts[1]), // quantity
                                ingParts[2]                     // unit
                        );
                        ingredientList.add(ingredient);
                    }
                }
            }
            Ingredient[] ingredients = ingredientList.toArray(new Ingredient[0]);

            // Parse nutritional information
            int calories = Integer.parseInt(parts[4]);
            int protein = Integer.parseInt(parts[5]);
            int carbs = Integer.parseInt(parts[6]);
            int sugars = Integer.parseInt(parts[7]);
            int fats = Integer.parseInt(parts[8]);

            // Parse tags
            String[] tags = parts[9].split(";");

            return new Recipe(name, book, page, ingredients, 
                            calories, protein, carbs, sugars, fats, tags);
        } catch (Exception e) {
            System.err.println("Error parsing recipe line: " + line);
            e.printStackTrace();
            return null;
        }
    }

    // Method to save all recipes (overwrites the file)
    public static void saveAllRecipes(List<Recipe> recipes) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("recipes.txt", false))) {
            for (Recipe recipe : recipes) {
                String line = formatRecipeToLine(recipe);
                pw.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatRecipeToLine(Recipe recipe) {
        StringBuilder sb = new StringBuilder();
        
        // Add basic information
        sb.append(recipe.getName()).append(",")
          .append(recipe.getBook()).append(",")
          .append(recipe.getPage()).append(",");

        // Add ingredients
        for (Ingredient ing : recipe.getIngredients()) {
            sb.append(ing.getName()).append(":").append(ing.getQuantity()).append(":").append(ing.getUnit()).append(";");
        }
        sb.append(",");

        // Add nutritional information
        sb.append(recipe.getCalories()).append(",")
          .append(recipe.getProtein()).append(",")
          .append(recipe.getCarbs()).append(",")
          .append(recipe.getSugars()).append(",")
          .append(recipe.getFats()).append(",");

        // Add tags
        for (String tag : recipe.getTags()) {
            sb.append(tag).append(";");
        }

        return sb.toString();
    }
    public static boolean removeRecipe(String recipeName) {
        RecipeList recipes = loadAllRecipes();
        
        // Find and remove the recipe with the matching name
        boolean removed = recipes.removeIf(recipe -> 
            recipe.getName().equalsIgnoreCase(recipeName));
        
        if (removed) {
            // If recipe was found and removed, save the updated list
            saveAllRecipes(recipes);
            System.out.println("Recipe '" + recipeName + "' removed successfully!");
            return true;
        } else {
            System.out.println("Recipe '" + recipeName + "' not found!");
            return false;
        }
    }

    // Inventory file handling methods
    public static void saveInventory(Inventory inventory) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("inventory.txt", false))) {
            // Format: ingredientName:quantity:unit
            for (Map.Entry<Ingredient, Double> entry : inventory.getAllIngredients().entrySet()) {
                Ingredient ingredient = entry.getKey();
                double quantity = entry.getValue();
                String line = ingredient.getName() + ":" + quantity + ":" + ingredient.getUnit();
                pw.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Inventory loadInventory() {
        Inventory inventory = new Inventory();
        try (BufferedReader br = new BufferedReader(new FileReader("inventory.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 3) {
                    Ingredient ingredient = new Ingredient(
                        parts[0],                    // name
                        Double.parseDouble(parts[1]), // quantity
                        parts[2]                     // unit
                    );
                    inventory.addIngredient(ingredient, ingredient.getQuantity());
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty inventory
            System.out.println("No inventory file found. Starting with empty inventory.");
        }
        return inventory;
    }

    // LeftoverMeal file handling methods
    public static void saveLeftoverMeals(List<LeftoverMeal> leftoverMeals) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("leftover_meals.txt", false))) {
            for (LeftoverMeal meal : leftoverMeals) {
                String line = formatLeftoverMealToLine(meal);
                pw.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<LeftoverMeal> loadLeftoverMeals() {
        List<LeftoverMeal> leftoverMeals = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("leftover_meals.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                LeftoverMeal meal = parseLeftoverMealLine(line);
                if (meal != null) {
                    leftoverMeals.add(meal);
                }
            }
        } catch (IOException e) {
            System.out.println("No leftover meals file found.");
        }
        return leftoverMeals;
    }

    private static String formatLeftoverMealToLine(LeftoverMeal meal) {
        StringBuilder sb = new StringBuilder();
        sb.append(meal.getName()).append(",")
          .append(meal.getPortions()).append(",")
          .append(meal.getCalories()).append(",")
          .append(meal.getProtein()).append(",")
          .append(meal.getCarbs()).append(",")
          .append(meal.getSugars()).append(",")
          .append(meal.getFats()).append(",");
        
        for (String tag : meal.getTags()) {
            sb.append(tag).append(";");
        }
        
        return sb.toString();
    }

    private static LeftoverMeal parseLeftoverMealLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length < 8) return null;

            String name = parts[0];
            int portions = Integer.parseInt(parts[1]);
            int calories = Integer.parseInt(parts[2]);
            int protein = Integer.parseInt(parts[3]);
            int carbs = Integer.parseInt(parts[4]);
            int sugars = Integer.parseInt(parts[5]);
            int fats = Integer.parseInt(parts[6]);
            String[] tags = parts[7].split(";");

            return new LeftoverMeal(name, portions, calories, protein, carbs, sugars, fats, tags);
        } catch (Exception e) {
            System.err.println("Error parsing leftover meal line: " + line);
            e.printStackTrace();
            return null;
        }
    }

    // Save the meal plan to meal_plan.txt (Day,RecipeName,Book,Page)
    public static void saveMealPlan(MealPlan mealPlan) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("meal_plan.txt", false))) {
            for (String day : mealPlan.getPlannedDays()) {
                Recipe recipe = mealPlan.getMeal(day);
                if (recipe != null) {
                    pw.println(day + "," + recipe.getName() + "," + recipe.getBook() + "," + recipe.getPage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the meal plan from meal_plan.txt (Day,RecipeName,Book,Page)
    public static void loadMealPlan(MealPlan mealPlan, RecipeList recipes) {
        try (BufferedReader br = new BufferedReader(new FileReader("meal_plan.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length == 4) {
                    String day = parts[0];
                    String recipeName = parts[1];
                    String book = parts[2];
                    int page = Integer.parseInt(parts[3]);
                    // Find the recipe by name, book, and page
                    for (Recipe r : recipes) {
                        if (r.getName().equals(recipeName) && r.getBook().equals(book) && r.getPage() == page) {
                            mealPlan.addMeal(day, r);
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            // File may not exist yet, that's fine
        }
    }
}