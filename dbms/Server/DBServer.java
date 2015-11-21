package Server;

import core.exceptions.RecordStatus;
import core.exceptions.SQLError;
import core.managers.DBManager;

import java.io.IOException;
import java.net.ServerSocket;

public class DBServer {
    public static DBManager SMDB;

    public static void main(String[] args) throws IOException {


        int portNumber = 44444;
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Running server on port " + portNumber);
            SMDB = new DBManager("sm.db");
            System.out.println("Ready");
            while (listening) {
                new DBServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        } catch (SQLError sqlError) {
            sqlError.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (RecordStatus recordStatus) {
            recordStatus.printStackTrace();
        }
    }
}