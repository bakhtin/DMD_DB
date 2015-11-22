package core.descriptive;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/3/2015
 */
public class SerializationTest {
    /*

    @Test
    public void testTable() throws Exception, SQLError {
        TableSchema t = new TableSchema();
        t.recordsTotal = 100500;
        t.tbl_name = "table";

        t.attributes = new Attribute[3];
        t.attributes[0] = new Attribute("attr1", Attribute.T_SHORT);
        t.attributes[1] = new Attribute("attr2", Attribute.T_SHORT);
        t.attributes[2] = new Attribute("attr3", Attribute.T_SHORT);
        t.attributes[2].setFlag(Attribute.F_AI);

        ByteBuffer b = t.serialize();

        TableSchema tc = TableSchema.deserialize(b);

        if (t.recordsTotal != tc.recordsTotal) throw new Exception("TABLE: recordsTotal");
        if (t.attributes.length != tc.attributes.length) throw new Exception("TABLE: attr len");
        for (int i = 0; i < t.attributes.length; i++) {
            if (!t.attributes[i].name.equals(tc.attributes[i].name)) throw new Exception("TABLE: attr" + i + " name");
            if (t.attributes[i].flags != tc.attributes[i].flags) throw new Exception("TABLE: attr" + i + " flags");
        }
    }

    @Test
    public void testRecord() throws Exception, SQLError {
        Record r = new Record();
        r.type = 1;
        r.setPayload(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        r.forward_overfow = 15;
        r.backward_overflow = 1000;
        r.rowid = 100500;

        ByteBuffer b = r.serialize();

        Record rc = Record.deserialize(b);

        if (r.type != rc.type) throw new Exception("RECORD: types are not equal");
        if (r.rowid != rc.rowid) throw new Exception("RECORD: rowids are not equal");

        if (r.type == Record.T_OVERFLOW_TUPLE) {
            if (r.backward_overflow != rc.backward_overflow)
                throw new Exception("RECORD: backward overflows are not equal");
            if (r.forward_overfow != rc.forward_overfow)
                throw new Exception("RECORD: forward overflows are not equal");
        }
        if (r.record_length != rc.record_length) throw new Exception("RECORD: record lengths are not equal");
        if (!Misc.compareBytes(r.payload, rc.payload)) throw new Exception("RECORD: payloads are not equal");
    }


    @Test
    public void testPage() throws Exception, SQLError, RecordStatus {
        Page p = new Page(15);
        p.previous = 50;
        p.next = 61;

        Record a = new Record(Record.T_TUPLE, 1);
        a.setPayload(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

        Record b = new Record(Record.T_TUPLE, 2);
        b.setPayload(new byte[]{9, 8, 7, 6, 5, 4, 2, 1});

        p.addRecord(a);
        p.addRecord(b);

        p.type = Page.T_TUPLE;

        ByteBuffer buf = p.serialize();

        Page pc = Page.deserialize(buf);

        if (p.number != pc.number) throw new Exception("PAGE: page numbers are not equal");
        if (p.type != pc.type) throw new Exception("PAGE: page types are not equal");
        if (p.previous != pc.previous) throw new Exception("PAGE: previous are not equal");
        if (p.next != pc.next) throw new Exception("PAGE: next are not equal");
        if (p.records.equals(pc.records)) throw new Exception("PAGE: data are not equal");
    }


    @Test
    public void testAttribute() throws Exception, SQLError {
        Attribute a = new Attribute("hello", Attribute.T_SHORT);
        a.setDefaultValue(new byte[]{1, 2, 3, 4});
        a.setFlag(Attribute.F_UQ);
        a.rootpage = 100500;

        ByteBuffer b = a.serialize();

        Attribute ac = Attribute.deserialize(b);

        if (!a.name.equals(ac.name)) throw new Exception("ATTR: Not equal names");
        if (a.type != ac.type) throw new Exception("ATTR: Not equal types");
        if (a.flags != ac.flags) throw new Exception("ATTR: Not equal flags");
        if (!Misc.compareBytes(a.defaultValue, ac.defaultValue)) throw new Exception("ATTR: Not equal default value");
        if (a.fk != null && a.fk.equals(ac.fk)) throw new Exception("ATTR: Not equal foreign key");
        if (a.rootpage != ac.rootpage) throw new Exception("ATTR: Not equal rootpage");
    }

    */
}
