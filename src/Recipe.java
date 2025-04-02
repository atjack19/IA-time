public class Recipe {
    String name;
    String category;
    String book;
    int page;
    Ingredient[] ingredients;
    int calories;
    int protein;
    int carbs;
    int sugars;
    int fats;
    String[] tags;

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getBook() {
        return book;
    }

    public int getPage() {
        return page;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public int getCalories() {
        return calories;
    }

    public int getProtein() {
        return protein;
    }

    public int getCarbs() {
        return carbs;
    }

    public int getSugars() {
        return sugars;
    }

    public int getFats() {
        return fats;
    }

    public String[] getTags() {
        return tags;
    }

    public void addRecipe(String name, String category, String book, int page, Ingredient[] ingredients, int calories, int protein, int carbs, int sugars, int fats, String[] tags) {
        String line = name + "," + category + "," + book + "," + page + ",";
        for (Ingredient ingredient : ingredients) {
            line += ingredient + ";";
        }
        line = line + "," + calories + "," + protein + "," + carbs + "," + sugars + "," + fats + ",";
        for (String tag : tags) {
            line += tag + ";";
        }
        FileHandler.writeToFile("recipes.txt", line, true); // add an arraylist of recipe names to ensure no duplicates
    }

}
