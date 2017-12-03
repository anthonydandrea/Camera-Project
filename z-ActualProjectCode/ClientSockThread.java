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
import java.nio.Buffer;
import java.nio.CharBuffer;

public class ClientSockThread extends Thread{
    Socket echoSocket = null;
    BufferedReader in;
    BufferedReader buffIn;
    BufferedReader stdIn;
    InputStream is;
    int numBytes = 33278;
    byte b[] = new byte[numBytes];
    monitor m;
    int camera;
    int port;
    int numRead;
    PrintWriter out;
    char[] buffer = new char[100];
    //ModeThread modeThread;
    String msg;
    String s;
    int size;
    long timestamp;
    int totalRead;
    
    ClientSockThread(monitor mon, int c, int p) {
        m = mon;
        camera = c;
        port = p;
        try {
            echoSocket = new Socket("localhost", port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            //buffIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            
            is = echoSocket.getInputStream();
           // modeThread = new ModeThread(m, out);
           // modeThread.start();
        } catch(Exception e) {}
    }
    public void run() {
        try{
            Thread.sleep(500);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        while(!isInterrupted()) {
            
            try {
                
                // Instead:
                // read chunk into buffer
                // divide appropriately
                
                
                // read in the size of the image file
                totalRead = 0;
                while(totalRead < 23){
                    System.out.println(".......");
                    numRead = in.read(buffer, 0, 23);
                    s = String.valueOf(buffer);
                    totalRead += numRead;
                }
                System.out.println("read size bytes = " + totalRead);
                
                try{
                    size = Integer.parseInt(s.replaceAll("[\\D]", ""));
                } catch (NumberFormatException e){
                    System.out.println("Error with converting to int " + s);
                }

                System.out.println("Read size as " + size);
                if (size > numBytes){
                    // bigger than max size
                    System.out.println("ERROR READING SIZE: TOO BIG");
                    size = 24710;
                }
                
                // Read in the timestamp
                totalRead = 0;
                while(totalRead < 23){
                    System.out.println(".......");
                    numRead = in.read(buffer, 0, 24);
                    s = String.valueOf(buffer);
                    totalRead += numRead;
                }
                System.out.println("read time bytes = " + totalRead);
                
                try{
                    timestamp = Long.parseLong(s.replaceAll("[\\D]", ""));
                } catch (NumberFormatException e){
                    System.out.println("Error with converting to long " + s);

                }
                System.out.println("read time as = " + timestamp);
                
                // Read in the image data
                totalRead = 0;
                while(totalRead < (size - 1)){
                    //if (is.available() > 1){
                        System.out.println("READING");
                        numRead = is.read(b, 0, size - numRead);
                        System.out.println("READ " + numRead);
                        totalRead += numRead;
                    //}else {
                    //    System.out.println("Nothing to read");
                    //    Thread.sleep(200);
                    //}
                }
                System.out.println("read image bytes " + totalRead);
                if (totalRead > size){
                    System.out.println("READ EXTRA " + (totalRead - size));
                }
                 
  
                
                //System.out.println("Read image bytes: " + numRead);
                //numRead = is.read(b,0,size);
                //System.out.println("Read # bytes: " + numRead);
                //String q = "";
                //q = b.toString();
                //System.out.println("length of q:" + q.length());
                //System.out.println("ClientSockThread add image");
                m.addImage(camera, b, System.currentTimeMillis());
                
                //in.read(buffer, 0, 8);
                //s = String.valueOf(buffer);
                //System.out.println("Read in the time of " + s);
                
                
                //msg = m.framesRate();
                msg = "Aut";
                System.out.println(msg);
                out.println(msg);

  
            } catch (Exception e ){
                e.printStackTrace();
            };
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
}
