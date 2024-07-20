package com.alchemist;

import org.json.JSONObject;

/**
 * Read config for each notification.
 */
public class ConfigNotification {
	public ConfigNotification (JSONObject json) {
		targetChannelId = json.getLong("target_channel");
		pingRoleId = json.optLong("ping_role_id");
		
		JSONObject membershipConfig = json.optJSONObject("membership_config");
		if (membershipConfig != null) {
			membershipTargetChannelId = membershipConfig.getLong("target_channel");
			additionalMessage = membershipConfig.getString("additional_message");
		}
		else {
			membershipTargetChannelId = 0;
			additionalMessage = null;
		}
	}
	
	public final String additionalMessage;
	public final long pingRoleId, targetChannelId, membershipTargetChannelId;
}
