package core.sys.descriptive;

import core.sys.util.Misc;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class Table {
    String tbl_name;

    int recordsTotal = 0;
    int rootpage = 0;

    Attribute[] attributes;

    public static Table deserialize(ByteBuffer b) {
        Table t = new Table();
        t.tbl_name = Misc.parseStr(b);

        t.recordsTotal = b.getInt();
        t.rootpage = b.getInt();

        short attrn = b.getShort();
        t.attributes = new Attribute[attrn];
        for (short i = 0; i < attrn; i++) {
            t.attributes[i] = Attribute.deserialize(b);
        }

        return t;
    }

    public ByteBuffer serialize() {
        byte[] tbl = tbl_name.getBytes();

        int size = 2 + tbl.length + 4 + 4 + 2; // recordsTotal(int) + rootpage(int) + attrs.length(short)

        ByteBuffer[] attrs = new ByteBuffer[attributes.length];
        for (int i = 0; i < attributes.length; i++) {
            attrs[i] = attributes[i].serialize();
            size += attrs[i].capacity();
        }

        ByteBuffer b = ByteBuffer.allocate(size);


        // PUT
        Misc.addStr(b, tbl_name);
        b.putInt(recordsTotal);
        b.putInt(rootpage);

        b.putShort((short) attrs.length);
        for (int i = 0; i < attributes.length; i++) {
            b.put(attrs[i]);
        }

        b.flip();

        return b;
    }

}