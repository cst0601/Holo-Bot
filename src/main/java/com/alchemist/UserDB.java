package com.alchemist;

import static com.mongodb.client.model.Filters.eq;

import javax.annotation.Nullable;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.alchemist.exceptions.EntryExistException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

public class UserDB {
	private MongoClient client;
	private MongoDatabase database;
	
	public UserDB () {
		client = MongoClients.create("mongodb://localhost:27017");
		database = client.getDatabase("test_db");
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
	
	public void test() {
		client = MongoClients.create("mongodb://localhost:27017");
		database = client.getDatabase("test_db");
		
		Document newDoc = new Document("dc_id", "198340280146067456")
				.append("yt_id", "UCPAPC9V8oLmJ41nUkvbNwDQ")
				.append("is_member", true)
				.append("verify_timestamp", "2022-03-05T13:49:51.141Z");
		
		database.getCollection("users").insertOne(newDoc);
		
		Document result = database.getCollection("users").find(eq("dc_id", "198340280146067456")).first();
		System.out.println(result.get("verify_timestamp"));
	}

	public void verifyUser(String discordId) {
		Bson updates = Updates.combine(
				Updates.set("is_member", true),
				Updates.set("verify_timestamp", "2022-03-05T13:49:51.141Z"));
		
		database.getCollection("users").updateOne(
				new Document().append("dc_id", discordId),
				updates,
				new UpdateOptions().upsert(true));
	}
}
