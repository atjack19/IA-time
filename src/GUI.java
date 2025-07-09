import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    JLabel lblMessage;
    JButton recipeButton;
    JButton stockButton;
    JButton monButton;
    JButton tueButton;
    JButton wedButton;
    JButton thuButton;
    JButton friButton;
    JButton satButton;
    JButton sunButton;
    JButton listButton;
    JButton clearButton;
    int standardWidth = 150;
    int standardHeight = 30;
    int containerWidth;
    int containerHeight;
    private MealPlan mealPlan;

    public GUI(RecipeList recipes, Inventory inventory, List<LeftoverMeal> leftoverMeals, RecipeRecommender recommender, MealPlan mealPlan) {
        this.mealPlan = mealPlan;
        setTitle("Main Page");
        setSize(1366, 768);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        containerWidth = getContentPane().getWidth();
        containerHeight = getContentPane().getHeight();
        int xOffset = 308;
        
        // Top buttons
        recipeButton = new JButton("Recipes");
        recipeButton.setBounds(75, 50, standardWidth, standardHeight);
        add(recipeButton);

        stockButton = new JButton("Stock");
        stockButton.setBounds(1366 - standardWidth - 75, 50, standardWidth, standardHeight);
        add(stockButton);

        // Day buttons with placeholders for recipe and missing stock
        monButton = new JButton("<html>Mon<br><small>Recipe: [None]<br>Missing: 0</small></html>");
        monButton.setBounds(xOffset, 150, 150, 150);
        add(monButton);

        tueButton = new JButton("<html>Tue<br><small>Recipe: [None]<br>Missing: 0</small></html>");
        tueButton.setBounds(xOffset + 200, 150, 150, 150);
        add(tueButton);

        wedButton = new JButton("<html>Wed<br><small>Recipe: [None]<br>Missing: 0</small></html>");
        wedButton.setBounds(xOffset + 400, 150, 150, 150);
        add(wedButton);

        thuButton = new JButton("<html>Thu<br><small>Recipe: [None]<br>Missing: 0</small></html>");
        thuButton.setBounds(xOffset + 600, 150, 150, 150);
        add(thuButton);

        friButton = new JButton("<html>Fri<br><small>Recipe: [None]<br>Missing: 0</small></html>");
        friButton.setBounds(xOffset + 100, 350, 150, 150);
        add(friButton);

        satButton = new JButton("<html>Sat<br><small>Recipe: [None]<br>Missing: 0</small></html>");
        satButton.setBounds(xOffset + 300, 350, 150, 150);
        add(satButton);

        sunButton = new JButton("<html>Sun<br><small>Recipe: [None]<br>Missing: 0</small></html>");
        sunButton.setBounds(xOffset + 500, 350, 150, 150);
        add(sunButton);

        // Bottom buttons
        listButton = new JButton("View List");
        listButton.setBounds(75, 768 - standardHeight - 75, 150, 30);
        add(listButton);

        clearButton = new JButton("Clear all selections");
        clearButton.setBounds(1366 - standardWidth - 75, 768 - standardHeight - 75, 180, 30);
        add(clearButton);
        // Clear all day selections
        clearButton.addActionListener(e -> {
            // Clear all meals in the MealPlan data structure
            mealPlan.clearAllMeals();
            // Reset all day button labels
            String resetText = "<html>%s<br><small>Recipe: [None]<br>Missing: 0</small></html>";
            monButton.setText(String.format(resetText, "Mon"));
            tueButton.setText(String.format(resetText, "Tue"));
            wedButton.setText(String.format(resetText, "Wed"));
            thuButton.setText(String.format(resetText, "Thu"));
            friButton.setText(String.format(resetText, "Fri"));
            satButton.setText(String.format(resetText, "Sat"));
            sunButton.setText(String.format(resetText, "Sun"));
        });

        // --- Navigation logic ---
        // Store a reference to the main content for switching back
        JPanel mainPanel = new JPanel(null);
        for (Component c : getContentPane().getComponents()) {
            mainPanel.add(c);
        }
        // Remove all from frame and add mainPanel
        getContentPane().removeAll();
        setContentPane(mainPanel);
        setVisible(true);

        // Action to switch to RecipeListPage
        recipeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RecipeListPage recipeListPage = new RecipeListPage(() -> {
                    setContentPane(mainPanel);
                    revalidate();
                    repaint();
                }, recipes, inventory);
                setContentPane(recipeListPage);
                revalidate();
                repaint();
            }
        });
        // Action to switch to StockListPage
        stockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StockListPage stockListPage = new StockListPage(() -> {
                    setContentPane(mainPanel);
                    revalidate();
                    repaint();
                }, inventory, leftoverMeals);
                setContentPane(stockListPage);
                revalidate();
                repaint();
            }
        });
    }
}
