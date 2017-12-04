import java.awt.Image;
import java.util.concurrent.ExecutionException;
import java.util.*;
import javax.swing.SwingWorker;

public class monitor {
	//ATTRIBUTES RELATING TO MODES
	int cameraMode; // MOVIE vs IDLE
	int viewingMode; // SYNCHRONOUS vs ASYNCHRONOUS
	int userViewMode;
	int userCamMode;
	//boolean modeChanged;

	// ATTRIBUTES RELATING TO DISPLAYING IMAGES
	boolean newImage; // new picture available
    boolean updatedMode;
	boolean display1; // Which camera to display
	long dispTime; // Time to display at
	long diff; // difference in display times
	long lastTimeAdded; // Last time an image was added
    long delay;
    TimestampedImage imageToDisp;

	// LISTS OF IMAGES TO DISPLAY
	LinkedList<TimestampedImage> imageList1 = new LinkedList<TimestampedImage>();
	LinkedList<TimestampedImage> imageList2 = new LinkedList<TimestampedImage>();

	boolean emptyList;
	String message;

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
		//modeChanged = false;
		emptyList = true;
		cameraMode = IDLE_MODE;
		viewingMode = ASYNCHRONOUS_MODE;
		userViewMode = AUTOMATIC_MODE;
		userCamMode = AUTOMATIC_MODE;
        delay = 0;

	}

	//uncomment notifyAll at bottom if made synchronized again
	synchronized public void addImage(int camera, byte[] i, long timestamp, boolean motion) {
		// Check if none == new image
			System.out.println("adding Image in monitor");
        // store the image into the list
       if (camera == 1){
            imageList1.add(new TimestampedImage(i, timestamp));
        } else {
        		imageList2.add(new TimestampedImage(i, timestamp));
        }

       // check image receiving timing for viewing modes
       if (userViewMode == AUTOMATIC_MODE && (System.currentTimeMillis() - lastTimeAdded) < 200 ) {
    	   		// Images were added less than 200ms apart
    	   		// Change to synchronized mode
    	   		System.out.println("Time: " +System.currentTimeMillis());
    	   		System.out.println("Last Time: " + lastTimeAdded);
    	   		lastTimeAdded = System.currentTimeMillis();
    	   		viewingMode = SYNCHRONOUS_MODE;
       } else if (userViewMode == AUTOMATIC_MODE && (System.currentTimeMillis() - lastTimeAdded) >= 200 ) {
    	   		// Images were added over 200 ms apart
    	   		// Change to automatic mode
    	   		lastTimeAdded = System.currentTimeMillis();
    	   		viewingMode = ASYNCHRONOUS_MODE;

       }
        if (motion == true && userCamMode != IDLE_MODE){
            // If we are not enforcing Idle and there is motion, switch to Movie
            cameraMode = MOVIE_MODE;
        } else {
            cameraMode = IDLE_MODE;
        }
        newImage = true;
		// Alerts other threads
		notifyAll();
	}


	synchronized void changeMode(int newMode, int type) {
		System.out.println("Changing mode");
        if (type == 0) {
        		// Changes viewing mode
        		System.out.println("Changing Viewing Mode");
        		if(newMode == userViewMode) {
        			// No new mode
        			return;
        		} else {
        			userViewMode = newMode;
        			if (userViewMode != AUTOMATIC_MODE) {
        				// If the user doesn't choose auto, we enforce the mode
        				viewingMode = userViewMode;
        			}
        		}
        } else if (type == 1) {
        		// Changes camera mode
        		System.out.println("Changing Camera Mode");
        		if (newMode == userCamMode) {
        			// No new mode
        			return;
        		} else {
        			userCamMode = newMode;
        			if (userCamMode != AUTOMATIC_MODE) {
        				// Enforce mode if not auto
        				cameraMode = userCamMode;
        			}
        		}
        }
        updatedMode = true;
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
		// display the images
		System.out.println("Display called");


        if (viewingMode == SYNCHRONOUS_MODE){
            // display the images synchronously
            // images from the same camera displayed in order
            // the two camera streams should coordinate in terms of when they display relative to each other
            // ie one captured 2s earlier should be displayed 2s earlier

        		System.out.println("SYNCHRONIZED");
            while (imageList1.size() <= 0 || imageList2.size() <= 0){
                // while there is no image to show
                // wait
                try{
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            System.out.println("Sync imagelist1: "+ imageList1.size());
            diff = imageList1.getFirst().timeStamp - imageList2.getFirst().timeStamp;
            // difference in time between the two images
            if (diff < 0){
                diff = - diff;
                display1 = true; // want to display image 1 first
            } else{
                display1 = false; // want to display image 2 first
            }
            // do the actual displaying of the first image
            if (display1 == true) {
            		// display image 1
                imageToDisp = imageList1.getFirst();
                viewImage(imageToDisp.image, gui.imagePanel1);
                delay = System.currentTimeMillis() - imageToDisp.timeStamp;
                imageList1.removeFirst();
                
                gui.delayLabel.setText("Delay current time = " + delay);
                gui.delayLabel.setFont(gui.delayLabel.getFont().deriveFont(12.0f));
            } else {
            		// display image 2
                imageToDisp = imageList2.getFirst();
                viewImage(imageToDisp.image, gui.imagePanel2);
                delay = System.currentTimeMillis() - imageToDisp.timeStamp;
                imageList2.removeFirst();
                
                gui.delayLabel.setText("Delay current time = " + delay);
                gui.delayLabel.setFont(gui.delayLabel.getFont().deriveFont(12.0f));
            }
            dispTime = System.currentTimeMillis() + diff;
            while(dispTime > System.currentTimeMillis()){
                try{
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            // Now we have waited the right amount, display the second image
            if (display1 == true) {
            		// display image 2
                    imageToDisp = imageList2.getFirst();
            		viewImage(imageToDisp.image, gui.imagePanel2);
                    delay = System.currentTimeMillis() - imageToDisp.timeStamp;
                    imageList2.removeFirst();
                
                gui.delayLabel.setText("Delay current time = " + delay);
                gui.delayLabel.setFont(gui.delayLabel.getFont().deriveFont(12.0f));
            } else {
            		// display image 1
                    imageToDisp = imageList1.getFirst();
            		viewImage(imageToDisp.image, gui.imagePanel1);
                    delay = System.currentTimeMillis() - imageToDisp.timeStamp;
                    imageList1.removeFirst();
                
                gui.delayLabel.setText("Delay current time = " + delay);
                gui.delayLabel.setFont(gui.delayLabel.getFont().deriveFont(12.0f));
            }








        } else {
            // display images asynchronously
            // display as soon as available
        		System.out.println("ASYNCHRONIZED");
            while (imageList1.size() <= 0 && imageList2.size() <= 0){
                // while there is no image to show
                // wait
                try{
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            System.out.println("ASync imagelist1: "+ imageList1.size());
            if (imageList1.size() > 0){
                // there is an image in list 1 to display
                imageToDisp = imageList1.getFirst();
                viewImage(imageToDisp.image, gui.imagePanel1);
                 delay = System.currentTimeMillis() - imageToDisp.timeStamp;
                imageList1.removeFirst();
                
                gui.delayLabel.setText("Delay current time = " + delay);
                gui.delayLabel.setFont(gui.delayLabel.getFont().deriveFont(12.0f));
            }
            if (imageList2.size() > 0){
                // there is an image in list 2 to display
                imageToDisp = imageList2.getFirst();
                viewImage(imageToDisp.image, gui.imagePanel2);
                 delay = System.currentTimeMillis() - imageToDisp.timeStamp;
                imageList2.removeFirst();
                
                gui.delayLabel.setText("Delay current time = " + delay);
                gui.delayLabel.setFont(gui.delayLabel.getFont().deriveFont(12.0f));
            }
        }
        notifyAll();
	}

	synchronized String framesRate() {
		// Look at mode
		// tell camera new rate

		// look at mode and determine frame rate
		if (userCamMode == IDLE_MODE) {
			// notify cameras of idle mode
            // Send information to c
            message = "Idl";
		} else if (userCamMode == AUTOMATIC_MODE){
			// notify cameras of automatic mode
            message = "Aut";
        } else {
            // notify cameras of movie mode
            message = "Mov";
        }
		updatedMode = false;
		return message;
	}

}
