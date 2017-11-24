
public class FakeCameraThread extends Thread {
	private int turn;
	monitor mon;
	byte[] im1;
	byte[] im2;
	int num;
	
 public FakeCameraThread(byte[] i1, byte[] i2, monitor m, int n) {
	 mon = m;
	 im1 = i1;
	 im2 = i2;
	 turn = 1;
	 num = n;
 }
 
 public void run() {
  try {
   while (true) {
    Thread.sleep(1000);
    if (turn == 1) {
    		mon.addImage(num, im1, System.currentTimeMillis(), false);
    		turn = 0;
    } else {
    		mon.addImage(num, im2, System.currentTimeMillis(), false);
    		turn = 1;
    }
   }
  } catch (InterruptedException e) {
   // Exit
  }
 }
 }