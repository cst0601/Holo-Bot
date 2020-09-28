package com.alchemist;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import com.alchemist.service.ManualListener;
import com.alchemist.service.PingListener;
import com.alchemist.service.RollListener;
import com.alchemist.service.VtubeListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class DiscordMpNext {
	private JDA jda;
	private Logger logger;
	
	public static void main(String[] args) {
		try {
			new DiscordMpNext().startUp();
		} catch (LoginException e) {
			e.printStackTrace();
		}	    
	}
	
	/**
	 * Loads some confidential informations [Discord bot token / youtube api
	 * key] and creates JDA
	 * @throws LoginException
	 */
	private void startUp () throws LoginException {
		logger = Logger.getLogger(DiscordMpNext.class.getName());
		// load token from config
		Properties properties = new Properties();
		try {
			properties.load(DiscordMpNext.class.getResourceAsStream("/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("Failed to read from config.properties, please" +
					      "check if the file exists.");
			return;
		}
		
		String token = properties.getProperty("token", null);
		String ytKey = properties.getProperty("yt_key", null);
		
		try {
			jda = JDABuilder.createDefault(token)
					.addEventListeners(new PingListener())
					.addEventListeners(new RollListener())
					.addEventListeners(new ManualListener())
					.addEventListeners(new VtubeListener(ytKey))
					.addEventListeners(new TwitterBroadcaster())
					.setActivity(Activity.of(Activity.ActivityType.DEFAULT,
								 			 "Say >man to seek help!"))
					.build();
			jda.awaitReady();
			
			logger.info("Finish building JDA!");
		} catch (InterruptedException e) {
			// await is a blocking method, if interrupted
			e.printStackTrace();
		} catch (LoginException e) {
			// things go wrong in authentication
			e.printStackTrace();
		}
		
	}
	
}
