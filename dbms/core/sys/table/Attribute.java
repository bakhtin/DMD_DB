package core.sys.table;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 * @date 10/23/2015
 */
public class Attribute  {
    public static byte F_NN = 1;
    public static byte F_PK = 2;
    public static byte F_FK = 4;
    public static byte F_DV = 8;
    public static byte F_UQ = 16;
    public static byte F_AI = 32;

    String name;
    int type;
    private byte flags = 0;
    String foreignKey;
    byte[] value;

    public void setFlags(byte flags) {
        this.flags = (byte)flags;
    }

    public byte getFlags(){ return flags;}
}
