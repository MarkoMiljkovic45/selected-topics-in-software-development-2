package hr.fer.oprpp2.util.model.impl;

import hr.fer.oprpp2.util.model.Message;

import java.io.*;

public record HelloMessage(long messageNumber, String username, long randKey) implements Message {
    public static final byte CODE = 1;

    public byte[] getBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeByte(CODE);
            dos.writeLong(messageNumber);
            dos.writeUTF(username);
            dos.writeLong(randKey);
            dos.close();
        } catch (IOException ignore) {
            System.out.println("HelloMessage getBytes() error");
        }

        return bos.toByteArray();
    }

    public static HelloMessage getInstance(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bis);

        try {
            byte code = dis.readByte();
            if (code != CODE) throw new IllegalArgumentException("Wrong byte format");

            long   messageNumber = dis.readLong();
            String username      = dis.readUTF();
            long   randKey       = dis.readLong();
            dis.close();

            return new HelloMessage(messageNumber, username, randKey);
        } catch (IOException io) {
            System.out.println("HelloMessage getInstance() error");
            return null;
        }
    }

    @Override
    public byte getCode() {
        return CODE;
    }
}
