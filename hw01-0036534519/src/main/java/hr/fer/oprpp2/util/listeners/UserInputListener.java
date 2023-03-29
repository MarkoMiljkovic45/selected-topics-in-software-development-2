package hr.fer.oprpp2.util.listeners;

public interface UserInputListener {

    /**
     * Triggered when the user wants to send a message
     * @param text The message
     */
    void messageSent(String text);

    /**
     * Triggers when the user wants to exit the application
     */
    void closeApplication();
}
