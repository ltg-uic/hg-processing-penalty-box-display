package ltg.foraging;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ltg.commons.MessageListener;
import ltg.commons.SimpleXMPPClient;
import ltg.foraging.model.ForagingGame;
import ltg.foraging.model.RFIDTag;

import org.jivesoftware.smack.packet.Message;

import processing.core.PApplet;
import processing.core.PFont;

import com.github.jsonj.JsonArray;
import com.github.jsonj.JsonElement;
import com.github.jsonj.JsonObject;
import com.github.jsonj.exceptions.JsonParseException;
import com.github.jsonj.tools.JsonParser;


public class ForagingPenaltyBox extends PApplet {
	private static final long serialVersionUID = 1L;
	private static final String INIT_MESSAGE = "{\"event\":\"penalty_box_init\",\"payload\":{}}";
	private static final float MAX_BAR_WIDTH = .94f;

	
	// Data collection flag
	private boolean collectData = false;
	// XMMP client
	private SimpleXMPPClient xmpp = null;
	// JSON parser
	private JsonParser parser = new JsonParser();
	// Foraging game
	private ForagingGame fg = new ForagingGame();
	// Font
	private PFont labelsFont;


	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "ForagingPenaltyBox" });
	}



	////////////////////////
	// Processing methods //
	////////////////////////


	public void setup() {
		// Sketch
		frameRate(1);
		size(displayWidth/2, displayHeight/2);
		labelsFont = createFont("Helvetica",16,true);
		// Logic
		xmpp = new SimpleXMPPClient("fg-penalty-box@ltg.evl.uic.edu", "fg-penalty-box", "fg-pilot-oct12@conference.ltg.evl.uic.edu");
		println("Connected to chatroom and listening");
		xmpp.registerEventListener(new MessageListener() {
			@Override
			public void processMessage(Message m) {
				processIncomingData(m.getBody());
			}
		});
	}


	public void draw() {
		if (!collectData)
			return;
		fg.updateTimes();
		updateGrid(width, height);
		updateBars(width, height);
	}



	/////////////////////
	// Drawing methods //
	/////////////////////

	private void updateGrid(float w, float h) {
		background(0);
		stroke(255);
		line(.03f*w, .03f*h, 	.03f*w, .97f*h); 	// Vertical axis
		line(.025f*w, .265f*h, 	.035f*w, .265f*h);	// Tic 1
		line(.025f*w, .5f*h, 	.035f*w, .5f*h);	// Tic 2
		line(.025f*w, .735f*h, 	.035f*w, .735f*h);	// Tic 3
		fill (255);
		textFont(labelsFont);
		textAlign(CENTER);
		int thh = (int) (textAscent()+textDescent())/2;
		text("A", .015f*w-textWidth("A")/2, .1475f*h+thh);
		text("B", .015f*w-textWidth("A")/2, .3825f*h+thh);
		text("C", .015f*w-textWidth("A")/2, .6175f*h+thh);
		text("D", .015f*w-textWidth("A")/2, .8525f*h+thh);
	}


	private void updateBars(float w, float h) {
		if(!fg.isInitialized())
			return;
		float m = MAX_BAR_WIDTH*w;
		noStroke();
		// Cluster A
		fill(unhex(fg.getTagColor(1)));
		rect(.03f*w+1, .0475f*h, fg.getRemainingPBTime(1)*m, .04f*h);
		fill(unhex(fg.getTagColor(2)));
		rect(.03f*w+1, .0875f*h, fg.getRemainingPBTime(2)*m, .04f*h);
		fill(unhex(fg.getTagColor(3)));
		rect(.03f*w+1, .1275f*h, fg.getRemainingPBTime(3)*m, .04f*h);
		fill(unhex(fg.getTagColor(4)));
		rect(.03f*w+1, .1675f*h, fg.getRemainingPBTime(4)*m, .04f*h);
		// Cluster B
		fill(unhex(fg.getTagColor(5)));
		rect(.03f*w+1, .2825f*h, fg.getRemainingPBTime(5)*m, .04f*h);
		fill(unhex(fg.getTagColor(6)));
		rect(.03f*w+1, .3225f*h, fg.getRemainingPBTime(6)*m, .04f*h);
		fill(unhex(fg.getTagColor(7)));
		rect(.03f*w+1, .3625f*h, fg.getRemainingPBTime(7)*m, .04f*h);
		fill(unhex(fg.getTagColor(8)));
		rect(.03f*w+1, .4025f*h, fg.getRemainingPBTime(8)*m, .04f*h);
		// Cluster C
		fill(unhex(fg.getTagColor(9)));
		rect(.03f*w+1, .5175f*h, fg.getRemainingPBTime(9)*m, .04f*h);
		fill(unhex(fg.getTagColor(10)));
		rect(.03f*w+1, .5575f*h, fg.getRemainingPBTime(10)*m, .04f*h);
		fill(unhex(fg.getTagColor(11)));
		rect(.03f*w+1, .5975f*h, fg.getRemainingPBTime(11)*m, .04f*h);
		fill(unhex(fg.getTagColor(12)));
		rect(.03f*w+1, .6375f*h, fg.getRemainingPBTime(12)*m, .04f*h);
		// Cluster D
		fill(unhex(fg.getTagColor(13)));
		rect(.03f*w+1, .7525f*h, fg.getRemainingPBTime(13)*m, .04f*h);
		fill(unhex(fg.getTagColor(14)));
		rect(.03f*w+1, .7925f*h, fg.getRemainingPBTime(14)*m, .04f*h);
		fill(unhex(fg.getTagColor(15)));
		rect(.03f*w+1, .8325f*h, fg.getRemainingPBTime(15)*m, .04f*h);
		fill(unhex(fg.getTagColor(16)));
		rect(.03f*w+1, .8725f*h, fg.getRemainingPBTime(16)*m, .04f*h);
		fill(unhex(fg.getTagColor(17)));
		rect(.03f*w+1, .9125f*h, fg.getRemainingPBTime(17)*m, .04f*h);
	}

	


	////////////////////////////
	// Event handling methods //
	////////////////////////////

	public void processIncomingData(String s) {
		JsonObject json = null;
		JsonElement jsone = null;
		try {
			jsone = parser.parse(s);
			if (!jsone.isObject()) {
				// The element is not an object... bad...
				return;
			}
			json = jsone.asObject();
			// Pick the right JSON handler based on the event type
			if (isPenaltyBoxInit(json)) {
				penaltyBoxInitInfo(json);
			}else if (isBunnyKill(json)) {
				killBunny(json);
			}else if (isGameReset(json)) {
				resetGame();
			}else if (isGameStop(json)) {
				stopGame();
			}
		} catch (JsonParseException e) {
			// Not JSON... skip
			//System.err.println("Not JSON: " + s);
		}
	}


	private void stopGame() {
		collectData = false;
	}


	private void resetGame() {
		System.out.println("Resetting penalty box...");
		xmpp.sendMessage(INIT_MESSAGE);
	}


	private void killBunny(JsonObject json) {
		if (!collectData)
			return;
		fg.killBunny(json.getString("payload", "id"));
	}


	private void penaltyBoxInitInfo(JsonObject json) {
		System.out.println("Init info received");
		// Process tags
		List<RFIDTag> tags = new ArrayList<RFIDTag>();
		JsonArray jTags = json.getArray("payload", "tags");
		for (JsonElement jt: jTags) {
			JsonObject t = ((JsonObject) jt);
			tags.add(new RFIDTag(t.getString("tag"), t.getString("cluster"), t.getString("color")));
		}
		// Sort tags according to cluster and tagID
		Collections.sort(tags, new Comparator<RFIDTag>() {
			@Override
			public int compare(RFIDTag t1, RFIDTag t2) {
				 int clusterDiff = t1.cluster.compareTo(t2.cluster);
	                return clusterDiff == 0 ? 
	                        t1.id.compareTo(t2.id) :
	                        clusterDiff;
			}
		});
		// Init game
		fg.resetGame(tags, json.getInt("payload", "penalty"));
		// Enable data collection
		collectData = true;
	}


	private boolean isGameReset(JsonObject json) {
		if (json.getString("event")!= null && 
				json.getString("event").equals("game_reset"))
			return true;
		return false;
	}
	
	
	private boolean isGameStop(JsonObject json) {
		if (json.getString("event")!= null && 
				json.getString("event").equals("game_stop"))
			return true;
		return false;
	}


	private boolean isBunnyKill(JsonObject json) {
		if (json.getString("event")!= null && 
				json.getString("event").equals("kill_bunny") && 
				json.getString("destination")!= null && 
				json.getString("payload", "id") != null)
			return true;
		return false;
	}


	private boolean isPenaltyBoxInit(JsonObject json) {
		if (json.getString("event")!= null && 
				json.getString("event").equals("penalty_box_init_data") &&  
				json.getArray("payload", "tags")!= null)
			return true;
		return false;
	}

}
