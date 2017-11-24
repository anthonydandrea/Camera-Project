import java.awt.Image;
import java.util.concurrent.ExecutionException;
import java.util.*;
import javax.swing.SwingWorker;

public class monitor {
	//ATTRIBUTES RELATING TO MODES
	int cameraMode; // MOVIE vs IDLE
	int lastCamMode;
	int viewingMode; // SYNCHRONOUS vs ASYNCHRONOUS
	int lastViewingMode;
	boolean modeChanged;

	// ATTRIBUTES RELATING TO DISPLAYING IMAGES
	boolean newImage; // new picture available
	boolean display1; // Which camera to display
	long dispTime; // Time to display at
	long diff; // difference in display times
	
	// LISTS OF IMAGES TO DISPLAY
	LinkedList<TimestampedImage> imageList1 = new LinkedList<TimestampedImage>();
	LinkedList<TimestampedImage> imageList2 = new LinkedList<TimestampedImage>();
	
	boolean emptyList;
	int frames;

	GUI gui;

	// CONSTANTS
	public static final int IDLE_MODE  = 0;
	public static final int MOVIE_MODE = 1;
	
	public static final int SYNCHRONOUS_MODE  = 0;
	public static final int ASYNCHRONOUS_MODE = 1;
    public static final int AUTOMATIC_MODE = 2;
    
    public static final int CAMERA_1 = 0;
    public static final int CAMERA_2 = 1;
	
	public monitor(GUI g){
		gui = g;
		newImage = false;
		modeChanged = false;
		emptyList = true;
		cameraMode = IDLE_MODE;
		viewingMode = SYNCHRONOUS_MODE;
		
	}
	
	synchronized void addImage(int camera, byte[] i, long timestamp, boolean motion) {
		// Check if none == new image
        
        // store the image into the list
       if (camera == 1){
       //     lastTime1 = time1;
        //    time1 = System.currentTimeMillis();
            imageList1.add(new TimestampedImage(i, timestamp));
        } else {
        		imageList2.add(new TimestampedImage(i, timestamp));
        }
        
        // Check for motion and change camera mode appropriately
        if (motion == true){
            cameraMode = MOVIE_MODE;
        } else {
            cameraMode = IDLE_MODE;
        }

		// Alerts other threads
		notifyAll();
	}
	
	
	synchronized void changeMode(int newMode) {
		// changes viewing mode

        if(newMode == viewingMode) {
            // No new mode
            return;
        } else {
        		System.out.println("MONITOR CHANGING MODES");
            lastViewingMode = viewingMode;
            viewingMode = newMode;
            modeChanged = true;
        }
        notifyAll();
        return;
		// alerts other threads
	}
	
	private void viewImage(byte[] i, ImagePanel imagePanelToUpdate) {
		  SwingWorker<Data,String> sw = new SwingWorker<Data,String>() {
		   @Override
		   protected Data doInBackground() throws Exception {
			// Prepares Image for display
		    if (imagePanelToUpdate == null) return null;
		    Image image = imagePanelToUpdate.prepare(i); 
		    return new Data(image);
		   }
		   
		   @Override
		   protected void done() {
		    try {
		    	// Paints image on GUI
		     if (get() == null) return;
		     imagePanelToUpdate.refresh(get().image);
		    } catch (InterruptedException e) {
		     e.printStackTrace();
		    } catch (ExecutionException e) {
		     e.printStackTrace();
		    }
		   }
		  };
		  
		  sw.execute();
	 }
	
	synchronized void  display() {
		// wait until there is image to be shown
		// find the waiting time
		// imageTime = smallest amount of time until the next image is shown
        
        // now there is a picture to be shown
        // check mode
        
        if (viewingMode == SYNCHRONOUS_MODE){
            // display the images synchronoutsly
            // images from the same camera displayed in order
            // the two camera streams should coordinate in terms of when they display relative to eachother
            // ie one caputred 2s earlier should be displayed 2s earlier
            

            while (imageList1.size() <= 0 || imageList2.size() <= 0){
                // while there is no image to show
                // wait
                try{
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            
            diff = imageList1.getFirst().timeStamp - imageList2.getFirst().timeStamp;
            // difference in time between the two images
            if (diff < 0){
                diff = - diff;
                display1 = true; // want to display image 1 first
            } else{
                display1 = false; // want to display image 2 first
            }
            // do the actual displaying of the first image
            dispTime = System.currentTimeMillis() + diff;
            while(dispTime > System.currentTimeMillis()){
                try{
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            // Now we have waited the right amount, display the second image
            
            
        } else {
            // display images asysnchronously
            // display as soon as available
            while (imageList1.size() <= 0 && imageList2.size() <= 0){
                // while there is no image to show
                // wait
                try{
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            if (imageList1.size() > 0){
                // there is an image in list 1 to display
                // do the actual displaying
                viewImage(imageList1.getFirst().image, gui.imagePanel1);
                imageList1.removeFirst();
            }
            if (imageList2.size() > 0){
                // there is an image in list 2 to display
                // do the actual displaying
                viewImage(imageList2.getFirst().image, gui.imagePanel2);
                imageList2.removeFirst();
            }
        }
        
        
	}
    

	synchronized int framesRate() {
		// Look at mode
		// tell camera new rate
		while(!modeChanged) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// look at mode and determine frame rate
		modeChanged = false;
		return frames;
	}
	
	
}
