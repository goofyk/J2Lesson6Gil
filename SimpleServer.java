/**
 * @author Anton Gil
 * @version dated Jan 14, 2018
 * @link https://github.com/goofyk/J2Lesson6Gil
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class SimpleServer {

    final int SERVER_PORT = 2048;
    final String SERVER_START = "Server is started...";
    final String SERVER_STOP = "Server stopped.";
    final String CLIENT_JOINED = " client joined.";
    final String CLIENT_DISCONNECTED = " disconnected";
    final String EXIT_COMMAND = "exit";

    PrintWriter writer;
    private List<PrintWriter> clientOutputStreams = new ArrayList<PrintWriter>();

    public static void main(String[] args) {
        new SimpleServer();
    }

    SimpleServer() {
        int clientCount = 0;
        System.out.println(SERVER_START);
        try (ServerSocket server = new ServerSocket(SERVER_PORT)) {
            while (true) {
                Socket socket = server.accept();
                writer = new PrintWriter(socket.getOutputStream());
                clientOutputStreams.add(writer);
                System.out.println("#" + (++clientCount) + CLIENT_JOINED);
                new Thread(new ClientHandler(socket, clientCount)).start();
            } 
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(SERVER_STOP);
    }

   class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket socket;
        String name;

        ClientHandler(Socket clientSocket, int clientCount) {
            try {
                socket = clientSocket;
                reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream());
                name = "Client #" + clientCount;
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        @Override
        public void run() {
            String message;
            try {
                do {
                    message = reader.readLine();
                    System.out.println(name + ": " + message);
                    writer.println("echo: " + message);
                    writer.flush();
                    tellEveryone(message);
                } while (!message.equalsIgnoreCase(EXIT_COMMAND));
                socket.close();
                System.out.println(name + CLIENT_DISCONNECTED);
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void tellEveryone(String message) {
            Iterator it = clientOutputStreams.iterator();
            while(it.hasNext()) {
                try {
                    writer = (PrintWriter) it.next();
                    writer.print(name + ": ");
                    writer.println(message);
                    writer.flush();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}