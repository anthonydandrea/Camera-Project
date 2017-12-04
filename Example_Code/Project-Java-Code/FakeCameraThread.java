
public class FakeCameraThread extends Thread {
	private int turn;
	monitor mon;
	byte[] im1;
	byte[] im2;
	byte[] im3;
	byte[] im4;
	int num;

	byte[] picture;

 public FakeCameraThread(byte[] i1, byte[] i2, byte[] i3, byte[] i4, monitor m, int n) {
	 mon = m;
	 im1 = i1;
	 im2 = i2;
	 im3 = i3;
	 im4 = i4;
	 turn = 0;
	 num = n;
 }

 public FakeCameraThread(monitor m, int n) {
	 mon = m;
	 turn = 0;
	 num = n;

 }

 public void handleNewImage(byte[] pic) {
	 if(pic != null)
	 picture = pic;
 }

 public void run() {
  try {
   while (true) {
    Thread.sleep(1000);
		mon.addImage(num, picture, System.currentTimeMillis(), false);
    /* (turn == 0) {
    		mon.addImage(num, im1, System.currentTimeMillis(), false);
    		turn = 1;
    } else if (turn == 1) {
    		mon.addImage(num, im2, System.currentTimeMillis(), false);
    		turn = 2;
    } else if (turn == 2) {
    		mon.addImage(num, im3, System.currentTimeMillis(), false);
    		turn = 3;
    } else {
    		mon.addImage(num, im4, System.currentTimeMillis(), false);
    		turn = 0;
    }*/
   }
  } catch (Exception e) {
   // Exit
  }
 }
 }
