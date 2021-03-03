package com.alchemist.service;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.StreamNotifierRunner;

public class StreamNotifierService extends ListenerAdapter implements Service {
	public StreamNotifierService() {
		logger = LoggerFactory.getLogger(StreamNotifierService.class);
	}
	
	@Override
	public void onReady(ReadyEvent event) {
		try {
			StreamNotifierConfig config = readConfig();
			streamNotifierRunner = new StreamNotifierRunner(
					event.getJDA(),
					config.memberName,
					config.targetChannel,
					config.roleId);
			streamNotifierRunner.start();
			logger.info("Stream notifier ready!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("Error occured when reading config file, skip runner build.");
		}
	}
	
	public void terminate() {
		// if runner did not build on ready
		if (streamNotifierRunner == null)
			return;
		
		logger.info("Terminating stream notifier...");
		try {
			streamNotifierRunner.sendMessage("stop");
			streamNotifierRunner.interrupt();
			streamNotifierRunner.join();
		} catch (InterruptedException e) {
			logger.warn("Interrupt occured when stopping runner.");
			e.printStackTrace();
		}
	}
	
	private StreamNotifierConfig readConfig() throws FileNotFoundException {		
		Scanner scanner = new Scanner(new File("config/stream_notification.json"));
		scanner.useDelimiter("\\Z");
		JSONObject json = new JSONObject(scanner.next());
		scanner.close();
		
		return new StreamNotifierConfig(json.getString("member_name"),
								        json.getLong("target_channel"),
								        json.getLong("ping_role_id"));
	}
	
	private class StreamNotifierConfig {
		private final String memberName;
		private final long targetChannel;
		private final long roleId;
		public StreamNotifierConfig(String memberName, long targetChannel, long roleId) {
			this.memberName = memberName;
			this.targetChannel = targetChannel;
			this.roleId = roleId;
		}
	}

	private Logger logger;
	private StreamNotifierRunner streamNotifierRunner = null;
}
