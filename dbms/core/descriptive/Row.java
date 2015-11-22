package core.descriptive;

import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/27/2015
 */
public class Row implements Serializable {
    final Object[] attrs;
    String tbl_name;

    public Row(Object[] attrs, String tbl_name) {
        super();
        this.attrs = attrs;
        this.tbl_name = tbl_name;
    }

    public static Serializer getSerializer(TableSchema schema) {
        return new Serializer<Row>() {
            @Override
            public void serialize(DataOutput dataOutput, Row row) throws IOException {
                for (int i = 0; i < row.attrs.length; i++) {
                    if (schema.attributes[i].type == Attribute.T_TEXT) dataOutput.writeUTF((String) row.attrs[i]);
                    else if (schema.attributes[i].type == Attribute.T_INT) dataOutput.writeInt((Integer) row.attrs[i]);
                    else if (schema.attributes[i].type == Attribute.T_SHORT)
                        dataOutput.writeShort((Short) row.attrs[i]);
                    else if (schema.attributes[i].type == Attribute.T_FLOAT)
                        dataOutput.writeFloat((Float) row.attrs[i]);
                }
            }

            @Override
            public Row deserialize(DataInput dataInput, int q) throws IOException {
                Object[] attrs = new Object[schema.attributes.length];

                for (int i = 0; i < attrs.length; i++) {
                    if (schema.attributes[i].type == Attribute.T_TEXT) attrs[i] = (String) dataInput.readUTF();
                    else if (schema.attributes[i].type == Attribute.T_INT) attrs[i] = (Integer) dataInput.readInt();
                    else if (schema.attributes[i].type == Attribute.T_SHORT)
                        attrs[i] = (Short) dataInput.readShort();
                    else if (schema.attributes[i].type == Attribute.T_FLOAT)
                        attrs[i] = (Float) dataInput.readFloat();
                }

                return new Row(attrs, schema.tbl_name);
            }
        };
    }

}
