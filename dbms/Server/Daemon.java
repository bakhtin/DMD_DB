package Server;


import java.io.IOException;
import java.net.ServerSocket;

public class Daemon {
    static private boolean shutdownFlag = false;

    public static void main(String[] args) throws IOException {
        try {
            daemonize();
        } catch (Throwable e) {
            System.err.println("Startup failed. " + e.getMessage());
        }

        registerShutdownHook();

        doProcessing();
    }

    static private void doProcessing() {
        while (!shutdownFlag) {
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

    static public void setShutdownFlag() {
        shutdownFlag = true;
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    public void run() {
                        Daemon.setShutdownFlag();
                    }
                }
        );
    }

    static private void daemonize() throws Exception {
        System.in.close();
        System.out.close();
    }
}