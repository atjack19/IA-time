import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class GUI extends JFrame {
    JButton recipeBtn;
    JButton stockBtn;
    JButton monBtn;
    JButton tueBtn;
    JButton wedBtn;
    JButton thuBtn;
    JButton friBtn;
    JButton satBtn;
    JButton sunBtn;
    JButton listBtn;
    JButton clearBtn;
    int btnWidth = 150;
    int btnHeight = 30;
    private MealPlan mealPlan;
    private Inventory inventory;

    public GUI(RecipeList recipes, Inventory inventory, List<LeftoverMeal> leftoverMeals, RecipeRecommender recommender, MealPlan mealPlan) {
        this.mealPlan = mealPlan;
        this.inventory = inventory;
        setTitle("Main Page");
        setSize(1920, 1080);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int xStart = 432; // scaled from 308 for 1920 width

        btnWidth = 210; // scaled from 150
        btnHeight = 42; // scaled from 30

        recipeBtn = new JButton("Recipes");
        recipeBtn.setBounds(105, 70, btnWidth, btnHeight); // scaled from 75, 50
        add(recipeBtn);

        stockBtn = new JButton("Stock");
        stockBtn.setBounds(1920 - btnWidth - 105, 70, btnWidth, btnHeight); // scaled from 1366, 75
        add(stockBtn);

        monBtn = new JButton();
        monBtn.setBounds(xStart, 210, 210, 210); // scaled from 150, 150, 150
        add(monBtn);

        tueBtn = new JButton();
        tueBtn.setBounds(xStart + 280, 210, 210, 210); // scaled from 200, 150, 150
        add(tueBtn);

        wedBtn = new JButton();
        wedBtn.setBounds(xStart + 560, 210, 210, 210); // scaled from 400, 150, 150
        add(wedBtn);

        thuBtn = new JButton();
        thuBtn.setBounds(xStart + 840, 210, 210, 210); // scaled from 600, 150, 150
        add(thuBtn);

        friBtn = new JButton();
        friBtn.setBounds(xStart + 140, 490, 210, 210); // scaled from 100, 350, 150
        add(friBtn);

        satBtn = new JButton();
        satBtn.setBounds(xStart + 420, 490, 210, 210); // scaled from 300, 350, 150
        add(satBtn);

        sunBtn = new JButton();
        sunBtn.setBounds(xStart + 700, 490, 210, 210); // scaled from 500, 350, 150
        add(sunBtn);

        listBtn = new JButton("View List");
        listBtn.setBounds(105, 1080 - btnHeight - 105, 210, 42); // scaled from 75, 768, 150, 30
        add(listBtn);

        clearBtn = new JButton("Clear all selections");
        clearBtn.setBounds(1920 - btnWidth - 105, 1080 - btnHeight - 105, 252, 42); // scaled from 1366, 75, 180, 30
        add(clearBtn);

        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mealPlan.clearAllMeals();
                updateDayButtons();
            }
        });

        listBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ShoppingList shoppingList = new ShoppingList();
                shoppingList.generateList(mealPlan, inventory);
                ShoppingListDialog dialog = new ShoppingListDialog(GUI.this, shoppingList);
                dialog.setVisible(true);
            }
        });

        JPanel mainPanel = new JPanel(null);
        for (Component c : getContentPane().getComponents()) {
            mainPanel.add(c);
        }
        getContentPane().removeAll();
        setContentPane(mainPanel);
        setVisible(true);

        recipeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RecipeListPage recipeListPage = new RecipeListPage(new Runnable() {
                    public void run() {
                        setContentPane(mainPanel);
                        revalidate();
                        repaint();
                        updateDayButtons();
                    }
                }, recipes, inventory);
                recipeListPage.setMealPlan(mealPlan);
                setContentPane(recipeListPage);
                revalidate();
                repaint();
            }
        });
        stockBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StockListPage stockListPage = new StockListPage(new Runnable() {
                    public void run() {
                        setContentPane(mainPanel);
                        revalidate();
                        repaint();
                        updateDayButtons();
                    }
                }, inventory, leftoverMeals);
                stockListPage.setMealPlan(mealPlan);
                setContentPane(stockListPage);
                revalidate();
                repaint();
            }
        });
        updateDayButtons();
    }

    private void updateDayButtons() {
        updateDayButton(monBtn, "Monday");
        updateDayButton(tueBtn, "Tuesday");
        updateDayButton(wedBtn, "Wednesday");
        updateDayButton(thuBtn, "Thursday");
        updateDayButton(friBtn, "Friday");
        updateDayButton(satBtn, "Saturday");
        updateDayButton(sunBtn, "Sunday");
    }

    private void updateDayButton(JButton btn, String day) {
        Recipe recipe = mealPlan.getMeal(day);
        String recipeName = (recipe != null) ? recipe.getName() : "[None]";
        int missing = 0;
        if (recipe != null) {
            Ingredient[] ings = recipe.getIngredients();
            for (Ingredient ing : ings) {
                double have = inventory.getQuantity(ing);
                if (have < ing.getQuantity()) missing++;
            }
        }
        btn.setText("<html>" + day.substring(0, 3) + "<br>Recipe: " + recipeName + "<br>Missing: " + missing + "</html>");
    }
}
