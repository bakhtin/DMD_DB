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
    public void testTable() throws Exception {
        Table t = new Table();
        t.rootpage = 30;
        t.recordsTotal = 100500;
        t.tbl_name = "table";

        t.attributes = new Attribute[3];
        t.attributes[0] = new Attribute("attr1");
        t.attributes[1] = new Attribute("attr2");
        t.attributes[2] = new Attribute("attr3");
        t.attributes[2].setFlag(Attribute.F_AI);

        ByteBuffer b = t.serialize();

        Table tc = Table.deserialize(b);

        if (t.rootpage != tc.rootpage) throw new Exception("TABLE: rootpage");
        if (t.recordsTotal != tc.recordsTotal) throw new Exception("TABLE: recordsTotal");
        if (t.attributes.length != tc.attributes.length) throw new Exception("TABLE: attr len");
        for (int i = 0; i < t.attributes.length; i++) {
            if (!t.attributes[i].name.equals(tc.attributes[i].name)) throw new Exception("TABLE: attr" + i + " name");
            if (t.attributes[i].flags != tc.attributes[i].flags) throw new Exception("TABLE: attr" + i + " flags");
        }
    }

    @Test
    public void testRecord() throws Exception {
        Record r = new Record();
        r.type = 1;
        r.payload = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        r.record_length = 100;
        r.forward_overfow = 15;
        r.backward_overflow = 1000;
        r.rowid = 100500;

        ByteBuffer b = r.serialize();

        Record rc = Record.deserialize(b);

        if (r.type != rc.type) throw new Exception("RECORD: types are not equal");
        if (r.rowid != rc.rowid) throw new Exception("RECORD: rowids are not equal");
        if (r.backward_overflow != rc.backward_overflow)
            throw new Exception("RECORD: backward overflows are not equal");
        if (r.forward_overfow != rc.forward_overfow) throw new Exception("RECORD: forward overflows are not equal");
        if (r.record_length != rc.record_length) throw new Exception("RECORD: record lengths are not equal");
        if (!Misc.compareBytes(r.payload, rc.payload)) throw new Exception("RECORD: payloads are not equal");
    }

    @Test
    public void testPage() throws Exception {
        Page p = new Page(15);
        p.previous = 50;
        p.next = 61;
        p.data.put(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
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
