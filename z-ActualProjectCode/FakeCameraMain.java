import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.*;

class FakeCameraMain {
	public static void main(String[] args){
		// create and start thread

		System.out.println("Starting GUI");
	     GUI gui = new GUI();

	     FileImage fi = new FileImage("media/1.jpg");
	     byte[] image1 = fi.getData();
	     fi = new FileImage("media/2.jpg");
	     byte[] image2 = fi.getData();
	     fi = new FileImage("media/3.jpg");
	     byte[] image3 = fi.getData();
	     fi = new FileImage("media/4.jpg");
	     byte[] image4 = fi.getData();


        //try {
        //    BufferedImage image = ImageIO.read( new ByteArrayInputStream(clientIm) );
        //    ImageIO.write(image, "JPG", new File("SIMAIN.jpg"));
        //} catch (IOException e){
        //    e.printStackTrace();
        //}

	     monitor m = new monitor(gui);
	     gui.addMonitor(m);

        ClientSockThread cs = new ClientSockThread(m,1,9990);
        cs.start();
				ClientSockThread c2 = new ClientSockThread(m,2,9991);
        c2.start();


		System.out.println("Starting Camera");
		  //FakeCameraThread cam1 = new FakeCameraThread(image1, image2, image3, image3, m, 1);
		  //cam1.start();

		  //FakeCameraThread cam2 = new FakeCameraThread(image1, image2, image3, image3, m, 2);
		  //cam2.start();

		  DisplayThread dispThread = new DisplayThread(m);
		  dispThread.start();

		  // create and start gui
		  gui.runGUI();
   }

}
