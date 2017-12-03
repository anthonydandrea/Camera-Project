import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.*;

class CameraClientMain {
	public static void main(String[] args){
		// create and start thread

		System.out.println("Starting GUI");
	     GUI gui = new GUI();


	     monitor m = new monitor(gui);
	     gui.addMonitor(m);

        ClientThread cs = new ClientThread(m,1,9990);
        cs.start();
        
        ClientThread c2 = new ClientThread(m,2,9991);
        c2.start();

		System.out.println("Starting Camera");

		  DisplayThread dispThread = new DisplayThread(m);
		  dispThread.start();

		  // create and start gui
		  gui.runGUI();
   }

}
