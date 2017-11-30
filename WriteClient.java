import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;

public class WriteClient extends Thread {

	public void run() { 
	
	String[] args = {"localhost","5020"};
	int numBytes = 33278;
	char b[] = new char[numBytes];
	String hostName = args[0];
	int portNumber = Integer.parseInt(args[1]);

	try {
	    Socket echoSocket = new Socket(hostName, portNumber);
	    System.out.println("Attemption socket creation");
	    PrintWriter out =   new PrintWriter(echoSocket.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
	    BufferedReader stdIn = new BufferedReader( new InputStreamReader(System.in));
	    String userInput;
//	    while ((userInput = stdIn.readLine()) != null) {
//	        out.println(userInput);
//	        System.out.println("echo: " + in.readLine());
//	    }
	    //int s = stdIn.read(b, 0, 100);
	System.out.println("echo: "+ in.readLine());
	System.out.println("echo2: "+ in.readLine());
	System.out.println("echo2: "+ in.readLine());
	out.println("Hello Server, I am the client writing back! \n");
	out.println("message\n");
	    //System.out.printf("Bytes read: %s \n",+s);
	    
	    echoSocket.close();
	}catch(Exception e) {
		e.printStackTrace();
	}
	}
}
