package core.sys.parse;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class Misc {
    public static String parseStr(ByteBuffer b) {
        short len = b.getShort();
        byte [] str = new byte[len];
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
     * @param b
     * @param s
     */
    public static void addStr(ByteBuffer b, String s) {
        byte[] str = s.getBytes();
        b.putShort((short)str.length);
        b.put(str);
    }

}
