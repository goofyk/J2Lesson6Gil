import java.io.*;
import java.net.*;
import java.util.Scanner;

public class VerySimpleChatClient {

    final String EXIT_COMMAND = "exit";
    final String CONNECT_CLOSED = "Connection closed.";
    final String CONNECT_TO_SERVER = "Connection to server established.";

    String message;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;

    public static void main(String[] args) {
        VerySimpleChatClient client = new VerySimpleChatClient();
        client.go();
    }
    public void go() {
        setUpNetworking();
        try(Scanner scanner = new Scanner(System.in)){
            System.out.println(CONNECT_TO_SERVER);
            do{
                message = scanner.nextLine();
                writer.println(message);
                writer.flush();
            } while(!message.equals(EXIT_COMMAND));
            System.out.println(CONNECT_CLOSED);
        } catch(Exception ex){
            ex.printStackTrace();
            writer.close();
        }
    }

    private void setUpNetworking() {

        try {
            sock = new Socket("127.0.0.1", 5000);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());

            Thread readerThread = new Thread(new IncomingReader());
            readerThread.start();

        } catch (IOException ex) {
            ex.printStackTrace();
            writer.close();
        }
    }

    public class IncomingReader implements Runnable {

        public IncomingReader() {
            try {
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch(Exception ex) {ex.printStackTrace();}
        }

        @Override
        public void run() {
            String messResponse;
            try {
               while ((messResponse = reader.readLine()) != null) {
                    System.out.println(messResponse);
               }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
