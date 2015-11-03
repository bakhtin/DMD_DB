package core.sys.descriptive;

import core.sys.util.Misc;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/3/2015
 */
public class SerializationTest {
    @Test
    public void testPage() throws Exception {
        Page p = new Page(15);
        p.previous = 50;
        p.next = 61;
        p.data = ByteBuffer.wrap(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        p.type = 3;
        p.numberOfRecords = 100500;

        ByteBuffer b = p.serialize();

        Page pc = Page.deserialize(b);

        if (p.number != pc.number) throw new Exception("PAGE: page numbers are not equal");
        if (p.type != pc.type) throw new Exception("PAGE: page types are not equal");
        if (p.numberOfRecords != pc.numberOfRecords) throw new Exception("PAGE: number of records are not equal");
        if (p.previous != pc.previous) throw new Exception("PAGE: previous are not equal");
        if (p.next != pc.next) throw new Exception("PAGE: next are not equal");
        if (p.data.equals(pc.data)) throw new Exception("PAGE: data are not equal");
    }


    @Test
    public void testAttribute() throws Exception {
        Attribute a = new Attribute("hello world my name is Attribute");
        a.setDefaultValue(new byte[]{1, 2, 3, 4});
        a.setFlag(Attribute.F_PK);
        a.setType((byte) 0);

        ByteBuffer b = a.serialize();

        Attribute ac = Attribute.deserialize(b);

        if (!a.name.equals(ac.name)) throw new Exception("ATTR: Not equal names");
        if (a.type != ac.type) throw new Exception("ATTR: Not equal types");
        if (a.flags != ac.flags) throw new Exception("ATTR: Not equal flags");
        if (!Misc.compareBytes(a.defaultValue, ac.defaultValue)) throw new Exception("ATTR: Not equal default value");
        if (a.fk != null && a.fk.equals(ac.fk)) throw new Exception("ATTR: Not equal foreign key");
    }
}
