package hr.fer.oprpp2;

import hr.fer.oprpp2.util.Util;
import hr.fer.oprpp2.util.model.Message;
import hr.fer.oprpp2.util.model.impl.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Server {

    private DatagramSocket socket;
    private final AtomicLong nextUID;
    private final ConcurrentMap<Long, Session> sessions;

    private static final int      ACK_TIMEOUT           = 5000;
    private static final TimeUnit ACK_TIMEOUT_TIME_UNIT = TimeUnit.MILLISECONDS;
    private static final int      RETRANSMISSION_LIMIT  = 10;
    private static final int      RECEIVE_BUFFER_SIZE   = 1024;

    public Server() {
        this.nextUID  = new AtomicLong(new Random().nextLong());
        this.sessions = new ConcurrentHashMap<>();
    }

    public void bindToPort(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    public void start() {
        while(true) {
            byte[]         buff          = new byte[RECEIVE_BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(buff, RECEIVE_BUFFER_SIZE);

            while (true) {
                try {
                    socket.receive(receivePacket);
                    break;
                } catch (IOException ignore) {}
            }

            Message message = Util.parseBytes(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

            try {
                switch (message.getCode()) {
                    case HelloMessage.CODE -> handleHello((HelloMessage) message, receivePacket.getSocketAddress());
                    case AckMessage.CODE   -> handleAck((AckMessage) message);
                    case ByeMessage.CODE   -> handleBye((ByeMessage) message);
                    case OutMessage.CODE   -> handleOut((OutMessage) message);
                }
            }
            catch (Exception ignore) {}
        }
    }

    private void handleHello(HelloMessage hello, SocketAddress address) throws IOException {
        Session session = findByRandKey(hello.randKey());

        if (session == null) {
            session = new Session(this, hello.randKey(), nextUID.incrementAndGet(), hello.username(), address);
            sessions.put(session.UID, session);
            new Thread(session).start();
        }

        AckMessage ack = new AckMessage(0, session.UID);
        byte[] payload = ack.getBytes();
        DatagramPacket packet = new DatagramPacket(payload, payload.length, address);
        socket.send(packet);
    }

    private void handleAck(AckMessage ack) {
        Session session = sessions.get(ack.UID());

        if (session != null) {
            session.receiveQueue.add(ack);
        }
    }

    private void handleBye(ByeMessage bye) throws IOException {
        Session session = sessions.get(bye.UID());

        if (session == null) return;

        session.setValid(false);

        AckMessage ack = new AckMessage(bye.messageNumber(), session.UID);
        byte[] payload = ack.getBytes();
        DatagramPacket packet = new DatagramPacket(payload, payload.length, session.address);
        socket.send(packet);

    }

    private void handleOut(OutMessage out) throws IOException {
        Session session = sessions.get(out.UID());

        if (session == null) return;
        if (!session.valid)  return;

        sendInMessages(session.username, out.text());

        AckMessage ack = new AckMessage(out.messageNumber(), session.UID);
        byte[] payload = ack.getBytes();
        DatagramPacket packet = new DatagramPacket(payload, payload.length, session.address);
        socket.send(packet);
    }

    private void sendInMessages(String sender, String text) {
        for (Session session: sessions.values()) {
            session.sendQueue.add(new InMessage(session.getMessageNumber(), sender , text));
        }
    }

    private Session findByRandKey(long randKey) {
        for (Session session: sessions.values()) {
            if (session.randKey == randKey) {
                return session;
            }
        }
        return null;
    }

    private static class Session implements Runnable {
        private final Server server;
        private volatile boolean valid;
        private long messageNumber;
        private final long randKey;
        private final long UID;
        private final String username;
        private final SocketAddress address;
        private final BlockingQueue<Message> sendQueue;
        private final BlockingQueue<Message> receiveQueue;

        public Session(Server server, long randKey, long UID, String username, SocketAddress address) {
            this.server        = server;
            this.valid         = true;
            this.messageNumber = 1;
            this.randKey       = randKey;
            this.UID           = UID;
            this.username      = username;
            this.address       = address;
            this.sendQueue     = new LinkedBlockingQueue<>();
            this.receiveQueue  = new LinkedBlockingQueue<>();
        }

        public long getMessageNumber() {
            return messageNumber++;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        @Override
        public void run() {
            while(valid) {
                try {
                    Message message = sendQueue.take();
                    sendMessage(message);
                }
                catch (InterruptedException ignore) { }
            }

            server.sessions.remove(UID);
        }

        private void sendMessage(Message message) {
            byte[] payload = message.getBytes();
            DatagramPacket packet = new DatagramPacket(payload, payload.length, address);

            int retransmissions = 0;
            while (true) {

                while (true) {
                    retransmissions++;
                    try {
                        server.socket.send(packet);
                        break;
                    }
                    catch (IOException e) {
                        if (retransmissions > RETRANSMISSION_LIMIT) {
                            setValid(false);
                            return;
                        }
                    }
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
