package hr.fer.oprpp2.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientGUI extends JFrame {

    private JTextField clientInput;
    private JTextArea textArea;

    public ClientGUI(Client client) {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocation(0, 0);
        setSize(800, 800);
        setTitle("Chat client: " + client.getUsername());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.getSocket().close();
                ClientGUI.this.dispose();
            }
        });

        initGUI();
    }

    private void initGUI() {
        Container contentPane = getContentPane();

        contentPane.setLayout(new BorderLayout());

        clientInput = new JTextField();
        contentPane.add(clientInput, BorderLayout.PAGE_START);

        textArea = new JTextArea();
        contentPane.add(textArea, BorderLayout.CENTER);
    }

    private void errorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
