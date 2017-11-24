

class DisplayThread extends Thread{
	
	monitor m;
	
	DisplayThread(monitor mon){
		m = mon;
	}
	
	public void run() {
		while(!isInterrupted()) {
			m.display();
		}
	}
	
}
