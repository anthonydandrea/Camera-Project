import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.CharBuffer;

public class WriteClient extends Thread {

	public void run() {

		String[] args = { "localhost", "9999" }; //port 5020 to your server
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);

		try {
			Socket echoSocket = new Socket(hostName, portNumber);
			System.out.println("Attempting socket creation");
			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			char[] buf = new char[15];
			in.read(buf, 0, 15);
			String s = String.valueOf(buf);
			System.out.println(s);
			BigInteger timeStamp = new BigInteger(String.valueOf(buf));
			System.out.println("The timestamp value is: " + timeStamp);
			out.println("Hello Server, I am the client writing back! \n");
			out.println("message\n");
			echoSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
