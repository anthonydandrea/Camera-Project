import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.*;

class FakeCameraMain {

	static ClientSock cs1;
	static ClientSock cs2;
	  static byte[] clientIm1 = null;
		  static byte[] clientIm2 = null;
			static FakeCameraThread cam1;
static 			FakeCameraThread cam2;

	public static void main(String[] args) {
		// create and start thread

		System.out.println("Starting GUI");
	     GUI gui = new GUI();

	     /*FileImage fi = new FileImage("media/1.jpg");
	     byte[] image1 = fi.getData();
	     fi = new FileImage("media/2.jpg");
	     byte[] image2 = fi.getData();
	     fi = new FileImage("media/3.jpg");
	     byte[] image3 = fi.getData();
	     fi = new FileImage("media/4.jpg");
	     byte[] image4 = fi.getData();
*/
		try {
       // cs1 = new ClientSock(9997);
		//		cs1.connect();

			  cs2 = new ClientSock(9996);
				cs2.connect();

			} catch(Exception e)
			{
				System.out.println("Failed");
			}

        //try {
        //    BufferedImage image = ImageIO.read( new ByteArrayInputStream(clientIm) );
        //    ImageIO.write(image, "JPG", new File("SIMAIN.jpg"));
        //} catch (IOException e){
        //    e.printStackTrace();
        //}

	     monitor m = new monitor(gui);
	     gui.addMonitor(m);

		System.out.println("Starting Camera");
		  // FakeCameraThread cam1 = new FakeCameraThread(image1, image2, image3, clientIm, m, 1);
			cam1 = new FakeCameraThread(m, 1);
			//cam1.handleNewImage(clientIm1);
		  cam1.start();

		  // FakeCameraThread cam2 = new FakeCameraThread(image1, image2, image3, image4, m, 2);
			cam2 = new FakeCameraThread(m, 2);
			//cam2.handleNewImage(clientIm2);
		  cam2.start();

		  DisplayThread dispThread = new DisplayThread(m);
		  dispThread.start();

		  // create and start gui
		  gui.runGUI();


		 (new Thread() {
  public void run() {
		while(true) {
		 System.out.println("Getting pictures");
		 clientIm1 = cs1.ClientReadSingleImage();
		 clientIm2 = cs2.ClientReadSingleImage();
		 cam1.handleNewImage(clientIm1);
		 cam2.handleNewImage(clientIm2);
		 try {
		//  Thread.sleep(2000);
	 } catch(Exception e){}

	 }
  }
 }).start();
   }



}
