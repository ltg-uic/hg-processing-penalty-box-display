package ltg.foraging.model;

public class RFIDTag {

	public String id;
	public String cluster;
	public String color;
	public int remainingTime;
	
	
	public RFIDTag(String id, String cluster, String color) {
		this.id = id;
		this.cluster = cluster;
		this.color = color;
		this.remainingTime = 0;
		}
		
		
		@Override
		public String toString() {
			return cluster + "|" + id;
		}

}
