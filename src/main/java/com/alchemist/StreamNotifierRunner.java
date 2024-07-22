package com.alchemist;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

/**
 * Workhorse of stream notification
 * @author chikuma
 *
 */
public class StreamNotifierRunner extends Thread {
	
	public StreamNotifierRunner(JDA jda, BlockingQueue<String> messageBox) {
		Thread.currentThread().setName("StreamNotifierRunner");
		logger = LoggerFactory.getLogger(StreamNotifierRunner.class);
		config = Config.getConfig();
		this.jda = jda;
		
		serviceMessageBox = messageBox;
		api = new HoloDexApi();
		upcomingStreams = new LinkedList<UpcomingStream>();
		
		// TODO: add some more message in member target channel
		// memberTargetChannel = jda.getTextChannelById(config.membershipTargetChannelId);
		this.memberName = config.memberName;
	}
	
	public void run() {
		updateUpcomingStreams();	// init upcoming stream list
		notifyUpcomingStreams(false);
		
		while (true) {
			List<String> message;
			try {
				if ((message = messageBox.poll()) != null) {
					if (message.get(0).equals("stop")) {
						logger.info("StreamNotifierRunner terminating...");
						break;
					}
					else if (message.get(0).equals("flush")) {
						upcomingStreams.clear();
						updateUpcomingStreams();
						notifyUpcomingStreams(false);
						serviceMessageBox.put("Flushed all cached streams.");
						logger.info("Flushed all cached streams.");
					}
					else if (message.get(0).equals("list")) {
						listAllUpcomingStreams(message.get(1));
						logger.info("listed all upcoming stream");
					}
				}
				else {
					updateUpcomingStreams();
					notifyUpcomingStreams(true);
					sleep(60000);	// 1 min
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		logger.info("StreamNotifierRunner exit run.");
	}
	
	public void sendMessage(List<String> message) throws InterruptedException {
		messageBox.put(message);
	}
	
	private int containsUpcomingStream(UpcomingStream newStream) throws NoSuchElementException {
		for (int i = 0; i < upcomingStreams.size(); ++i) {
			if (upcomingStreams.get(i).getStreamUrl().equals(newStream.getStreamUrl()))
				return i;
		}
		throw new NoSuchElementException("Stream does not exist");
	}
	
	private void updateUpcomingStreams() {
		try {
			List<UpcomingStream> updateStream = new ArrayList<UpcomingStream>();
			for (LiveStream stream: api.getStreamOfMember(memberName, "upcoming")) {
				UpcomingStream upcomingStream = new UpcomingStream(stream, jda);
				if (!upcomingStream.hasStarted()) {	// api might give stream started but state = upcoming
					updateStream.add(upcomingStream);
				}
			}
			
			for (UpcomingStream stream: updateStream) {
				// if stream does not exist, add it to list
				// if does exist, check if scheduled start time needs update
				// TODO: if stream exist in cache but no longer in yt, delete it
				try {
					int streamIndex = containsUpcomingStream(stream);
					ArrayList<MessageCreateBuilder> updateMessages = upcomingStreams
							.get(streamIndex)
							.checkStreamStartTime(stream);
					
					if (updateMessages != null) {
						sendMessageToChannels(updateMessages, upcomingStreams.get(streamIndex));
						logger.info("Updated stream start time " + stream.toString());
					}
					
				} catch (NoSuchElementException e) {
					upcomingStreams.add(stream);
					if (!stream.hasStarted()) {
						logger.info("New upcocmming stream " + stream.toString());
					}
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("Failed to update upomming streams.");
		}
	}
	
	private void notifyUpcomingStreams(boolean sendMessage) {
		ListIterator<UpcomingStream> iter = upcomingStreams.listIterator();
		while(iter.hasNext()) {
			UpcomingStream stream = iter.next();
			ArrayList<MessageCreateBuilder> messageBuilders = stream.broadcast();

			if (messageBuilders != null && sendMessage) {
				sendMessageToChannels(messageBuilders, stream);
				logger.info("Notified stream: " + stream.getStreamUrl());	
			}
			if (stream.hasStarted()) {
				logger.info("Remove started stream: " + stream.getStreamUrl());
				iter.remove();
			}
		}
	}
	
	private void listAllUpcomingStreams(String channelId) throws InterruptedException {
		MessageChannel channel = jda.getTextChannelById(channelId);
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle("Upcoming Streams")
				.setColor(Color.red)
				.setDescription("List all upcoming streams");
		String message = "# Upcoming streams\n";
		String formattedStreamList = "";
		for (UpcomingStream stream: upcomingStreams) {
			message += stream.toString() + "\n";
			formattedStreamList += stream.toMarkdownString();
		}
		serviceMessageBox.put(message);
		logger.info(message);
		builder.addField("List (start_time, url, state)", formattedStreamList, false);
		channel.sendMessageEmbeds(builder.build()).queue();
	}
	
	private void sendMessageToChannels(ArrayList<MessageCreateBuilder> messageBuilders, UpcomingStream stream) {
		messageBuilders = stream.appendMemberOnlyMessage(messageBuilders);
		for (int i = 0; i < config.notifications.size(); i++) {
			MessageChannel channel = jda.getTextChannelById(config.notifications.get(i).targetChannelId);
			channel.sendMessage(messageBuilders.get(i).build()).queue();

		}
	}
	
	private JDA jda;
	private Logger logger;
	private Config config;
	private BlockingQueue<List<String>> messageBox = new LinkedBlockingQueue<List<String>>();
	private BlockingQueue<String> serviceMessageBox;
	private HoloDexApi api;
	private String memberName;
	private List<UpcomingStream> upcomingStreams;	// Welp, upcoming stream usually does not have a lot, so...
}