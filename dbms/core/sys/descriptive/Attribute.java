package core.sys.descriptive;

import core.sys.exceptions.SQLError;
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
    public static final byte F_NN = 1;        // not null.      If set -- raise an exception while inserting null
    public static final byte F_PK = 2;        // primary key.   If set -- attribute is Not Null and AutoIncrement
    public static final byte F_FK = 4;        // foreign key.   If set -- it points to the tabledescriptor name.attribute name
    public static final byte F_DV = 8;        // default value. If set -- defaultValue is set
    public static final byte F_UQ = 16;       // unique value
    public static final byte F_AI = 32;       // auto increment

    /**
     * TYPES
     */
    public static final byte T_INT = 1;
    public static final byte T_FLOAT = 2;
    public static final byte T_TEXT = 3;
    public static final byte T_BYTE = 4;
    public static final byte T_SHORT = 5;

    String name;
    /**
     * 1 - Integer
     * 2 - Float
     * 3 - String
     * 4 - Bytes
     * 5 - Short
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

    public Attribute(String name) throws SQLError {
        setName(name);
    }

    public Attribute(String name, String type) throws SQLError {
        setName(name);
        setType(type);
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
                case 1:
                    a.defaultValue = new byte[4];
                    break;
                case 2:
                    a.defaultValue = new byte[4];
                    break;
                case 3:
                    short size = b.getShort();
                    a.defaultValue = new byte[size];
                    break;
                case 4:
                    a.defaultValue = new byte[1];
                    break;
                case 5:
                    a.defaultValue = new byte[2];
                    break;
            }
            b.get(a.defaultValue);
        }

        if (a.hasFlag(F_FK)) {
            a.fk = Misc.parseStr(b);
        }

        return a;
    }

    public static int getType(String type) {
        type = type.toLowerCase();
        if (type.matches(".*(?:int).*")) return T_INT;
        if (type.matches(".*(?:varchar|text).*")) return T_TEXT;
        if (type.matches(".*(?:float).*")) return T_FLOAT;
        if (type.matches(".*(?:short).*")) return T_SHORT;
        return T_BYTE;
    }

    public void removeFlag(byte flag) {
        if (this.hasFlag(flag)) this.flags ^= flag;
    }

    public void setName(String n) throws SQLError {
        if (n.matches("[a-z0-9A-Z_\\-]{1,24}")) {
            this.name = n;
        } else {
            throw new SQLError("Incorrect attribute name");
        }
    }

    public void setType(byte type) throws SQLError {
        if (type >= 1 && type <= 5) this.type = type;
        else throw new SQLError("Wrong type!");
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
