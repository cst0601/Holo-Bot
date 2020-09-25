package com.alchemist;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

import org.json.JSONArray;

import net.dv8tion.jda.api.JDA;

public class TwitterBroadcaster {
	public TwitterBroadcaster(JDA jda) {
		logger = Logger.getLogger(TwitterBroadcaster.class.getName());
		broadcastRunner = new TwitterBroadcasterRunner(jda, readBroadcastConfig());
	}
	
	public void start() {
		logger.info("twitter broadcaster ready!!!!");
		broadcastRunner.start();
	}
	
	private ArrayList<TwitterSubscription> readBroadcastConfig() {
		ArrayList<TwitterSubscription> subscriptions = new ArrayList<TwitterSubscription>();
		
		try {
			Scanner scanner = new Scanner(new File("config/broadcast.json"));
			scanner.useDelimiter("\\Z");
			JSONArray json = new JSONArray(scanner.next());
			scanner.close();
			
			for (int i = 0; i < json.length(); ++i) {
				JSONArray targetJson = json.getJSONObject(i).getJSONArray("target");
				ArrayList<Long> targetChannels = new ArrayList<Long>();
				
				for (int j = 0; j < targetJson.length(); ++j)
					targetChannels.add(targetJson.getLong(j));

				subscriptions.add(
					new TwitterSubscription(
						json.getJSONObject(i).getString("hashtag"), targetChannels));
			}
			
		} catch (Exception e) {
			logger.severe("Cannot read broadcast config file.");
		}
		
		return subscriptions;
	}
	
	private TwitterBroadcasterRunner broadcastRunner;
	private Logger logger;
}
