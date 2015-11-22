package Server;

import core.exceptions.RecordStatus;
import core.exceptions.SQLError;
import net.sf.jsqlparser.JSQLParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DBServerThread extends Thread {
    private Socket socket = null;

    public DBServerThread(Socket socket) {
        super("DBServerThread");
        this.socket = socket;
    }

    public void run() {

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()))
        ) {
            String inputLine, outputLine = "";
            String query = "";
            out.println("CONNECTION_OK");

            while ((inputLine = in.readLine()) != null) {

                if (inputLine.equals("Bye")) {
                    DBServer.db.commit();
                    DBServer.db.close();
                    break;
                }

                try {
                    query += inputLine;
                    if (query.endsWith(";")) {
                        query = query.replaceAll("[\\s]{2,}", " ").replaceAll("[\\n\\r\\t]|;", "");
                        outputLine = Server.DBServer.SMDB.processQuery(query);
                        query = "";
                        out.println(outputLine);
                    }
                } catch (SQLError e) {
                    out.println();
                    out.println(e.getMessage());
                    query = "";
                } catch (JSQLParserException e) {
                    out.println();
                    if (e == null) out.println("Wrong SQL syntax.");
                    else out.println(e.getCause());
                    query = "";
                } catch (Exception e) {
                    out.println();
                    e.printStackTrace();
                    query = "";
                } catch (RecordStatus recordStatus) {
                    out.println();
                    recordStatus.printStackTrace();
                    query = "";
                }


            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

