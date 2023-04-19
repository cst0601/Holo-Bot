package com.alchemist;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.JSONObject;

/*
 * Basically a data struct, by java's code convention, member variables are public
 */
public class StreamNotifierConfig {
	
	public final String memberName;
	public final long targetChannelId;
	public final long pingRoleId;
	
	public final boolean membershipMode;
	public final String speculateName;
	public final long membershipTargetChannel;
	public final String additionalMessage;
	
	
	public StreamNotifierConfig () throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("config/stream_notification.json"));
		scanner.useDelimiter("\\Z");
		JSONObject json = new JSONObject(scanner.next());
		scanner.close();
		
		memberName = json.getString("member_name");
		targetChannelId = json.getLong("target_channel");
		pingRoleId = json.getLong("ping_role_id");
		
		if (json.has("membership_config")) {
			membershipMode = true;
			JSONObject membershipConfig = json.getJSONObject("membership_config");
			speculateName = membershipConfig.getString("speculate_name");
			membershipTargetChannel = membershipConfig.getLong("target_channel");
			additionalMessage = membershipConfig.getString("additional_message");
		}
		else {
			membershipMode = false;
			speculateName = null;
			membershipTargetChannel = 0;
			additionalMessage = null;
		}
	}
	
	public boolean isMembershipMode() {
		return membershipMode;
	}
}
