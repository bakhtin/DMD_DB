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

            out.println("SMDB v.0.1-pre-alpha Connection ready");

            while ((inputLine = in.readLine()) != null) {
                try {
                    outputLine = Server.DBServer.SMDB.processQuery(inputLine);
                    out.println(outputLine);
                } catch (SQLError e) {
                    out.println();
                    out.println(e.getMessage());
                } catch (JSQLParserException e) {
                    out.println();
                    if (e == null) out.println("Wrong SQL syntax.");
                    else out.println(e.getCause());
                } catch (Exception e) {
                    out.println();
                    e.printStackTrace();
                } catch (RecordStatus recordStatus) {
                    out.println();
                    recordStatus.printStackTrace();
                }

                if (outputLine.equals("Bye"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

