package com.alchemist;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 * Config class for storing bot configurations, and API keys.
 * @author chikuma
 */
public class Config {

	private Config() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("config/stream_notification.json"));
		scanner.useDelimiter("\\Z");
		JSONObject json = new JSONObject(scanner.next());
		scanner.close();

		memberName = json.getString("member_name");
		speculateName = json.getString("speculate_name");
		Iterator<Object> notificationJson = json.getJSONArray("notifications").iterator();
		notifications = new ArrayList<ConfigNotification>();
		while (notificationJson.hasNext()) {
			notifications.add(new ConfigNotification((JSONObject)notificationJson.next()));
		}

		// discord token and API keys
		scanner = new Scanner(new File("config/credentials/credentials.json"));
		scanner.useDelimiter("\\Z");
		json = new JSONObject(scanner.next());
		scanner.close();

		discordToken = json.getString("discord_token");
		youtubeKey = json.getString("yt_api_key");
		holoDexApiKey = json.getString("holodex_api_key");
		isTwitterBroadcastServiceOn = json.getBoolean("twitter_broadcast");
		isTwitterUrlReplaceServiceOn = json.getBoolean("twitter_url_replace");
		isMemberVerificationServiceOn = json.getBoolean("member_verification");
	}

	public static synchronized Config getConfig() {
		if (instance == null) {
			try {
				instance = new Config();
			} catch (FileNotFoundException e) {
				Logger logger = LoggerFactory.getLogger(Config.class);
				logger.error("Error finding config files.");
				e.printStackTrace();
			}
		}
		// TODO: add channel, role id, etc. verification --> reference the verify
		// method in StreamNofitiferService
		return instance;
	}

	public final String memberName, speculateName;
	public final String discordToken;
	public final String youtubeKey;
	public final String holoDexApiKey;
	public final boolean isTwitterBroadcastServiceOn;
	public final boolean isTwitterUrlReplaceServiceOn;
	public final boolean isMemberVerificationServiceOn;
	public ArrayList<ConfigNotification> notifications;

	private static Config instance = null;
}
