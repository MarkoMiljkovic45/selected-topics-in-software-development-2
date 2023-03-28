package hr.fer.oprpp2.util;

import hr.fer.oprpp2.util.model.Message;
import hr.fer.oprpp2.util.model.impl.*;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Util {
    public static Message parseBytes(byte[] bytes) {
        if (bytes.length < 1) throw new IllegalArgumentException("Can't parse empty bytes array");

        byte messageCode = bytes[0];

        return switch (messageCode) {
            case HelloMessage.CODE -> HelloMessage.getInstance(bytes);
            case AckMessage.CODE -> AckMessage.getInstance(bytes);
            case ByeMessage.CODE -> ByeMessage.getInstance(bytes);
            case InMessage.CODE -> InMessage.getInstance(bytes);
            case OutMessage.CODE -> OutMessage.getInstance(bytes);
            default -> null;
        };
    }
}
