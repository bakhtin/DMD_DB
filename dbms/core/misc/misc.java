package core.misc;

import java.nio.ByteBuffer;

/**
 * Created by ִלטענטי on 24.10.2015.
 */
public class misc {

    public static int parseInt(ByteBuffer b) {
        return b.getInt();
    }

    public static byte parseByte(ByteBuffer b) {
        return b.get();
    }
    public static long parseLong(ByteBuffer b) {
        return b.getLong();
    }
    public static String parseString(ByteBuffer b, int l) {
        char[] c = new char[l];
        for (int i = 0; i <l ; i++) {
            c[i] =  (char)b.get();
        }
        return new String(c);
    }

    public static float parseFloat(ByteBuffer b) {
        return b.getFloat();
    }

}
