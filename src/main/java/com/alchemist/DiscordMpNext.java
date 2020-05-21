package com.alchemist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class DiscordMpNext {

	public static void main(String[] args) {
		try {
			new DiscordMpNext().startUp();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	
	private void startUp () throws LoginException {
		// load toke from config
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("src/main/resources/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		String token = properties.getProperty("token", null);
		
		JDA jda = JDABuilder.createDefault(token).build();
	}
	
}
