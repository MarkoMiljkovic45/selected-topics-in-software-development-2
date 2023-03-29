package hr.fer.oprpp2.util.listeners;

public interface ClientListener {

    /**
     * Triggered when a message from the server is received
     * @param message from the server
     */
    void messageReceived(String message);

    /**
     * Triggered when the listener fails to receive an acknowledgment from the server
     */
    void messageAcknowledgementFailed();
}
