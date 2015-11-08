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

    private int comparableIndex = 0;

    public Row(Object[] attrs, int comparableIndex) {
        this.attrs = attrs;
        this.comparableIndex = comparableIndex;
    }

    Row() {
    }

    public static Row deserialize(ByteBuffer b, TableSchema t) {
        Row row = new Row();

        row.attrs = new ByteBuffer[t.attributes.length];
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

    @Override
    public int compareTo(Row o) {
        return ((Comparable) attrs[comparableIndex]).compareTo(o.attrs[comparableIndex]);
    }

    public ByteBuffer serialize() throws Exception {
        int size = 0;
        for (int i = 0; i < attrs.length; i++) {
            if (attrs[i] instanceof Integer) size += 4;
            else if (attrs[i] instanceof Short) size += 4;
            else if (attrs[i] instanceof String) size += 2 + ((String) attrs[i]).length();
            else if (attrs[i] instanceof byte[]) size += 2 + ((byte[]) attrs[i]).length;
            else if (attrs[i] instanceof Float) size += 4;
            else throw new Exception("Wrong type");
        }

        ByteBuffer b = ByteBuffer.allocate(size);
        for (int i = 0; i < attrs.length; i++) {
            if (attrs[i] instanceof Integer) b.putInt((Integer) attrs[i]);
            else if (attrs[i] instanceof Short) b.putShort((Short) attrs[i]);
            else if (attrs[i] instanceof String) Misc.addStr(b, (String) attrs[i]);
            else if (attrs[i] instanceof byte[]) Misc.addBytes(b, (byte[]) attrs[i]);
            else if (attrs[i] instanceof Float) b.putFloat((Float) attrs[i]);
            else throw new Exception("Wrong type");
        }
        return b;
    }


}
