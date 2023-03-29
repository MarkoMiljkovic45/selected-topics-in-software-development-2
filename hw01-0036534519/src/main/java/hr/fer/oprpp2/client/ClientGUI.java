package hr.fer.oprpp2.client;

import hr.fer.oprpp2.util.listeners.ClientListener;
import hr.fer.oprpp2.util.listeners.UserInputListener;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI extends JFrame {

    private final List<UserInputListener> listeners;
    private final JTextField              clientInput;
    private final JTextArea               textArea;

    public ClientGUI(String username) {
        listeners   = new ArrayList<>();
        clientInput = new JTextField();
        textArea    = new JTextArea();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocation(0, 0);
        setSize(800, 800);
        setTitle("Chat client: " + username);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                listeners.forEach(UserInputListener::closeApplication);
                ClientGUI.this.dispose();
                listeners.clear();
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
            listeners.forEach(l -> l.messageSent(message));
        }
    };

    public void addUserInputListener(UserInputListener listener) {
        listeners.add(listener);
    }

    public ClientListener LISTENER = new ClientListener() {
        @Override
        public void messageReceived(String message) {
            try {
                Document document = textArea.getDocument();
                document.insertString(document.getLength(), message, null);
            }
            catch (BadLocationException ignore) {}
        }

        @Override
        public void messageAcknowledgementFailed() {
            JOptionPane.showMessageDialog(ClientGUI.this, "Connection failed", "Error", JOptionPane.ERROR_MESSAGE);
            ClientGUI.this.dispose();
            listeners.clear();
        }
    };
}