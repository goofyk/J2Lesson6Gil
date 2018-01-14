import java.io.*; 
import java.net.*; 
import java.util.*;

public class VerySimpleChatServer {

	private List<PrintWriter> clientOutputStreams = new ArrayList<PrintWriter>();
	PrintWriter writer;
	String message;
	BufferedReader reader;
	Socket sock;

	public class ClientHandler implements Runnable { 

		public ClientHandler(Socket clientSocket) { 
			try { 
				sock = clientSocket;
				InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(isReader);
			} catch(Exception ex) {ex.printStackTrace();}
		}

		@Override
		public void run() { 
			try {
				while ((message = reader.readLine()) != null) { 
					System.out.println(Thread.currentThread().getName() + ": " + message);
					tellEveryone(message); 
				}
			} catch(Exception ex) {ex.printStackTrace();}
		}
	}
	
	public static void main (String[] args) {
		System.out.println("Server starting...");
		new VerySimpleChatServer().go();
	}
	
	public void go() {

		clientOutputStreams = new ArrayList<PrintWriter>();

		try {
			ServerSocket serverSock = new ServerSocket(5000);
			while(true) {
				Socket clientSocket = serverSock.accept();
				writer = new PrintWriter(clientSocket.getOutputStream());
				clientOutputStreams.add(writer); 
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start() ;
				System.out.println("got a connection");
			}} catch(Exception ex) {
				ex.printStackTrace();
			}
		}

	public void tellEveryone(String message) {
		Iterator it = clientOutputStreams.iterator();
		while(it.hasNext()) {
			try {
				writer = (PrintWriter) it.next();
				writer.print(Thread.currentThread().getName() + ": ");
				writer.println(message);
				writer.flush();
			} catch(Exception ex) {
				ex.printStackTrace();
			} 
		}
	}
}
