package com.alchemist;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.bson.Document;

public class MemberRecord {
	private String discordId;
	private String youtubeId;
	private boolean isMember;
	private ZonedDateTime verifiedTime;
	private final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
	
	public MemberRecord(Document docRecord) {
		discordId = docRecord.getString("dc_id");
		youtubeId = docRecord.getString("yt_id");
		isMember = docRecord.getBoolean("is_member");
		verifiedTime = ZonedDateTime.parse(docRecord.getString("verified_time"), formatter);
	}
}
