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
        setTitle("New Window");
        //setSize(1920,1008);
        setSize(1366,768);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        containerWidth = getContentPane().getWidth();
        containerHeight = getContentPane().getHeight();
        setVisible(false);

        lblMessage = new JLabel("New Label");
        lblMessage.setBounds(50, 50, standardWidth, 150);
        lblMessage.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        recipeButton = new JButton("Recipes");
        recipeButton.setBounds(75,50,standardWidth,standardHeight);

        stockButton = new JButton("Stock");
        System.out.println(containerWidth);
        System.out.println(containerWidth-standardWidth-50);
        stockButton.setBounds(containerWidth-standardWidth-75,50,standardWidth,standardHeight);

        monButton = new JButton("Monday");
        monButton.setBounds(75,75,150,30);

        tueButton = new JButton("Tuesday");
        //tueButton.setBounds(75,75,150,30);

        wedButton = new JButton("Wednesday");
        //wedButton.setBounds(75,75,150,30);

        thuButton = new JButton("Thursday");
        //thuButton.setBounds(75,75,150,30);

        friButton = new JButton("Friday");
        //friButton.setBounds(75,75,150,30);

        satButton = new JButton("Saturday");
        //satButton.setBounds(75,75,150,30);

        sunButton = new JButton("Sunday");
        //sunButton.setBounds(75,75,150,30);

        listButton = new JButton("List");
        listButton.setBounds(75,containerHeight-standardHeight-75,150,30);

        clearButton = new JButton("Clear");
        clearButton.setBounds(containerWidth-standardWidth-75,containerHeight-standardHeight-75,150,30);

        // Creating a panel to add buttons
        // and a specific layout
        JPanel p = new JPanel();

        // Adding buttons and textfield to panel
        // using add() method
        p.add(monButton);
        //p.add(tueButton, BorderLayout.SOUTH);
        //p.add(wedButton, BorderLayout.EAST);
        //p.add(thuButton, BorderLayout.WEST);
        //p.add(friButton, BorderLayout.CENTER);

        // setbackground of panel
        p.setBackground(Color.red);
        p.setSize(500, 500);
        p.setLocation(150, 150);

        // Adding panel to frame

        //setLocationByPlatform(true);
        //pack();

        //add(p);

        // Setting the size of frame
        //f.setSize(300, 300);



        add(recipeButton);
        add(stockButton);
        //add(monButton);
        //add(tueButton);
        //add(wedButton);
        //add(thuButton);
        //add(friButton);
        //add(satButton);
        //add(sunButton);
        add(listButton);
        add(clearButton);

        //setContentPane(p);
        add(p);

        setVisible(true);
    }
}
