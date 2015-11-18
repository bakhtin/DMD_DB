package Server;

import java.net.*;
import java.io.*;

public class DBServer {
    public static void main(String[] args) throws IOException {

        int portNumber = 44444;
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Running server on port " + portNumber);
            System.out.println("Ready");
            while (listening) {
                new DBServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}