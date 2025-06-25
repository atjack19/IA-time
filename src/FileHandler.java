import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static BinarySearchTree createBST(int type) { // fix this to iterate over loadallrecipes?? recipefileread only reads names
        ArrayList<String> recipeFile = recipeFileRead();
        BinarySearchTree bst = new BinarySearchTree();
        assert recipeFile != null;
        for (String line : recipeFile) {
            bst.add(line.split(",")[type]);
        }
        return bst;
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
            sb.append(ing.getName()).append(";");
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
}