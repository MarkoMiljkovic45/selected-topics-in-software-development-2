package hr.fer.oprpp2.client;

import hr.fer.oprpp2.util.listeners.ClientListener;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientGUI extends JFrame implements ClientListener {

    private final Client client;
    private final JTextField clientInput;
    private final JTextArea textArea;

    public ClientGUI(Client client) {
        this.client = client;
        clientInput = new JTextField();
        textArea    = new JTextArea();

        client.addClientListener(this);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocation(0, 0);
        setSize(800, 800);
        setTitle("Chat client: " + client.getUsername());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ClientGUI.this.dispose();
                client.removeClientListener(ClientGUI.this);
                client.setAlive(false);
            }
        });

        initGUI();
    }

    private void initGUI() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        sendMessage.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        clientInput.addActionListener(sendMessage);
        contentPane.add(clientInput, BorderLayout.PAGE_START);

        textArea.setFocusable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private final Action sendMessage = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = clientInput.getText();
            clientInput.setText("");
            client.sendMessage(message);
        }
    };

    @Override
    public void messageReceived(String message) {
        try {
            Document document = textArea.getDocument();
            document.insertString(document.getLength(), message, null);
        }
        catch (BadLocationException ignore) {}
    }

    @Override
    public void closeConnection() {
        JOptionPane.showMessageDialog(ClientGUI.this, "Connection failed", "Error", JOptionPane.ERROR_MESSAGE);
        dispose();
    }
}
