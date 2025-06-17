public class LeftoverMeal extends Recipe {
    private String name;
    private int calories;
    private int protein;
    private int carbs;
    private int sugars;
    private int fats;
    private String[] tags;
    private String storageDate;

    public LeftoverMeal(String name, int calories, int protein, int carbs, 
                       int sugars, int fats, String[] tags, 
                       String sourceContainer, String storageDate) {
        // Call super with empty/default values for unused properties
        super(name, "", 0, new Ingredient[0], calories, protein, 
              carbs, sugars, fats, tags);
        this.storageDate = storageDate;
    }

    // Override methods that shouldn't apply to leftovers
    @Override
    public Ingredient[] getIngredients() {
        return new Ingredient[0]; // Return empty array as leftovers don't need ingredients
    }

    public String getStorageDate() {
        return storageDate;
    }

    @Override
    public String toString() {
        return "Leftover: " + getName() + ", stored " + storageDate + ")";
    }
}