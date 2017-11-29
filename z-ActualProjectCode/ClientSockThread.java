import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ClientSockThread extends Thread{
	Socket echoSocket;
	BufferedReader in;
	BufferedReader stdIn;
	int numBytes = 33278;
	byte b[] = new byte[numBytes];
    monitor m;
    
	ClientSockThread(monitor mon) {
        m = mon;
	}
	public void run() {
		int num = 0;
		while(num < 10) {
		try {
		    Socket echoSocket = new Socket("localhost", 9993);
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
            System.out.println(is.available());
			String q = "";
			q = b.toString();
			System.out.println("length of q:" + q.length());

            m.addImage(1, b, System.currentTimeMillis(), false);
            //m.addImage(1, b, System.currentTimeMillis(), false);
            //m.addImage(1, b, System.currentTimeMillis(), false);
            
            BufferedImage image = ImageIO.read( new ByteArrayInputStream(b) );
            ImageIO.write(image, "JPG", new File("SentImage1.jpg"));
        } catch (Exception e ){
        		e.printStackTrace();
        	};
        	num += 1;
        	try {
        		Thread.sleep(3000);
        	} catch (InterruptedException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
	}
	}
	
}
