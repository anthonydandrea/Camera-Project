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

public class ClientThread extends Thread{
    Socket echoSocket = null;
    BufferedReader in;
    BufferedReader buffIn;
    BufferedReader stdIn;
    InputStream is;
    int numBytes = 33278;
    byte imageBytes[] = new byte[numBytes];
    byte timeBytes[] = new byte[24];
    byte sizeBytes[] = new byte[24];
    byte motionBytes[] = new byte[24];
    monitor m;
    int camera;
    int port;
    int numRead;
    PrintWriter out;
    String msg;
    int size;
    long timestamp;
    int totalRead;
    char motionChar;
    boolean detectedMotion;
 
    
    ClientThread(monitor mon, int c, int p) {
        // Assign attributes
        m = mon;
        camera = c;
        port = p;
        detectedMotion = false;
        // Create socket and input streams
        try {
            echoSocket = new Socket("localhost", port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            
            is = echoSocket.getInputStream();
        } catch(Exception e) {}
    }
    
    
    public void run() {
        try{
            Thread.sleep(500);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        int bytesRead = 0;
        int n = 0;
        while(!isInterrupted()) {
            try {
  
                // read in the size of the image file
                bytesRead = 0;
                numRead = 0;
                totalRead = 0;
                
                while(totalRead < 6){
                    numRead = is.read(sizeBytes, totalRead, (6 - totalRead));
                    totalRead += numRead;
                }
                // Convert size to integer
                String s = new String(sizeBytes, "UTF-8");
                try{
                    size = Integer.parseInt(s.replaceAll("[\\D]", ""));
                } catch (NumberFormatException e){
                    System.out.println("Error with converting to int " + s);
                    size = 24400;
                }
                
                
                // Check size to make sure it's resonable
                if (size > numBytes){
                    System.out.println("ERROR READING SIZE: TOO BIG");
                    size = 24400;
                }
                
                // Read in motion
                // "have" means motion is occuring
                // "stil" means no motion is happening
                totalRead = 0;
                numRead = 0;
                while(totalRead < 5){
                    numRead = is.read(motionBytes, totalRead, (5 - totalRead));
                    totalRead += numRead;
                }
                String motion = new String(motionBytes, "UTF-8");
          
             
                
                // Read in the image data
                totalRead = 0;
                n = 0;
                bytesRead = 0;
                
                while((n = is.read(imageBytes, bytesRead, size - bytesRead)) > 0){
                    bytesRead += n;
                }
                
 
                // Read in the timestamp
                totalRead = 0;
                numRead = 0;
                while(totalRead < 20){
                    numRead = is.read(timeBytes, totalRead, (20 - totalRead));
                    totalRead += numRead;
                }
                
                // Convert timestamp to long
                String str = new String(timeBytes, "UTF-8");
                try{
                    timestamp = Long.parseLong(str.replaceAll("[\\D]", ""));
                } catch (NumberFormatException e){
                    System.out.println("Error converting to long");
                }
                timestamp = timestamp / 1000000;
          
                // Add image to monitor
                motionChar =  motion.charAt(0);
                if (motionChar == 'h'){
                    // motion detected
                    detectedMotion = true;
                } else {
                    detectedMotion = false;
                }
                m.addImage(camera, imageBytes, timestamp, detectedMotion);
                
            
                // Get mode from monitor and send it back
                msg = m.framesRate();
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
