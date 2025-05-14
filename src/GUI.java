import javax.swing.*;
import java.awt.*;

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
    int containerWidth = getContentPane().getWidth();

    public GUI() {
        setTitle("New Window");
        setSize(600,400);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        lblMessage = new JLabel("New Label");
        lblMessage.setBounds(50, 50, standardWidth, 150);
        lblMessage.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        recipeButton = new JButton("Recipes");
        recipeButton.setBounds(75,50,standardWidth,standardHeight);

        stockButton = new JButton("Stock");
        stockButton.setBounds(-50,50,standardWidth,standardHeight);

        monButton = new JButton("Monday");
        monButton.setBounds(75,75,150,30);

        tueButton = new JButton("Tuesday");
        tueButton.setBounds(75,75,150,30);

        wedButton = new JButton("Wednesday");
        wedButton.setBounds(75,75,150,30);

        thuButton = new JButton("Thursday");
        thuButton.setBounds(75,75,150,30);

        friButton = new JButton("Friday");
        friButton.setBounds(75,75,150,30);

        satButton = new JButton("Saturday");
        satButton.setBounds(75,75,150,30);

        sunButton = new JButton("Sunday");
        sunButton.setBounds(75,75,150,30);

        listButton = new JButton("List");
        listButton.setBounds(75,75,150,30);

        clearButton = new JButton("Clear");
        clearButton.setBounds(75,75,150,30);

        add(recipeButton);
        add(stockButton);
        //add(monButton);
        //add(tueButton);
        //add(wedButton);
        //add(thuButton);
        //add(friButton);
        //add(satButton);
        //add(sunButton);
        //add(listButton);
        //add(clearButton);

    }
}
