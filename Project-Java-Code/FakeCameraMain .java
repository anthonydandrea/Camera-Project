
class FakeCameraMain {
	public static void main(String[] args){
		// create and start thread
		
		System.out.println("Starting GUI");
	     GUI gui = new GUI(); 
	     
	     FileImage fi = new FileImage("/Users/Sonja/Desktop/HC1_1.jpg");
		 byte[] image1 = fi.getData();
		 fi = new FileImage("/Users/Sonja/Desktop/HC1_2.jpg");
		 byte[] image2 = fi.getData();
		 fi = new FileImage("/Users/Sonja/Desktop/HC2_1.jpg");
		 byte[] image3 = fi.getData();
		 fi = new FileImage("/Users/Sonja/Desktop/HC2_2.jpg");
		 byte[] image4 = fi.getData();
		
			
	     monitor m = new monitor(gui);
	     gui.addMonitor(m);
	     
		System.out.println("Starting Camera");
		  FakeCameraThread cam1 = new FakeCameraThread(image1, image2, m, 1);
		  cam1.start();
		  
		  FakeCameraThread cam2 = new FakeCameraThread(image3, image4, m, 2);
		  cam2.start();
		  
		  DisplayThread dispThread = new DisplayThread(m);
		  dispThread.start();
		  
		  // create and start gui  
		  gui.runGUI();
   }
	
}