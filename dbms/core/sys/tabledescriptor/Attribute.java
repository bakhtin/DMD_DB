package core.sys.tabledescriptor;

import core.sys.parse.Misc;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 * @date 10/23/2015
 */
class Attribute {
    public static byte F_NN = 1;        // not null.      If set -- raise an exception while inserting null
    public static byte F_PK = 2;        // primary key.   If set -- attribute is Not Null and AutoIncrement
    public static byte F_FK = 4;        // foreign key.   If set -- it points to the tabledescriptor name|attribute name
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
     * Present ONLY if flag F_FK is present!
     * tablename|attributename
     */
    String fk_tbl_name;
    String fk_attr_name;

    public void setFlag(int flag) {
        // if flag = PK, then it has to be Not Null and AutoIncrement
        if (flag == F_PK) flag |= F_NN | F_AI;
        if (flag == F_FK || flag == F_DV || flag == F_UQ) flag |= F_NN;

        this.flags = (byte) (flags | flag);
    }

    public byte getFlags() {
        return flags;
    }

    public boolean hasFlag(int flag) {
        return (flags & flag) != 0;
    }


    public ByteBuffer serialize() {
        byte[] name = this.name.getBytes();
        byte[] fk_tbl_name = this.fk_tbl_name.getBytes();
        byte[] fk_attr_name = this.fk_attr_name.getBytes();
        int size = 2 + name.length + 1 + 1 + defaultValue.length + 2 + fk_tbl_name.length + 2 + fk_attr_name.length;


        ByteBuffer buf = ByteBuffer.allocate(size);
        // put name
        buf.putShort((short)name.length);
        buf.put(name);

        buf.put(type);
        buf.put(flags);

        buf.put(defaultValue);

        if( (flags & F_FK) != 0){
            // put fk
            buf.putShort((short)fk_tbl_name.length);
            buf.put(fk_tbl_name);
            buf.putShort((short)fk_attr_name.length);
            buf.put(fk_attr_name);
        }

        return buf;
    }

    public static Attribute deserialize(ByteBuffer b) {
        Attribute a = new Attribute();
        a.name = Misc.parseStr(b);
        a.type = b.get();
        a.flags = b.get();

        if( (a.flags & F_FK) != 0){
            a.fk_tbl_name = Misc.parseStr(b);
            a.fk_attr_name = Misc.parseStr(b);
        }

        return a;
    }

}
