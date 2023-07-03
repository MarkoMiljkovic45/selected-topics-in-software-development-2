package hr.fer.oprpp2.jmbag0036534519.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A class containing static utility methods
 *
 * @author Marko Miljković (miljkovicmarko45@gmail.com)
 */
public class Util {

    /**
     * This method takes hex-encoded String and returns appropriate byte array. If the string is
     * not valid (odd-sized, has invalid characters, …) method throws an IllegalArgumentException.
     * <p>
     * For zero-length string, method will return zero-length byte array.
     * <p>
     * Method hexToByte must support both uppercase letters and lowercase letters.
     *
     * @param keyText Hex-encoded string
     *
     * @return Byte array containing hex values from keyText
     *
     * @throws IllegalArgumentException if keyText is invalid
     */
    public static byte[] hexToByte(String keyText) {
        int keyTextLen = keyText.length();

        if (keyTextLen % 2 != 0)
            throw new IllegalArgumentException("Invalid argument for hexToByte: " + keyText);

        byte[] arr = new byte[keyTextLen / 2];

        try {
            int j = 0;
            for (int i = 0; i <= keyTextLen - 2; i += 2) {
                arr[j++] = (byte) Integer.parseInt(keyText.substring(i, i+2), 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }

        return arr;
    }

    /**
     * Method byteToHex takes a byte array and creates its hex-encoding: for each byte of given
     * array, two characters are returned in string, in big-endian notation.
     * <p>
     * For zero-length array an empty string is returned.
     * <p>
     * Method byteToHex uses lowercase letters.
     *
     * @param byteArray Array of bytes to be turned into string
     *
     * @return Hex-encoded bytes from byteArray in String form
     */
    public static String byteToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();

        for (byte b: byteArray) {
            if (b >= 0 && b <= 15)
                sb.append("0");

            sb.append(Integer.toHexString(Byte.toUnsignedInt(b)));
        }

        return sb.toString();
    }

    public static String getSHA1Digest(String plain) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] plainBytes = plain.getBytes();
            byte[] digest = sha1.digest(plainBytes);
            return byteToHex(digest);
        }
        catch (NoSuchAlgorithmException ignore) {
            return "Couldn't create SHA-1 instance";
        }
    }
}
