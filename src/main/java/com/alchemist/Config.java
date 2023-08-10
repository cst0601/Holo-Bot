package com.alchemist;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 * Config class for storing bot configurations, excluding credentials (keys).
 * @author chikuma
 *
 */
public class Config {
	
	private Config() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("config/stream_notification.json"));
		scanner.useDelimiter("\\Z");
		JSONObject json = new JSONObject(scanner.next());
		scanner.close();
		
		memberName = json.getString("member_name");
		targetChannelId = json.getLong("target_channel");
		pingRoleId = json.getLong("ping_role_id");
		
		JSONObject membershipConfig = json.getJSONObject("membership_config");
		speculateName = membershipConfig.getString("speculate_name");
		membershipTargetChannelId = membershipConfig.getLong("target_channel");
		additionalMessage = membershipConfig.getString("additional_message");
	}
	
	public static synchronized Config getConfig() {
		if (instance == null) {
			try {
				instance = new Config();
			} catch (FileNotFoundException e) {
				Logger logger = LoggerFactory.getLogger(Config.class);
				logger.error("Error finding stream notification config file.");
				e.printStackTrace();
			}
		}
		// TODO: add channel, role id, etc. verification --> reference the verify
		// method in StreamNofitiferService
		return instance;
	}
	
	public final String memberName, speculateName, additionalMessage;
	public final long pingRoleId, targetChannelId, membershipTargetChannelId;
		
	private static Config instance = null;
}
