package core.sys.table;

import core.sys.parse.Misc;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 * @date 10/23/2015
 */
public class Attribute {
    public static byte F_NN = 1;        // not null.      If set -- raise an exception while inserting null
    public static byte F_PK = 2;        // primary key.   If set -- attribute is Not Null and AutoIncrement
    public static byte F_FK = 4;        // foreign key.   If set -- it points to the table name|attribute name
    public static byte F_DV = 8;        // default value. If set -- defaultValue is set
    public static byte F_UQ = 16;       // unique value
    public static byte F_AI = 32;       // auto increment

    String name;
    /**
     * 0 - Integer
     * 1 - Float
     * 2 - String
     * 3 - Byte
     */
    private byte type;

    /**
     * Possible flags.
     */
    private byte flags = 0;

    // default value is present ONLY if flag F_DV is present!
    byte[] defaultValue;

    /**
     * Present ONLU if flag F_FK is present!
     * tablename|attributename
     */
    String foreignKey;

    public void setFlag(int flag) {
        this.flags = (byte) (flags | flag);
    }

    public byte getFlags() {
        return flags;
    }

    public boolean hasFlag(int flag) {
        return (flags & flag) != 0;
    }


    public byte[] serialize() {
        ByteBuffer buf;
        // initial capacity: name, byte for type and byte for flags
        int capacity = 4 + name.length() + 1 + 1;

        if (hasFlag(F_DV))
            capacity += 4 + defaultValue.length;
        if (hasFlag(F_FK))
            capacity += 4 + foreignKey.length();

        buf = ByteBuffer.allocate(capacity);

        Misc.addStr(buf, name);
        buf.put(type);
        buf.put(flags);

        if (hasFlag(F_DV)) {
            buf.putInt(defaultValue.length);
            buf.put(defaultValue);
        }
        if (hasFlag(F_FK)) {
            Misc.addStr(buf, foreignKey);
        }

        return buf.array();
    }

    public static Attribute deserialize(ByteBuffer b) {
        Attribute a = new Attribute();
        a.name = Misc.parseStr(b);
        a.type = b.get();
        a.flags = b.get();

        if (a.hasFlag(F_DV)) {
            int length = b.getInt();
            a.defaultValue = Misc.parseBytes(b, length);
        }

        if (a.hasFlag(F_FK)) {
            a.foreignKey = Misc.parseStr(b);
        }

        return a;
    }

}
