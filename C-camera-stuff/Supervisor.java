
class Supervisor extends Thread{
	
	Supervisor(monitor m){
		mon = m;
	}
	
	public void run(){
		
		while(!isInterrupted()) {
			mon.display();
		}
		
	}
	
