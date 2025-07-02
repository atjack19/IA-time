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
    int containerWidth;
    int containerHeight;

    public GUI() {
        setTitle("Main Menu");
        //setSize(1920,1008);
        setSize(1366,768);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        containerWidth = getContentPane().getWidth();
        containerHeight = getContentPane().getHeight();
        int xOffset = 308;
        setVisible(false);
        //uytrewsfghjkm nbv
        recipeButton = new JButton("Recipes");
        recipeButton.setBounds(75,50,standardWidth,standardHeight);

        stockButton = new JButton("Stock");
        stockButton.setBounds(1366-standardWidth-75,50,standardWidth,standardHeight);

        monButton = new JButton("Monday");
        monButton.setBounds(xOffset,150,150,150);

        tueButton = new JButton("Tuesday");
        tueButton.setBounds(xOffset+200,150,150,150);

        wedButton = new JButton("Wednesday");
        wedButton.setBounds(xOffset+400,150,150,150);

        thuButton = new JButton("Thursday");
        thuButton.setBounds(xOffset+600,150,150,150);

        friButton = new JButton("Friday");
        friButton.setBounds(xOffset+100,350,150,150);

        //satButton = new JButton("Saturday");
        Recipe satRecipe =
        satButton = new JButton("<html><center>Saturday<br>MEAL</center></html>");
        satButton.setBounds(xOffset+300,350,150,150);


        sunButton = new JButton("Sunday");
        sunButton.setBounds(xOffset+500,350,150,150);

        listButton = new JButton("List");
        listButton.setBounds(75,768-standardHeight-75,150,30);

        clearButton = new JButton("Clear");
        clearButton.setBounds(1366-standardWidth-75,768-standardHeight-75,150,30);

        add(recipeButton);
        add(stockButton);
        add(monButton);
        add(tueButton);
        add(wedButton);
        add(thuButton);
        add(friButton);
        add(satButton);
        add(sunButton);
        add(listButton);
        add(clearButton);

        setVisible(true);
    }
}
