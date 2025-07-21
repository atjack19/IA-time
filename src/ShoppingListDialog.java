import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class ShoppingListDialog extends JDialog {
    public ShoppingListDialog(JFrame parent, ShoppingList shoppingList) {
        super(parent, "Shopping List", true);
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        StringBuilder sb = new StringBuilder();
        Map<Ingredient, Double> missing = shoppingList.getMissingIngredients();
        if (missing.isEmpty()) {
            sb.append("You have everything you need!\n");
        } else {
            sb.append("Ingredients you need to buy:\n\n");
            for (Map.Entry<Ingredient, Double> entry : missing.entrySet()) {
                Ingredient ing = entry.getKey();
                double qty = entry.getValue();
                sb.append("- ").append(ing.getName()).append(": ")
                  .append(String.format("%.2f", qty)).append(" ")
                  .append(ing.getUnit()).append("\n");
            }
        }
        textArea.setText(sb.toString());
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }
} 