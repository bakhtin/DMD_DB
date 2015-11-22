package core.descriptive;

import core.exceptions.SQLError;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class TableSchema implements Serializable {
    public Attribute[] attributes;
    public HashMap<String, Attribute> attrs = new HashMap<>();
    String tbl_name;
    int recordsTotal = 0;
    int autoIncrement = 0;


    TableSchema() {
    }

    public TableSchema(String name) throws SQLError {
        setName(name);
    }

    public TableSchema(String name, int attrn) throws SQLError {
        setName(name);
        attributes = new Attribute[attrn];
    }

    public static Serializer tableSchemaSerializer() {
        return new Serializer<TableSchema>() {
            @Override
            public void serialize(DataOutput dataOutput, TableSchema o) throws IOException {
                dataOutput.writeUTF(o.tbl_name);
                dataOutput.writeShort(o.attributes.length);
                for (int i = 0; i < o.attributes.length; i++) {
                    Serializer<Attribute> attributeSerializer = Attribute.attributeSerializer();
                    attributeSerializer.serialize(dataOutput, o.attributes[i]);
                }
            }

            @Override
            public TableSchema deserialize(DataInput dataInput, int i) throws IOException {
                TableSchema table = new TableSchema();
                table.tbl_name = dataInput.readUTF();
                short attrl = dataInput.readShort();
                table.attributes = new Attribute[attrl];

                for (int k = 0; k < attrl; k++) {
                    Serializer<Attribute> attributeSerializer = Attribute.attributeSerializer();
                    table.attributes[k] = attributeSerializer.deserialize(dataInput, i);
                    table.attrs.put(table.attributes[k].getName(), table.attributes[k]);
                }

                return table;
            }
        };
    }

    public Serializer[] getSerializers() {
        Serializer[] sr = new Serializer[attributes.length];
        for (int i = 0; i < attributes.length; i++) {
            sr[i] = attributes[i].getSerializer();
        }

        return sr;
    }

    public int getPKlength() {
        int counter = 0;
        for (Attribute a : attributes) {
            if (a.hasFlag(Attribute.F_PK)) counter++;
        }
        return counter;
    }

    public String getName() {
        return tbl_name;
    }

    public void setName(String n) throws SQLError {
        if (n.matches("[a-z0-9A-Z_\\-]{1,24}")) {
            this.tbl_name = n;
        } else {
            throw new SQLError("Incorrect table name");
        }
    }

}
