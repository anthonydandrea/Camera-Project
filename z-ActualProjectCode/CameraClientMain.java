import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.*;
import java.util.Scanner;

class CameraClientMain {
	public static void main(String[] args){
		// create and start thread
        int port1;
        int port2;
        Scanner in = new Scanner(System.in);
        
        System.out.println("Please enter the first port number in the range 5001 - 9999: ");
        port1 = in.nextInt();
        System.out.println("You entered " + port1);
        while (port1 < 5001 || port1 > 9999){
            System.out.println("Invalid, please enter the first port again: ");
            port1 = in.nextInt();
            System.out.println("You entered " + port1);
        }
        
        System.out.println("Please enter the second port number in the range 5001 - 9999: ");
        port2 = in.nextInt();
        System.out.println("You entered " + port2);
        while (port2 < 5001 || port2 > 9999){
            System.out.println("Invalid, please enter the second port again: ");
            port2 = in.nextInt();
            System.out.println("You entered " + port2);
        }
        
        
		System.out.println("Starting GUI");
	     GUI gui = new GUI();


	     monitor m = new monitor(gui);
	     gui.addMonitor(m);

        ClientThread cs = new ClientThread(m,1,port1);
        cs.start();
        
        ClientThread c2 = new ClientThread(m,2,port2);
        c2.start();

		System.out.println("Starting Camera");

		  DisplayThread dispThread = new DisplayThread(m);
		  dispThread.start();

		  // create and start gui
		  gui.runGUI();
   }

}
