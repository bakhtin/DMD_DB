package core.sys.parse;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class Misc {
    public static String parseStr(ByteBuffer b) {
        int len = b.getInt();
        Character[] str = new Character[len];
        for (int i = 0; i < len; i++) {
            str[i] = b.getChar();
        }
        return String.valueOf(str);
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
        b.putInt(s.length());
        for (int i = 0; i < s.length(); i++) {
            b.putChar(s.charAt(i));
        }
    }

}
