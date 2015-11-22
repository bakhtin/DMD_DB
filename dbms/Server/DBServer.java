package Server;

import core.descriptive.TableSchema;
import core.managers.DBManager;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

public class DBServer {
    public static DBManager SMDB;
    public static DB db;
    public static Map<String, TableSchema> tables;

    public static void main(String[] args) throws IOException {


        int portNumber = 44444;
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Running server on port " + portNumber);

            db = DBMaker.fileDB(new File("sm.db"))
                    .transactionDisable()
                    .cacheLRUEnable()
                    .make();

            db.commit();

            tables = db.hashMapCreate("tables").valueSerializer(TableSchema.tableSchemaSerializer()).makeOrGet();

            System.out.println("Ready");
            while (listening) {
                new Server.DBServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}