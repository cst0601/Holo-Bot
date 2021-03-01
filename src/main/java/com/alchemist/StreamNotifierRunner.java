package com.alchemist;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * Workhorse of stream notification
 * @author greg8
 *
 */
public class StreamNotifierRunner extends Thread {
	public StreamNotifierRunner(JDA jda, String memberName, long messageChannelId) {
		logger = LoggerFactory.getLogger(StreamNotifierRunner.class);
		this.jda = jda;
		api = new HoloToolsApi();
		upcommingStreams = new LinkedList<UpcommingStream>();
		this.memberName = memberName;
		this.messageChannelId = messageChannelId;
		logger.warn(String.format("%d", messageChannelId));
	}
	
	public void run() {
		while (true) {
			String message;
			if ((message = messageBox.poll()) != null) {
				if (message.equals("stop")) {
					logger.info("Terminaing stream notifier runner...");
					break;
				}
			}
			else {
				updateUpcommingStreams();
				try {
					sleep(60000);	// 1 min
				} catch (InterruptedException e) {
					logger.info("Runner sleep interrupted");
				}
			}
		}
	}
	
	public void sendMessage(String message) throws InterruptedException {
		messageBox.put(message);
	}
	
	private void updateUpcommingStreams() {
		try {
			for (LiveStream stream: api.getStreamOfMember(memberName, "upcomming")) {
				UpcommingStream upcommingStream = new UpcommingStream(stream);
				logger.debug(String.format("api get stream: %s", stream.toString()));
				if (!upcommingStreams.contains(upcommingStream))
					upcommingStreams.add(upcommingStream);
			}	
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("Failed to update upomming streams.");
		}
		
		MessageChannel channel = jda.getTextChannelById(messageChannelId);
		
		ListIterator<UpcommingStream> iter = upcommingStreams.listIterator();
		while(iter.hasNext()) {
			UpcommingStream stream = iter.next();
			Message message = stream.broadcast();
			if (message != null) {
				channel.sendMessage(message).queue();
				logger.info("Notified stream");	
			}
			if (stream.hasStarted()) {
				logger.debug("remove started stream");
				iter.remove();
			}
		}
	}
	
	private JDA jda;
	private Logger logger;
	private BlockingQueue<String> messageBox = new LinkedBlockingQueue<String>();
	private HoloToolsApi api;
	private String memberName;
	private long messageChannelId;
	private List<UpcommingStream> upcommingStreams;
}
