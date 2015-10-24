package core.misc;

import junit.framework.TestCase;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Created by ִלטענטי on 24.10.2015.
 */
public class miscTest extends TestCase {
    @Test
    public void testMisc() throws Exception {

        ByteBuffer buf1 = ByteBuffer.allocate(128);

        buf1.putInt(123);
        buf1.put((byte) 12);
        buf1.putLong((long) 123);
        buf1.putFloat((float) 12.3);
        String x = "lololol!!!";
        buf1.putInt(x.length());
        buf1.put(x.getBytes());

        buf1.position(0);

        System.out.println(misc.parseInt(buf1));
        System.out.println(misc.parseByte(buf1));
        System.out.println(misc.parseLong(buf1));
        System.out.println(misc.parseFloat(buf1));
        System.out.println(misc.parseString(buf1,misc.parseInt(buf1)));



    }
}