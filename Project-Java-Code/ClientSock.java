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
		static int numBytes = 33278;
    static byte b[] = new byte[numBytes];

	public static void main(String[] args) throws IOException {


		try {
		    Socket echoSocket = new Socket("localhost", 9990);
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
				int numRead = is.read(b,0,b.length);
			System.out.println("Read # bytes: " + numRead);
			String q = "";
			// for(int x = 0 ; x < numBytes ; x++) {
			// 	//System.out.print(b[x]);
			// 	q += b[x];
			// }

			q = b.toString();
			//String s = new String(b);
			System.out.println("length of q:" +q.length());
			String data = q.substring(0,q.length()-3);
			System.out.println("length of q[0:-3]: " + q.substring(0,q.length()-3).length());
			//System.out.println(s);
			PrintWriter w = new PrintWriter("picJavaReceived.txt");
			w.println(q);
			System.out.println("data: "+ data);
	} catch (Exception e ){e.printStackTrace();};

		System.out.println("done");
	}



}
