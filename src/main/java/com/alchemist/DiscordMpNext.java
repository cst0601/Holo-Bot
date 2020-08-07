package com.alchemist;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import com.alchemist.service.PingListener;
import com.alchemist.service.VtubeListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class DiscordMpNext {
	private JDA jda;

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
		// load token from config
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("src/main/resources/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to read from config.properties, please" +
							   "check if the file exists.");
			return;
		}
		
		String token = properties.getProperty("token", null);
		String ytKey = properties.getProperty("yt_key", null);
		
		try {
			jda = JDABuilder.createDefault(token)
					.addEventListeners(new PingListener())
					.addEventListeners(new VtubeListener(ytKey))
					.build();
			jda.awaitReady();
			System.out.println("Finish building JDA!");
		} catch (InterruptedException e) {
			// await is a blocking method, if interrupted
			e.printStackTrace();
		} catch (LoginException e) {
			// things go wrong in authentication
			e.printStackTrace();
		}
		
	}
	
}
