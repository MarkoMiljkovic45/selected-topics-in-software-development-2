package hr.fer.oprpp2.server;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid number of arguments.");
            return;
        }

        try {
            int port = Integer.parseInt(args[0]);
            Server server = new Server();
            server.bindToPort(port);
            server.start();
        }
        catch (Exception e) { System.out.println("An error occurred: " + e.getMessage()); }
    }
}
