package hr.fer.oprpp2.util.listeners;

import hr.fer.oprpp2.util.model.Message;

public interface ServerListener {

    /**
     * Triggered when a message from the server is received
     * @param message from the server
     */
    void messageReceived(Message message);

    /**
     * Triggered when the listener fails to receive an acknowledgment from the server
     */
    void messageAcknowledgementFailed();
}
