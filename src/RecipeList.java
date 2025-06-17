import java.util.ArrayList;

public class RecipeList extends ArrayList<Recipe> {
    @Override
    public boolean add(Recipe recipe) {
        // Check for duplicates before adding
        if (this.stream().anyMatch(existing -> existing.equals(recipe))) {
            System.out.println("Recipe '" + recipe.getName() + "' already exists in the recipe book!");
            return false;
        }
        return super.add(recipe);
    }

    public void display() {
        for (Recipe recipe : this) {
            System.out.println(recipe.getName());
        }
    }

    public void displayFormatted() {
        for (Recipe recipe : this) {
            System.out.println("Recipe: " + recipe.getName());
            System.out.println("Ingredients:");
            for (Ingredient ing : recipe.getIngredients()) {
                System.out.println("- " + ing.getName());
            }
            System.out.println();
        }
    }
}
