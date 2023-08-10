package com.alchemist;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

import com.alchemist.service.*;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;


public class DiscordMpNext {
	private JDA jda;
	private Logger logger;
	
	public static void main(String[] args) {
		try {
			new DiscordMpNext().startUp(args);
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads some confidential informations [Discord bot token / youtube api
	 * key] and creates JDA
	 * @throws LoginException
	 */
	private void startUp (String args[]) throws LoginException {
		logger = LoggerFactory.getLogger(DiscordMpNext.class);
		// load token from config
		Properties properties = new Properties();
		try {
			properties.load(DiscordMpNext.class.getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Failed to read from config.properties, please" +
					     "check if the file exists.");
			return;
		}
		
		String token = properties.getProperty("token", null);
		
		try {
			JDABuilder builder = JDABuilder.createDefault(token)
					.enableIntents(GatewayIntent.MESSAGE_CONTENT)
					.addEventListeners(new PingListener())	// system commands
					.addEventListeners(new RollListener())
					.addEventListeners(new ManualListener())
					.addEventListeners(new VtubeListener())
					.addEventListeners(new AboutListener())
					.addEventListeners(new BonkListener())
					.addEventListeners(new CountDownListener())	// special event
					.addEventListeners(new StreamNotifierService())
					.setActivity(Activity.of(Activity.ActivityType.PLAYING,
								 			 "Say >man to seek help!"));
			
			builder = buildOptionalService(builder, properties);
			
			jda = builder.build();
			jda.awaitReady();
						
			logger.info("Finish building JDA!");
		} catch (InterruptedException e) {
			// await is a blocking method, if interrupted
			e.printStackTrace();
		}
	}
	
	/**
	 * Builds optional service if specified in .properties file. Currently
	 * includes twitter broadcast service and member verification service.
	 * @param builder
	 * @param properties
	 * @return
	 */
	private JDABuilder buildOptionalService(JDABuilder builder, Properties properties) {
		if (Boolean.parseBoolean(properties.getProperty("twitter"))) {
			builder.addEventListeners(new TwitterBroadcastService());
		}
		else {
			logger.info("Twitter mode is disabled.");
		}
		
		if (Boolean.parseBoolean(properties.getProperty("member_verification"))) {
			builder.addEventListeners(new MemberVerificationService());
		}
		else {
			logger.info("Member verification service is disabled.");
		}
		
		return builder;
	}
}
