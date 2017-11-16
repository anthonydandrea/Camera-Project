public class DataObject {

	private byte[] image;
	private long timestamp;
	
	DataObject(byte[] image, long timestamp){
		for(int i=0;i<image.length;i++){
			this.image[i]=image[i];
		}		
		this.timestamp=timestamp;
	}

	public long getTimestamp() {
		return this.timestamp;
	}
	
	public byte[] getImage() {
		byte[] ans = new byte[image.length];
		for(int i=0;i<image.length;i++){
			ans[i]=image[i];
		}
		return ans;
	}
	
}
