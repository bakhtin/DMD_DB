package core.descriptive;

import core.exceptions.SQLError;
import core.util.Misc;

import java.nio.ByteBuffer;
import java.util.HashMap;

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

    int rootpage = 0;

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
        setType((byte) getDataType(type));
    }

    public Attribute(String name, byte type) throws SQLError {
        setName(name);
        setType(type);
    }

    Attribute() {
    }

    public static byte fromMysql(String type) throws SQLError {
        if (type.equals("INT")) return 1;
        else if (type.equals("VARCHAR")) return 3;
        else if (type.equals("ENUM")) return 4; //return byte
        else throw (new SQLError("Unable to cast MySQL type: " + type));
    }

    public static byte toEnumCaster(String value) {
        // hardcodim
        HashMap<String, Byte> kwEnum = new HashMap<>();
        kwEnum.put("thesaurusterms", (byte) 1);
        kwEnum.put("controlledterms", (byte) 2);
        kwEnum.put("uncontrolledterms", (byte) 2);
        try {
            return kwEnum.get(value);
        }
        catch (NullPointerException e) {
            return (byte) 1;
        }
    }

    public static String fromEnumCaster(byte value) {
        HashMap<Byte, String> kwEnum = new HashMap<>();
        kwEnum.put(value, "thesaurusterms");
        kwEnum.put(value, "controlledterms");
        kwEnum.put(value, "uncontrolledterms");
        try {
            return kwEnum.get(value);
        }
        catch (NullPointerException e) {
            return "thesaurusterms";
        }
    }

    public static Attribute deserialize(ByteBuffer b) {
        Attribute a = new Attribute();
        a.name = Misc.parseStr(b);
        a.type = b.get();
        a.flags = b.get();

        if (a.hasFlag(F_DV)) {
            a.defaultValue = Misc.parseBytes(b);
        }

        if (a.hasFlag(F_FK)) {
            a.fk = Misc.parseStr(b);
        }

        if (a.hasFlag(F_UQ))
            a.rootpage = b.getInt();

        return a;
    }

    public static int getDataType(String type) {
        type = type.toLowerCase();
        if (type.matches(".*(?:int).*")) return T_INT;
        if (type.matches(".*(?:varchar|text).*")) return T_TEXT;
        if (type.matches(".*(?:float).*")) return T_FLOAT;
        if (type.matches(".*(?:short).*")) return T_SHORT;
        return T_BYTE;
    }

    public static byte getConstraintType(String constraint) {
        constraint = constraint.toLowerCase();
        if (constraint.matches(".*(?:primary key).*")) return F_PK;
        else if (constraint.matches(".*(?:not null).*")) return F_NN;
        else if (constraint.matches(".*(?:auto_increment).*")) return F_AI;
        else if (constraint.matches(".*(?:unique).*")) return F_UQ;
        else return -1;
    }

    public ByteBuffer serialize() {
        byte[] nameb = this.name.getBytes();
        byte[] fkb;

        int size = 2 + nameb.length + 1 + 1 + (this.hasFlag(F_UQ) ? 4 : 0);

        if (this.hasFlag(F_FK)) {
            fkb = this.fk.getBytes();
            size += 2 + fkb.length;
        }

        if (this.hasFlag(F_DV)) {
            size += 2 + defaultValue.length;
        }

        ByteBuffer buf = ByteBuffer.allocate(size);

        Misc.addStr(buf, name);
        buf.put(type);
        buf.put(flags);

        if (this.hasFlag(F_DV))
            Misc.addBytes(buf, defaultValue);

        if (this.hasFlag(F_FK))
            Misc.addStr(buf, fk);

        if (this.hasFlag(F_UQ))
            buf.putInt(rootpage);

        buf.flip();
        return buf;
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

    public void setFlag(byte flag) {
        // if flag = PK, then it has to be Not Null and AutoIncrement
        if (flag == F_PK) flag |= F_NN | F_UQ;
        if (flag == F_FK) flag |= F_NN;


        this.flags = (byte) (flags | flag);
    }

    public byte getFlags() {
        return flags;
    }

    public boolean hasFlag(int flag) {
        return (flags & flag) != 0;
    }


}
