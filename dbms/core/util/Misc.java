package core.util;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class Misc {
    /**
     * Read string from buffer b
     *
     * @param b - ByteBuffer
     * @return - String
     */
    public static String parseStr(ByteBuffer b) {
        short len = b.getShort();
        byte[] str = new byte[len];
        b.get(str);
        return new String(str);
    }

    /**
     * Read bytes from the ByteBuffer
     *
     * @param b - ByteBuffer
     * @return - byte array
     */
    public static byte[] parseBytes(ByteBuffer b) {
        short length = b.getShort();
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
        addBytes(b, s.getBytes());
    }

    /**
     * Add byte array to buffer b
     *
     * @param b     - ByteBuffer
     * @param bytes - byte array
     */
    public static void addBytes(ByteBuffer b, byte[] bytes) {
        b.putShort((short) bytes.length);
        b.put(bytes);
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

    public static byte[] subbyte(byte[] array, int start, int end) {
        if (end < start) throw new IllegalStateException("End > Start");
        if (start == 0 && end == array.length) return array;

        byte[] result = new byte[end - start];
        for (int i = start; i < end; i++) {
            result[i - start] = array[i];
        }
        return result;
    }

    public static int sizeof(Object obj) throws Exception {
        if (obj instanceof String) return 2 + ((String) obj).getBytes().length;
        else if (obj instanceof byte[]) return 2 + ((byte[]) obj).length;
        else if (obj instanceof Integer) return 4;
        else if (obj instanceof Short) return 2;
        else if (obj instanceof Float) return 4;
        else throw new Exception("Wrong type");
    }

}
