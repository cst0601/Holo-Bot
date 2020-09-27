package com.alchemist;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

import org.json.JSONArray;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TwitterBroadcaster extends ListenerAdapter {
	public TwitterBroadcaster() {
		logger = Logger.getLogger(TwitterBroadcaster.class.getName());
	}
	
	@Override
	public void onReady(ReadyEvent event) {
		broadcastRunner = new TwitterBroadcasterRunner(event.getJDA(), readBroadcastConfig());
		broadcastRunner.start();
		logger.info("twitter broadcaster ready!!!!");
	}
	
	public void terminate() {
		logger.info("terminating twitter broadcaster...");
		try {
			broadcastRunner.sendMessage("stop");
			broadcastRunner.join();
		} catch (InterruptedException e) {
			logger.warning("Interrupt occurred when stopping runner.");
			e.printStackTrace();
		}
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
