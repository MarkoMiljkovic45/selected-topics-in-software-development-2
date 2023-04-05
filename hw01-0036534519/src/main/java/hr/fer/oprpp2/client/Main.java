package hr.fer.oprpp2.client;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Invalid number of arguments.");
            return;
        }

        try {
            String serverIp   = args[0];
            int    serverPort = Integer.parseInt(args[1]);
            String username   = args[2];

            Client client = new Client(serverIp, serverPort, username);
            SwingUtilities.invokeLater(() -> new ClientGUI(client).setVisible(true));
            client.start();
        }
        catch (Exception e) { System.out.println("An error occurred: " + e.getMessage()); }
    }
}
