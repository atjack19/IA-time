import java.util.HashMap;
import java.util.Map;

public class Recipe {
    private String name;
    private String book;
    private int page;
    //private Ingredient[] ingredients;
    Map<Ingredient, Double> ingredientQuantities = new HashMap<>();
    private int calories;
    private int protein;
    private int carbs;
    private int sugars;
    private int fats;
    private String[] tags;

    public Recipe(String name, String book, int page, Ingredient[] ingredients, int calories, int protein, int carbs, int sugars, int fats, String[] tags) {
        this.name = name;
        this.book = book;
        this.page = page;
        //this.ingredients = ingredients;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.sugars = sugars;
        this.fats = fats;
        this.tags = tags;

        // Initialize ingredients with default quantities
        for (Ingredient ingredient : ingredients) {
            this.addIngredient(ingredient, ingredient.getQuantity());
        }
    }

    public String getName() {
        return name;
    }

    public String getBook() {
        return book;
    }

    public int getPage() {
        return page;
    }

    public Ingredient[] getIngredients() {
        return ingredientQuantities.keySet().toArray(new Ingredient[0]);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recipe other = (Recipe) obj;
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    public void addIngredient(Ingredient ingredient, double quantity) {
        ingredientQuantities.put(ingredient, quantity);
    }
    
    public double getRequiredQuantity(Ingredient ingredient) {
        return ingredientQuantities.getOrDefault(ingredient, 0.0);
    }

}