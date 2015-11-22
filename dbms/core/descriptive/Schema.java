package core.descriptive;

import Server.DBServer;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/21/2015
 */
public class Schema {
    public static ConcurrentNavigableMap<Integer, String>
            issue_type = DBServer.db
            .treeMapCreate("issue_type")
            .make();

    public static ConcurrentNavigableMap<Integer, String>
            keyword = DBServer.db
            .treeMapCreate("keyword")
            .make();

    public static ConcurrentNavigableMap<Integer, String>
            publisher = DBServer.db
            .treeMapCreate("publisher")
            .make();

    public static ConcurrentNavigableMap<Integer, String>
            affiliation = DBServer.db
            .treeMapCreate("affiliation")
            .make();

    public static ConcurrentNavigableMap<Integer, String>
            issue_name = DBServer.db
            .treeMapCreate("issue_name")
            .make();

    public static ConcurrentNavigableMap<Integer, String>
            role = DBServer.db
            .treeMapCreate("role")
            .make();

    public static ConcurrentNavigableMap<Integer, String>
            author = DBServer.db
            .treeMapCreate("author")
            .make();

    public static ConcurrentNavigableMap<Integer, Object[]>
            publication = DBServer.db
            .treeMapCreate("publication")
            .valueSerializer(new Serializer<Object[]>() {
                @Override
                public void serialize(DataOutput dataOutput, Object[] data) throws IOException {
                    dataOutput.writeUTF((String)data[0]);
                    dataOutput.writeUTF((String)data[1]);
                    dataOutput.writeUTF((String)data[2]);
                    dataOutput.writeUTF((String)data[3]);

                }

                @Override
                public Object[] deserialize(DataInput dataInput, int i) throws IOException {

                }
            })
            .make();



}
