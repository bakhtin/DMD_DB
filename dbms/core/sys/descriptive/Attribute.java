package core.sys.descriptive;

import core.sys.util.Misc;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/23/2015
 */
public class Attribute {
    /**
     * FLAGS
     **/
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
    byte type;
    /**
     * Possible flags.
     */
    byte flags = 0;
    // default value is present ONLY if flag F_DV is present!
    byte[] defaultValue;
    /**
     * Present ONLY if flag F_FK is present!
     * tablename.attributename
     */
    String fk;

    Attribute(String name) {
        this.name = name;
    }

    Attribute() {
    }

    public static Attribute deserialize(ByteBuffer b) {
        Attribute a = new Attribute();
        a.name = Misc.parseStr(b);
        a.type = b.get();
        a.flags = b.get();

        if (a.hasFlag(F_DV)) {
            switch (a.type) {
                case 0:
                    a.defaultValue = new byte[4];
                    break;
                case 1:
                    a.defaultValue = new byte[4];
                    break;
                case 2:
                    short size = b.getShort();
                    a.defaultValue = new byte[size];
                    break;
                case 3:
                    a.defaultValue = new byte[1];
                    break;
            }
            b.get(a.defaultValue);
        }

        if (a.hasFlag(F_FK)) {
            a.fk = Misc.parseStr(b);
        }

        return a;
    }

    public void setName(String n) {
        this.name = n;
    }

    public void setType(byte type) throws Exception {
        if (type >= 0 && type <= 3) this.type = type;
        else throw new Exception("Wrong type!");
    }

    public void setDefaultValue(byte[] dv) {
        this.defaultValue = dv.clone();
        this.setFlag(F_DV);
    }

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
        byte[] nameb = this.name.getBytes();
        byte[] fkb;

        int size = 2 + nameb.length + 1 + 1;

        if (this.fk != null) {
            fkb = this.fk.getBytes();
            size += fkb.length;
        }

        if (this.defaultValue != null) {
            size += defaultValue.length;
        }

        ByteBuffer buf = ByteBuffer.allocate(size);
        // put name
        Misc.addStr(buf, name);

        buf.put(type);
        buf.put(flags);

        if (this.hasFlag(F_DV))
            buf.put(defaultValue);

        if (this.hasFlag(F_FK))
            Misc.addStr(buf, fk);


        buf.flip();
        return buf;
    }

}
