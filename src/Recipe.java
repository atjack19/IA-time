public class Recipe {
    private String name;
    private String book;
    private int page;
    private Ingredient[] ingredients;
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
        this.ingredients = ingredients;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.sugars = sugars;
        this.fats = fats;
        this.tags = tags;
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
}