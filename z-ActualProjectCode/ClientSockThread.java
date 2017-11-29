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
	Socket echoSocket = null;
	BufferedReader in;
	BufferedReader stdIn;
	int numBytes = 33278;
	byte b[] = new byte[numBytes];
    monitor m;
		int camera;
		int port;

	ClientSockThread(monitor mon, int c, int p) {
        m = mon;
				camera = c;
				port = p;
				try {
						echoSocket = new Socket("localhost", port);
				} catch(Exception e) {}
	}
	public void run() {
		int num = 0;
		while(true) {
		
		try {

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
				System.out.println("ClientSockThread add image");
            m.addImage(camera, b, System.currentTimeMillis(), false);
            //m.addImage(1, b, System.currentTimeMillis(), false);
            //m.addImage(1, b, System.currentTimeMillis(), false);

            //BufferedImage image = ImageIO.read( new ByteArrayInputStream(b) );
            //ImageIO.write(image, "JPG", new File("SentImage1.jpg"));
        } catch (Exception e ){
        		e.printStackTrace();
        	};
        	num += 1;
        	try {
        		Thread.sleep(0);
        	} catch (InterruptedException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
	}
	}

}
