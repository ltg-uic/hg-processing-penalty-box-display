package ltg.foraging.model;

import java.util.List;

public class ForagingGame {
	
	
	private List<RFIDTag> tags = null;
	private int penalty = -1; 
	
	
	public ForagingGame() {
	}
	
	
	public synchronized void resetGame(List<RFIDTag> tags, Integer penalty) {
		this.tags = tags;
		this.penalty = penalty.intValue();
	}
	
	
	public synchronized String getTagColor(int tagIndex) {
		return "ff"+tags.get(tagIndex-1).color.substring(1);
	}


	public synchronized boolean isInitialized() {
		if (tags!=null && penalty!=-1)
			return true;
		return false;
	}


	public void updateTimes() {
		for (RFIDTag t: tags) {
			if (t.remainingTime!=0) {
				t.remainingTime--;
			}
		}
		
	}


	public float getRemainingPBTime(int i) {
		return ((float) (((float)tags.get(i-1).remainingTime) / ((float)penalty)));
	}


	public void killBunny(String tagId) {
		for (RFIDTag t: tags) {
			if (t.id.equals(tagId))
				t.remainingTime = penalty;
		}
	}

	
}
