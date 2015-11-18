package core.sys.descriptive;

import core.sys.util.Misc;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/27/2015
 */
public class Row implements Comparable<Row> {
    Object[] attrs;

    private int[] pk;

    public Row(Object[] attrs, int[] index) {
        this.attrs = attrs;
        this.pk = index;
    }

    Row() {
    }

    public static Row deserialize(ByteBuffer b, TableSchema t) {
        Row row = new Row();

        row.attrs = new Object[t.attributes.length];
        for (int i = 0; i < t.attributes.length; i++) {
            switch (t.attributes[i].type) {
                case Attribute.T_BYTE:
                    row.attrs[i] = Misc.parseBytes(b);
                    break;

                case Attribute.T_FLOAT:
                    row.attrs[i] = b.getFloat();
                    break;

                case Attribute.T_TEXT:
                    row.attrs[i] = Misc.parseStr(b);
                    break;

                case Attribute.T_INT:
                    row.attrs[i] = b.getInt();
                    break;

                case Attribute.T_SHORT:
                    row.attrs[i] = b.getShort();
                    break;
            }
        }

        return row;
    }

    public Object getPk() {
        if (this.getPkLength() == 1) {
            return this.attrs[0];
        } else {
            String ind = "";
            for (int i = 0; i < pk[i]; i++) {
                ind += attrs[i].toString() + (char) 0;
            }
            return ind;
        }
    }

    public int getPkLength() {
        return pk.length;
    }

    @Override
    public int compareTo(Row o) {
        return ((Comparable) getPk()).compareTo(o.getPk());
    }

    public ByteBuffer serialize() throws Exception {
        int size = 0;
        for (int i = 0; i < attrs.length; i++) {
            if (attrs[i] instanceof String) size += 2 + ((String) attrs[i]).getBytes().length;
            else if (attrs[i] instanceof byte[]) size += 2 + ((byte[]) attrs[i]).length;
            else if (attrs[i] instanceof Integer) size += 4;
            else if (attrs[i] instanceof Short) size += 2;
            else if (attrs[i] instanceof Float) size += 4;
            else throw new Exception("Wrong type");
        }

        ByteBuffer b = ByteBuffer.allocate(size);
        for (int i = 0; i < attrs.length; i++) {
            if (attrs[i] instanceof String) Misc.addStr(b, (String) attrs[i]);
            else if (attrs[i] instanceof byte[]) Misc.addBytes(b, (byte[]) attrs[i]);
            else if (attrs[i] instanceof Integer) b.putInt((int) attrs[i]);
            else if (attrs[i] instanceof Short) b.putShort((short) attrs[i]);
            else if (attrs[i] instanceof Float) b.putFloat((float) attrs[i]);
            else throw new Exception("Wrong type");
        }
        return b;
    }


}
