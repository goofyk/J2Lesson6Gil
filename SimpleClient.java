/**
 * @author Anton Gil
 * @version dated Jan 14, 2018
 * @link https://github.com/goofyk/J2Lesson6Gil
 */

import java.io.*;
import java.net.*;
import java.util.*;

class SimpleClient {

    final String SERVER_ADDR = "127.0.0.1";
    final int SERVER_PORT = 2048;
    final String CLIENT_PROMPT = "$ ";
    final String CONNECT_TO_SERVER = "Connection to server established.";
    final String CONNECT_CLOSED = "Connection closed.";
    final String EXIT_COMMAND = "exit";
    PrintWriter writer;
    Socket socket;
    BufferedReader buffReader;

    public static void main(String[] args) {
        new SimpleClient();
    }

    SimpleClient() {
        String message;
        try {

            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            writer = new PrintWriter(socket.getOutputStream());
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            buffReader = new BufferedReader(streamReader);

            Thread readerThread = new Thread(new IncomingReader());
            readerThread.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println(CONNECT_TO_SERVER);
        do
            {
                System.out.print(CLIENT_PROMPT);
                message = scanner.nextLine();
                writer.println(message);
                writer.flush();
            } while(!message.equals(EXIT_COMMAND));
            System.out.println(CONNECT_CLOSED);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public class IncomingReader implements Runnable {

        public IncomingReader() {
            try {
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                buffReader = new BufferedReader(isReader);
            } catch(Exception ex) {ex.printStackTrace();}
        }

        @Override
        public void run() {
            String messResponse;
            try {
                while ((messResponse = buffReader.readLine()) != null) {
                    System.out.println(messResponse);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}

