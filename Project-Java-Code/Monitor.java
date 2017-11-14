// TO DO:
	// KNOW HOW PICTURES ARE REPRESENTED
	// MAKE GUI CLASSES
	// CREATE BUTTON HANDLER
	// MAKE IMAGE LIST -- CLASS AND LISTS
	// COMPLETE METHODS


public class Monitor {
	/*

	// Attributes here
	int cameraMode; // MOVIE vs IDLE
	int lastCamMode;
	int lastViewingMode;
	int viewingMode; // SYNCHRONOUS vs ASYNCHRONOUS
	// Image list camera 1
	// Image list camera 2
	boolean newImage; // new picture available
	// CLASS IMAGE w/ IMAGE AND TIMESTAMP
	boolean modeChanged;
	int frames;

	synchronized void addImage(boolean none, int image) {
		// Check if none == new image
		if(none == true) {
			return;
		}
		// Adds a new image thats been taken
		// Updates the timestamps and images
		// if mode == synch? disp?
		// Check if mode changes
		// Alerts other threads
		notifyAll();
	}

	synchronized void changeMode(int type, int newMode) {
		// changes either viewing mode or cam mode
		if(type == 0) {
			if(newMode == cameraMode) {
				// No new mode
				return;
			} else {
				lastCamMode = cameraMode;
				cameraMode = newMode;
				modeChanged = true;
				notifyAll();
			}
		} else {
			if(newMode == viewingMode) {
				// No new mode
				return;
			} else {
				lastViewingMode = viewingMode;
				viewingMode = newMode;
				modeChanged = true;
				notifyAll();
			}
		}
		// alerts other threads
	}

	synchronized void  display() {
		// wait until new image available
		// find the waiting time
		// imageTime = smallest amount of time until the next image is shown
		while(imageTime > System.currentTimeMillis() && !newImage) {
			try {
				wait(imageTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// recheck image times
		}
		if(newImage) {
			// check if mode changes
		} else {
			// displays the next image
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
	*/
}
