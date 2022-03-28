package com.alchemist;

import static com.mongodb.client.model.Filters.eq;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nullable;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.alchemist.exceptions.EntryExistException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

public class UserDB {
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
	private MongoClient client;
	private MongoDatabase database;
	
	public UserDB (String dbConnection, String dbName) {
		client = MongoClients.create(dbConnection);
		database = client.getDatabase(dbName);
	}
	
	public void register(String discordId, String youtubeId) throws EntryExistException {
		if (isUserRegistered(discordId)) {
			throw new EntryExistException("User already registered");
		}
		else if (database.getCollection("users").find(eq("yt_id", youtubeId)).first() != null ) {
			throw new EntryExistException("YoutubeId already registered.");
		}
		
		database.getCollection("users").insertOne(
				new Document("dc_id", discordId)
				.append("yt_id", youtubeId)
				.append("is_member", false));
	}
	
	public boolean isUserRegistered(String discordId) {
		return database.getCollection("users").find(eq("dc_id", discordId)).first() != null;
	}
	
	@Nullable
	public String getYoutubeIdByUserId(String discordId) {
		Document result = database.getCollection("users").find(eq("dc_id", discordId)).first();
		if (result == null) return null;
		return result.getString("yt_id");
	}

	/**
	 * writes verification record to DB
	 * @param discordId
	 * @return verification valid date (next time the user will want to re verify membership)
	 */
	public String verifyUser(String discordId) {
		ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.of("UTC"));
		String expireTime = timestamp.plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
		
		Bson updates = Updates.combine(
				Updates.set("is_member", true),
				Updates.set("verify_timestamp", timestamp.format(formatter)));
		
		database.getCollection("users").updateOne(
				new Document().append("dc_id", discordId),
				updates,
				new UpdateOptions().upsert(true));
		
		return expireTime;
	}
}
