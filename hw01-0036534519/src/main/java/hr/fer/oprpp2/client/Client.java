package hr.fer.oprpp2.client;

import hr.fer.oprpp2.util.Util;
import hr.fer.oprpp2.util.listeners.ClientListener;
import hr.fer.oprpp2.util.listeners.UserInputListener;
import hr.fer.oprpp2.util.model.Message;
import hr.fer.oprpp2.util.model.impl.*;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Client {
    private final DatagramSocket socket;
    private final InetSocketAddress serverAddress;
    private final String username;
    private final long UID;
    private long messageNumber;
    private final BlockingQueue<Message> sendQueue;
    private final BlockingQueue<AckMessage> ackQueue;
    private final BlockingQueue<Message> unacknowledgedMessages;
    private final List<ClientListener> listeners;
    private boolean alive;

    private static final int      ACK_TIMEOUT           = 5000;
    private static final TimeUnit ACK_TIMEOUT_TIME_UNIT = TimeUnit.MILLISECONDS;
    private static final int      RETRANSMISSION_LIMIT  = 10;
    private static final int      RECEIVE_BUFFER_SIZE   = 1024;

    public Client(String serverIp, int serverPort, String username) {
        try {
            this.socket        = new DatagramSocket();
            this.serverAddress = new InetSocketAddress(InetAddress.getByName(serverIp), serverPort);
        }
        catch (SocketException socketException) { throw new IllegalStateException("Unable to open a UDP connection: " + socketException.getMessage()); }
        catch (UnknownHostException unknownHostException) { throw new IllegalArgumentException("Unable to connect to " + serverIp); }

        this.username               = username;
        this.messageNumber          = 0;
        this.UID                    = getUIDFromServer();
        this.sendQueue              = new LinkedBlockingQueue<>();
        this.ackQueue               = new LinkedBlockingQueue<>();
        this.unacknowledgedMessages = new LinkedBlockingQueue<>();
        this.listeners              = new ArrayList<>();
        this.alive                  = false;
    }

    public void start() {
        setAlive(true);

        Thread sendThread = new Thread(new SendMessageJob());
        sendThread.setDaemon(true);
        sendThread.start();

        Thread receiveThread = new Thread(new ReceiveMessageJob());
        receiveThread.setDaemon(true);
        receiveThread.start();

        while(alive) {
            acknowledgeMessage();
        }

        endConnection();
    }

    public String getUsername() {
        return username;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void addClientListener(ClientListener listener) {
        listeners.add(listener);
    }

    private long messageNumber() {
        return messageNumber++;
    }

    private long getUIDFromServer() {
        Random       random  = new Random();
        long         randKey = random.nextLong();
        HelloMessage hello   = new HelloMessage(messageNumber(), username, randKey);

        try {
            socket.setSoTimeout(ACK_TIMEOUT);

            for (int i = 0; i < RETRANSMISSION_LIMIT; i++) {
                Message response = sendAndWaitForMessage(hello);

                if (response != null && response.getCode() == AckMessage.CODE) {
                    AckMessage ack = (AckMessage) response;
                    return ack.UID();
                }
            }

            socket.setSoTimeout(0);
        }
        catch (SocketException ignore) { System.out.println("GetUIDFromServer SocketException"); }

        throw new IllegalStateException("Failed to connect to server");
    }

    private void endConnection() {
        ByeMessage bye = new ByeMessage(messageNumber(), UID);

        try {
            socket.setSoTimeout(ACK_TIMEOUT);

            for (int i = 0; i < RETRANSMISSION_LIMIT; i++) {
                Message response = sendAndWaitForMessage(bye);

                if (response != null && response.getCode() == AckMessage.CODE) break;
            }
        } catch (SocketException e) {
            sendMessage(bye);
        }
    }

    private void acknowledgeMessage() {
        try {
            Message oldest     = unacknowledgedMessages.take();
            int retransmission = 0;

            while (retransmission < RETRANSMISSION_LIMIT) {
                retransmission++;

                AckMessage ack = ackQueue.poll(ACK_TIMEOUT, ACK_TIMEOUT_TIME_UNIT);

                if (ack == null) {
                    sendMessage(oldest);
                    continue;
                }

                if (oldest.messageNumber() == ack.messageNumber()) {
                    break;
                }

                unacknowledgedMessages.removeIf(message -> message.messageNumber() == ack.messageNumber());
                sendMessage(oldest);
            }

            if (retransmission == RETRANSMISSION_LIMIT) {
                setAlive(false);
                listeners.forEach(ClientListener::messageAcknowledgementFailed);
                listeners.clear();
            }
        }
        catch (InterruptedException ignore) {
            System.out.println("Message acknowledging interrupted");
        }
    }

    /**
     * Sends a message to the connected server and waits for the response
     * @param message to be sent
     * @return response from server
     */
    private Message sendAndWaitForMessage(Message message) {
        sendMessage(message);
        return waitForResponse();
    }

    /**
     * Sends a message to the connected server
     * @param message to be sent
     */
    private void sendMessage(Message message) {
        try {
            byte[] payload = message.getBytes();
            DatagramPacket packet = new DatagramPacket(payload, payload.length, serverAddress);
            socket.send(packet);
        }
        catch (IOException ignore) { System.out.println("SendMessage IOException"); }
    }

    /**
     * Waits for connected server to respond
     * @return Next message from server
     */
    private Message waitForResponse() {
        try {
            byte[] receiveBuff = new byte[RECEIVE_BUFFER_SIZE];
            DatagramPacket response = new DatagramPacket(receiveBuff, receiveBuff.length);
            socket.receive(response);

            return Util.parseBytes(response.getData(), response.getOffset(), response.getLength());
        }
        catch (IOException ignore) { }

        return null;
    }

    private String messageToString(InMessage in) {

        return "[" + serverAddress + "] Poruka od korisnika: " + in.username() + "\n" +
                in.text() + "\n";
    }

    public UserInputListener LISTENER = new UserInputListener() {
        @Override
        public void messageSent(String text) {
            OutMessage out = new OutMessage(messageNumber(), UID, text);
            sendQueue.add(out);
        }

        @Override
        public void closeApplication() {
            setAlive(false);
            listeners.clear();
            endConnection();
            System.exit(0);
        }
    };

    private class SendMessageJob implements Runnable {
        @Override
        public void run() {
            while (alive) {
                try {
                    Message message = sendQueue.take();

                    sendMessage(message);

                    if (message.getCode() != AckMessage.CODE) {
                        unacknowledgedMessages.add(message);
                    }
                }
                catch (InterruptedException ignore) { System.out.println("SendMessageJob Interrupted Exception"); }
            }
        }
    }

    private class ReceiveMessageJob implements Runnable {
        @Override
        public void run() {
            while(alive) {
                Message message = waitForResponse();

                if (message == null) {
                    continue;
                }

                if (message.getCode() == AckMessage.CODE) {
                    ackQueue.add((AckMessage) message);
                }

                if (message.getCode() == InMessage.CODE) {
                    listeners.forEach(l -> l.messageReceived(messageToString((InMessage) message)));
                    sendQueue.add(new AckMessage(message.messageNumber(), UID));
                }
            }
        }
    }
}
