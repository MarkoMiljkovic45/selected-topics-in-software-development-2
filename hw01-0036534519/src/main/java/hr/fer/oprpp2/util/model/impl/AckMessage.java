package hr.fer.oprpp2.util.model.impl;

import hr.fer.oprpp2.util.model.Message;

import java.io.*;

public record AckMessage(long messageNumber, long UID) implements Message {
    public static final byte CODE = 2;

    public byte[] getBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeByte(CODE);
            dos.writeLong(messageNumber);
            dos.writeLong(UID);
            dos.close();
        } catch (IOException ignore) {
            System.out.println("AckMessage getBytes() error");
        }

        return bos.toByteArray();
    }

    public static AckMessage getInstance(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bis);

        try {
            byte code = dis.readByte();
            if (code != CODE) throw new IllegalArgumentException("Wrong byte format");

            long messageNumber = dis.readLong();
            long uid           = dis.readLong();
            dis.close();

            return new AckMessage(messageNumber, uid);
        } catch (IOException io) {
            System.out.println("AckMessage getInstance() error");
            return null;
        }
    }

    @Override
    public byte getCode() {
        return CODE;
    }
}
