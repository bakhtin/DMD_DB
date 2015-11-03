package core.sys.util;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class Misc {
    public static String parseStr(ByteBuffer b) {
        short len = b.getShort();
        byte[] str = new byte[len];
        b.get(str);
        return new String(str);
    }

    public static byte[] parseBytes(ByteBuffer b, int length) {
        byte[] buf = new byte[length];
        b.get(buf);
        return buf;
    }

    /**
     * Add string s to buffer b
     *
     * @param b - ByteBuffer
     * @param s - String
     */
    public static void addStr(ByteBuffer b, String s) {
        byte[] str = s.getBytes();
        b.putShort((short) str.length);
        b.put(str);
    }

    /**
     * Compares wto byte arrays.
     *
     * @param a - byte array
     * @param b - byte array
     * @return - true if arrays a and b are equal
     */
    public static boolean compareBytes(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

}
