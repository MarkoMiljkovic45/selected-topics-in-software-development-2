package hr.fer.oprpp2.client;

import hr.fer.oprpp2.util.Util;
import hr.fer.oprpp2.util.model.Message;
import hr.fer.oprpp2.util.model.impl.AckMessage;
import hr.fer.oprpp2.util.model.impl.HelloMessage;

import java.io.IOException;
import java.net.*;
import java.util.Random;

public class Client {
    private final DatagramSocket socket;
    private final InetSocketAddress serverAddress;
    private final String username;
    private final long UID;
    private long messageNumber;

    private static final int SOCKET_TIMEOUT = 5000;
    private static final int RETRANSMISSION_LIMIT = 10;

    public Client(String serverIp, int serverPort, String username) {
        try {
            this.socket        = new DatagramSocket();
            this.serverAddress = new InetSocketAddress(InetAddress.getByName(serverIp), serverPort);

            socket.setSoTimeout(SOCKET_TIMEOUT);
        }
        catch (SocketException socketException) { throw new IllegalStateException("Unable to open a UDP connection: " + socketException.getMessage()); }
        catch (UnknownHostException unknownHostException) { throw new IllegalArgumentException("Unable to connect to " + serverIp); }

        this.username = username;
        this.messageNumber = 0;
        this.UID = getUIDFromServer();
    }

    public String getUsername() {
        return username;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    private long messageNumber() {
        return messageNumber++;
    }

    private long getUIDFromServer() {
        Random random  = new Random();
        long   randKey = random.nextLong();

        HelloMessage hello           = new HelloMessage(messageNumber(), username, randKey);
        for (int i = 0; i < RETRANSMISSION_LIMIT; i++) {
            Message response = sendMessage(hello);

            if (response != null && response.getCode() == AckMessage.CODE) {
                AckMessage ack = (AckMessage) response;
                return ack.UID();
            }
        }

        throw new IllegalStateException("Failed to connect to server");
    }

    /**
     * Sends a message to the connected server
     * @param message to be sent
     * @return response from server
     */
    private Message sendMessage(Message message) {
        try {
            byte[] payload = message.getBytes();
            DatagramPacket packet = new DatagramPacket(payload, payload.length, serverAddress);
            socket.send(packet);

            byte[] receiveBuff = new byte[1024];
            DatagramPacket response = new DatagramPacket(receiveBuff, receiveBuff.length);
            socket.receive(response);

            return Util.parseBytes(response.getData());
        }
        catch (IOException ignore) {}

        return null;
    }

}
