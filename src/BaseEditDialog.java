import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class BaseEditDialog extends JDialog {
    protected JPanel fieldsPanel;
    protected JButton saveButton;
    protected JButton cancelButton;
    protected JButton deleteButton;

    public BaseEditDialog(JFrame parent, String title) {
        super(parent, title, true); // modal
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDelete();
            }
        });
        // Removed populateFields() call from here
    }

    // Subclasses must implement these
    protected abstract void populateFields();
    protected abstract void onSave();
    protected abstract void onDelete();
} 