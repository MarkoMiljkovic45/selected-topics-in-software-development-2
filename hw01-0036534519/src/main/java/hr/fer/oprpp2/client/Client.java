package hr.fer.oprpp2.client;

import hr.fer.oprpp2.util.Util;
import hr.fer.oprpp2.util.listeners.ClientListener;
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
    private long mostRecentServerInMessageNumber;
    private volatile boolean alive;

    private final Session session;
    private final List<ClientListener> listeners;

    private static final int      ACK_TIMEOUT           = 5000;
    private static final TimeUnit ACK_TIMEOUT_TIME_UNIT = TimeUnit.MILLISECONDS;
    private static final int      RETRANSMISSION_LIMIT  = 10;
    private static final int      RECEIVE_BUFFER_SIZE   = 1024;

    public Client(String serverIp, int serverPort, String username) {
        try {
            this.socket        = new DatagramSocket();
            this.serverAddress = new InetSocketAddress(InetAddress.getByName(serverIp), serverPort);

            socket.setSoTimeout(ACK_TIMEOUT);
        }
        catch (SocketException socketException) { throw new IllegalStateException("Unable to open a UDP connection: " + socketException.getMessage()); }
        catch (UnknownHostException unknownHostException) { throw new IllegalArgumentException("Unable to connect to " + serverIp); }

        this.username      = username;
        this.messageNumber = 0;
        this.UID           = getUIDFromServer();
        this.listeners     = new ArrayList<>();
        this.session       = new Session();
        this.alive         = false;

        this.mostRecentServerInMessageNumber = 0;
    }

    public void start() {
        setAlive(true);

        new Thread(session).start();

        while(alive) {
            Message message = waitForResponse();

            if (message == null) continue;

            try {
                switch (message.getCode()) {
                    case InMessage.CODE  -> handleIn((InMessage) message);
                    case AckMessage.CODE -> handleAck((AckMessage) message);
                }
            }
            catch (Exception ignore) {}
        }

        session.sendQueue.add(new ByeMessage(getMessageNumber(), UID));
        listeners.clear();
    }

    private void handleIn(InMessage in) {
        long serverInNumber = in.messageNumber();

        if (mostRecentServerInMessageNumber < serverInNumber) {
            listeners.forEach(l -> l.messageReceived(messageToString(in)));
            setMostRecentServerInMessageNumber(serverInNumber);
        }

        sendMessage(new AckMessage(serverInNumber, UID));
    }

    private void handleAck(AckMessage ack) {
        session.receiveQueue.add(ack);
    }

    public String getUsername() {
        return username;
    }

    public void setAlive(boolean alive) {
        if (!alive) {
            listeners.forEach(ClientListener::closeConnection);
        }

        this.alive = alive;
    }

    public void setMostRecentServerInMessageNumber(long serverInNumber) {
        if (serverInNumber > mostRecentServerInMessageNumber) {
            mostRecentServerInMessageNumber = serverInNumber;
        }
    }

    public void addClientListener(ClientListener listener) {
        listeners.add(listener);
    }

    public void removeClientListener(ClientListener listener) {
        listeners.remove(listener);
    }

    private long getMessageNumber() {
        return messageNumber++;
    }

    private long getUIDFromServer() {
        Random       random  = new Random();
        long         randKey = random.nextLong();
        HelloMessage hello   = new HelloMessage(getMessageNumber(), username, randKey);

        for (int i = 0; i < RETRANSMISSION_LIMIT; i++) {
            Message response = sendAndWaitForMessage(hello);

            if (response != null && response.getCode() == AckMessage.CODE) {
                AckMessage ack = (AckMessage) response;
                return ack.UID();
            }
        }

        throw new IllegalStateException("Failed to connect to server");
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

    public void sendMessage(String text) {
        OutMessage out = new OutMessage(getMessageNumber(), UID, text);
        session.sendQueue.add(out);
    }


    private class Session implements Runnable {
        private final BlockingQueue<Message> sendQueue;
        private final BlockingQueue<Message> receiveQueue;

        public Session() {
            this.sendQueue    = new LinkedBlockingQueue<>();
            this.receiveQueue = new LinkedBlockingQueue<>();
        }

        @Override
        public void run() {
            while(alive) {
                try {
                    Message message = sendQueue.take();
                    sendMessage(message);
                }
                catch (InterruptedException ignore) { }
            }
        }

        private void sendMessage(Message message) {
            int retransmissions = 0;

            while (true) {
                retransmissions++;

                Client.this.sendMessage(message);

                if (retransmissions > RETRANSMISSION_LIMIT) {
                    setAlive(false);
                    return;
                }

                try {
                    Message response = receiveQueue.poll(ACK_TIMEOUT, ACK_TIMEOUT_TIME_UNIT);

                    if (response == null) {
                        continue;
                    }

                    if (response.getCode() != AckMessage.CODE) {
                        continue;
                    }

                    if (response.messageNumber() == message.messageNumber()) {
                        return;
                    }
                }
                catch (InterruptedException ignore) {}
            }
        }
    }
}
