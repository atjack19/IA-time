import javax.swing.*;

public class GUI extends JFrame {
    JLabel lblMessage;

    public GUI() {
        setTitle("New Window");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        lblMessage = new JLabel("New Label");
        lblMessage.setBounds(50, 50, 150, 30);
    }
}
