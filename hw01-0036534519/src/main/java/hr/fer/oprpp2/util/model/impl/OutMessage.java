package hr.fer.oprpp2.util.model.impl;

import hr.fer.oprpp2.util.model.Message;

import java.io.*;

public record OutMessage(long messageNumber, long UID, String text) implements Message {
    public static final byte CODE = 4;

    public byte[] getBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeByte(CODE);
            dos.writeLong(messageNumber);
            dos.writeLong(UID);
            dos.writeUTF(text);
            dos.close();
        } catch (IOException ignore) {
            System.out.println("OutMessage getBytes() error");
        }

        return bos.toByteArray();
    }

    public static OutMessage getInstance(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bis);

        try {
            byte code = dis.readByte();
            if (code != CODE) throw new IllegalArgumentException("Wrong byte format");

            long   messageNumber = dis.readLong();
            long   UID           = dis.readLong();
            String text          = dis.readUTF();
            dis.close();

            return new OutMessage(messageNumber, UID, text);
        } catch (IOException io) {
            System.out.println("OutMessage getInstance() error");
            return null;
        }
    }

    @Override
    public byte getCode() {
        return CODE;
    }
}
