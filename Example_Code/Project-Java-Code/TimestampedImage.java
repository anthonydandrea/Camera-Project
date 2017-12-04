class TimestampedImage{
	public byte[] image;
	public long timeStamp;
	
	public TimestampedImage(byte[] i, long t) {
		timeStamp = t;
		image = i;
	}
	
}