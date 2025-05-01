import java.io.*;
import java.util.ArrayList;

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
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void recipeFileSearch(String term) {
        // create a sort - merge sort
        // then, binary search for the name
        // separate searches for different terms
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

    public static void addRecipe(String name, String book, int page, Ingredient[] ingredients, int calories, int protein, int carbs, int sugars, int fats, String[] tags) {
        String line = name + "," + book + "," + page + ",";
        for (Ingredient ingredient : ingredients) {
            line += ingredient.getName() + ";";
        }
        line = line + "," + calories + "," + protein + "," + carbs + "," + sugars + "," + fats + ",";
        for (String tag : tags) {
            line += tag + ";";
        }

        try {
            if (FileHandler.recipeFileRead() != null && !FileHandler.recipeFileRead().contains(name)) {
                FileHandler.writeToFile("recipes.txt", line, true); // add an arraylist of recipe names to ensure no duplicates
            }
        } catch (NullPointerException e) {
            FileHandler.writeToFile("recipes.txt", line, true);
        }
    }

    public static void addRecipe(Recipe recipe) {
        addRecipe(recipe.getName(), recipe.getBook(), recipe.getPage(), recipe.getIngredients(), recipe.getCalories(), recipe.getProtein(), recipe.getCarbs(), recipe.getSugars(), recipe.getFats(), recipe.getTags());
    }


}
