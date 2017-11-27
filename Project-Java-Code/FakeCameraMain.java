
class FakeCameraMain {
	public static void main(String[] args){
		// create and start thread
		
		System.out.println("Starting GUI");
	     GUI gui = new GUI(); 
		
	     FileImage fi = new FileImage("/Users/Sonja/Desktop/Camera-Project/c-examples/examples/media/1.jpg");
	     byte[] image1 = fi.getData();
	     fi = new FileImage("/Users/Sonja/Desktop/Camera-Project/c-examples/examples/media/2.jpg");
	     byte[] image2 = fi.getData();
	     fi = new FileImage("/Users/Sonja/Desktop/Camera-Project/c-examples/examples/media/3.jpg");
	     byte[] image3 = fi.getData();
	     fi = new FileImage("/Users/Sonja/Desktop/Camera-Project/c-examples/examples/media/4.jpg");
	     byte[] image4 = fi.getData();
			
	     monitor m = new monitor(gui);
	     gui.addMonitor(m);
	     
		System.out.println("Starting Camera");
		  FakeCameraThread cam1 = new FakeCameraThread(image1, image2, image3, image4, m, 1);
		  cam1.start();
		  
		  FakeCameraThread cam2 = new FakeCameraThread(image1, image2, image3, image4, m, 2);
		  cam2.start();
		  
		  DisplayThread dispThread = new DisplayThread(m);
		  dispThread.start();
		  
		  // create and start gui  
		  gui.runGUI();
   }
	
}