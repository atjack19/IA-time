public class LeftoverMeal extends Recipe {
    private int portions;
    public LeftoverMeal(String name,
                        int portions,
                        int calories,
                        int protein,
                        int carbs,
                        int sugars,
                        int fats,
                        String[] tags) {

        super(name,"",0,new Ingredient[0],calories, protein, carbs, sugars, fats, tags);

        this.portions   = portions;
    }

    @Override
    public Ingredient[] getIngredients() {
        return new Ingredient[0];
    }

    public int getPortions() {
        return portions;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }
}