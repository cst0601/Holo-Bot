package com.alchemist.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.TwitterBroadcasterRunner;
import com.alchemist.TwitterSubscription;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterBroadcaster extends ListenerAdapter implements Service {
	public TwitterBroadcaster() {
		logger = LoggerFactory.getLogger(TwitterBroadcaster.class);
	}
	
	@Override
	public void onReady(ReadyEvent event) {
		broadcastRunner = new TwitterBroadcasterRunner(
				event.getJDA(), initTwitterApi(), readBroadcastConfig());
		broadcastRunner.start();
		logger.info("Twitter broadcaster ready!!!!");
	}
	
	public void terminate() {
		logger.info("Terminating twitter broadcaster...");
		try {
			broadcastRunner.sendMessage("stop");
			broadcastRunner.interrupt();
			broadcastRunner.join();
		} catch (InterruptedException e) {
			logger.warn("Interrupt occurred when stopping runner.");
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
						json.getJSONObject(i).getString("query"), targetChannels));
			}
			
		} catch (Exception e) {
			logger.error("Failed to read broadcast config file.");
		}
		
		return subscriptions;
	}
	
	/**
	 * reads twitter4j properties and init an instance
	 * @return
	 */
	private Twitter initTwitterApi() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("config/credentials/twitter4j.properties"));
		} catch (Exception e) {
			logger.error("Failed to read twitter4j token file");
			return null;
		}
		
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		configBuilder.setDebugEnabled(Boolean.parseBoolean(properties.getProperty("debug")))
			.setOAuthConsumerKey(properties.getProperty("oauth.consumerKey"))
			.setOAuthConsumerSecret(properties.getProperty("oauth.consumerSecret"))
			.setOAuthAccessToken(properties.getProperty("oauth.accessToken"))
			.setOAuthAccessTokenSecret(properties.getProperty("oauth.accessTokenSecret"));
		TwitterFactory tf = new TwitterFactory(configBuilder.build());
		return tf.getInstance();
	}
	
	private TwitterBroadcasterRunner broadcastRunner;
	private Logger logger;
}
