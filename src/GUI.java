import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    JLabel lblMessage;
    JButton button;

    public GUI() {
        setTitle("New Window");
        setSize(600,400);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        lblMessage = new JLabel("New Label");
        lblMessage.setBounds(50, 50, 150, 150);
        lblMessage.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        button = new JButton("bazinga");
        button.setBounds(75,75,150,30);

        button = new JButton("bazinga");
        button.setBounds(75,75,150,30);

        button = new JButton("bazinga");
        button.setBounds(75,75,150,30);

        button = new JButton("bazinga");
        button.setBounds(75,75,150,30);

        button = new JButton("bazinga");
        button.setBounds(75,75,150,30);

        button = new JButton("bazinga");
        button.setBounds(75,75,150,30);

        button = new JButton("bazinga");
        button.setBounds(75,75,150,30);

        button = new JButton("bazinga");
        button.setBounds(75,75,150,30);

        button = new JButton("bazinga");
        button.setBounds(75,75,150,30);

        button = new JButton("bazinga");
        button.setBounds(75,75,150,30);

        button = new JButton("bazinga");
        button.setBounds(75,75,150,30);

        add(lblMessage);

    }
}
