package com.alchemist;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;

/**
 * Workhorse of stream notification
 * @author greg8
 *
 */
public class StreamNotifierRunner extends Thread {
	public StreamNotifierRunner(JDA jda, String memberName,
			long messageChannelId, long pingId, BlockingQueue<String> messageBox) {
		Thread.currentThread().setName("StreamNotifierRunner");
		logger = LoggerFactory.getLogger(StreamNotifierRunner.class);
		serviceMessageBox = messageBox;
		api = new HoloToolsApi();
		upcomingStreams = new LinkedList<UpcomingStream>();
		targetChannel = jda.getTextChannelById(messageChannelId);
		pingRole = jda.getRoleById(pingId);
		this.memberName = memberName;
	}
	
	public void run() {
		updateUpcomingStreams();	// init upcoming stream list
		notifyUpcomingStreams(false);
		
		while (true) {
			String message;
			try {
				if ((message = messageBox.poll()) != null) {
					if (message.equals("stop")) {
						logger.info("StreamNotifierRunner terminating...");
						break;
					}
					else if (message.equals("flush")) {
						upcomingStreams.clear();
						updateUpcomingStreams();
						notifyUpcomingStreams(false);
						serviceMessageBox.put("Flushed all cached streams.");
						logger.info("Flushed all cached streams.");
					}
					else if (message.equals("list")) {
						listAllUpcomingStreams();
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
	
	public void sendMessage(String message) throws InterruptedException {
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
				UpcomingStream upcomingStream = new UpcomingStream(stream, pingRole);
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
					Message updateMessage = upcomingStreams.get(streamIndex).checkStreamStartTime(stream);
					
					if (updateMessage != null) {
						targetChannel.sendMessage(updateMessage).queue();
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
			Message message = stream.broadcast();
			if (message != null && sendMessage) {
				targetChannel.sendMessage(message).queue();
				logger.info("Notified stream: " + stream.getStreamUrl());	
			}
			if (stream.hasStarted()) {
				logger.info("Remove started stream: " + stream.getStreamUrl());
				iter.remove();
			}
		}
	}
	
	private void listAllUpcomingStreams() throws InterruptedException {
		
		String message = "# Upcoming streams\n";
		for (UpcomingStream stream: upcomingStreams) {
			message += stream.toString() + "\n";
		}
		serviceMessageBox.put(message);
		logger.info(message);
	}
	
	private Logger logger;
	private BlockingQueue<String> messageBox = new LinkedBlockingQueue<String>();
	private BlockingQueue<String> serviceMessageBox;
	private HoloToolsApi api;
	private String memberName;
	private MessageChannel targetChannel;
	private Role pingRole;
	private List<UpcomingStream> upcomingStreams;	// Welp, upcoming stream usually does not have a lot, so...
}