package com.alchemist.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.naming.ConfigurationException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.ArgParser;
import com.alchemist.StreamNotifierRunner;
import com.alchemist.exceptions.ArgumentParseException;

public class StreamNotifierService extends ListenerAdapter implements Service {
	
	public StreamNotifierService() {
		logger = LoggerFactory.getLogger(StreamNotifierService.class);
	}
	
	@Override
	public void onReady(ReadyEvent event) {
		try {
			StreamNotifierConfig config = readConfig();
			isNotifierConfigVaild(event.getJDA(), config.targetChannel, config.roleId);
			streamNotifierRunner = new StreamNotifierRunner(
					event.getJDA(),
					config.memberName,
					config.targetChannel,
					config.roleId,
					serviceMessageBox);
			streamNotifierRunner.start();
			logger.info("StreamNotifierService ready!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("Error occured when reading config file, skip runner build.");
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		MessageChannel channel = event.getChannel();
		String msg = event.getMessage().getContentDisplay();	// get readable version of the message

		if (event.isFromType(ChannelType.TEXT)) {
			Member member = event.getMember();
			parser = new ArgParser(msg);
			
			if (parser.getCommand().equals(">sudo") && member.hasPermission(Permission.ADMINISTRATOR)) {
				try { parser.parse(); } 
				catch (ArgumentParseException e1) { e1.printStackTrace(); }
				
				if (parser.getCommandSize() > 1) {
					if (parser.getCommand().equals(">sudo") &&
						parser.getCommand(1).equals("stream_flush")) {
					
						try {
							streamNotifierRunner.sendMessage("flush");
							streamNotifierRunner.interrupt();
							channel.sendMessage("upcoming stream cache cleared.").queue();
						} catch (InterruptedException e) {
							logger.warn("Interrupt occured when flushing stream cache.");
							e.printStackTrace();
						}
					}
					else if (parser.getCommand().equals(">sudo") &&
						parser.getCommand(1).equals("stream_list")) {
					
						try {
							streamNotifierRunner.sendMessage("list");
							streamNotifierRunner.interrupt();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			}
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
			logger.warn("Interrupt occured when stopping StreamNotifierRunner. Failed to terminate stream notifier.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Validates configuration for notifier runner. Check if text channel and role exist.
	 * @throws ConfigurationException
	 */
	private void isNotifierConfigVaild(JDA jda, long channelId, long roleId) throws ConfigurationException {
		logger.info("StreamNotifierService configuration verification start ...");
		if (jda.getTextChannelById(channelId) == null) {
			logger.error("Text channel with ID: " + channelId + " does not exist.");
			throw new ConfigurationException("StreamNotifierService configuraiton verification failed.");
		}
		else if (jda.getRoleById(roleId) == null) {
			logger.error("Role with ID: " + roleId + " does not exist.");
			throw new ConfigurationException("StreamNotifierService configuraiton verification failed.");
		}
		logger.info("StreamNotifierService configuration ... OK");
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
	private ArgParser parser = null;
	private BlockingQueue<String> serviceMessageBox = new LinkedBlockingQueue<String>();
}
