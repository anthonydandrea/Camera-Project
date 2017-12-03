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


public class ModeThread extends Thread{
    monitor m;
    String msg;
    PrintWriter out;
    
    ModeThread(monitor mon, PrintWriter o){
        m = mon;
        out = o;
    }
    
    public void run(){
        while(!isInterrupted()){
            msg = "Message\n\n";
            msg = m.framesRate();
            out.println(msg);
            System.out.println(msg);
        }
        
    }
}
