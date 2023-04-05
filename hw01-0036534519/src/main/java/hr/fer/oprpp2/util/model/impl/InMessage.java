package hr.fer.oprpp2.util.model.impl;

import hr.fer.oprpp2.util.model.Message;

import java.io.*;

public record InMessage(long messageNumber, String username, String text) implements Message {
    public static final byte CODE = 5;

    public byte[] getBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeByte(CODE);
            dos.writeLong(messageNumber);
            dos.writeUTF(username);
            dos.writeUTF(text);
            dos.close();
        } catch (IOException ignore) {
            System.out.println("InMessage getBytes() error");
        }

        return bos.toByteArray();
    }

    public static InMessage getInstance(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bis);

        try {
            byte code = dis.readByte();
            if (code != CODE) throw new IllegalArgumentException("Wrong byte format");

            long   messageNumber = dis.readLong();
            String username      = dis.readUTF();
            String text          = dis.readUTF();
            dis.close();

            return new InMessage(messageNumber, username, text);
        } catch (IOException io) {
            System.out.println("InMessage getInstance() error");
            return null;
        }
    }

    public byte getCode() {
        return CODE;
    }
}
