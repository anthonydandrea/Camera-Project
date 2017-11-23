import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSock {
	static Socket echoSocket;
    static PrintWriter out;
    static BufferedReader in;
    static BufferedReader stdIn;
    static byte b[] = new byte[24663];

	public static void main(String[] args) throws IOException {


		try {
		    Socket echoSocket = new Socket("localhost", 9996);
		    PrintWriter out =
		        new PrintWriter(echoSocket.getOutputStream(), true);
		    BufferedReader in =
		        new BufferedReader(
		            new InputStreamReader(echoSocket.getInputStream()));
		    BufferedReader stdIn =
		        new BufferedReader(
		            new InputStreamReader(System.in));

		    InputStream is = echoSocket.getInputStream();
		    System.out.println(is.available());
			is.read(b,3,b.length-3);
			System.out.println("Dese nutszzzzzz");
			String s = new String(b);
			System.out.println(s);
			PrintWriter w = new PrintWriter("pic.jpg");
			w.println(s);
	} catch (Exception e ){e.printStackTrace();};

		System.out.println("done");
	}



}
