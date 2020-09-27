package com.alchemist;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * Workhorse of searching new tweets and send messages
 * @author chikuma
 *
 */
public class TwitterBroadcasterRunner extends Thread {
	public TwitterBroadcasterRunner(JDA jda, ArrayList<TwitterSubscription> subscriptions) {
		logger = Logger.getLogger(TwitterBroadcasterRunner.class.getName());
		this.jda = jda;
		this.subscriptions = subscriptions;
	}
	
	public void run() {
		while (true) {
			String message;		// stop if received stop message
			if ((message = messageBox.poll()) != null) {
				if (message.equals("stop")) {
					logger.info("Terminating twitter broascater runner...");
					break;
				}
			}
			
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (Long channelId: subscriptions.get(0).getTargetChannels()) {
				MessageChannel channel = jda.getTextChannelById(channelId);
				channel.sendMessage("test").queue();
			}
		}
	}
	
	public void sendMessage(String message) throws InterruptedException {
		messageBox.put(message);
	}
	
	private JDA jda;
	private Logger logger;
	private ArrayList<TwitterSubscription> subscriptions;
	private BlockingQueue<String> messageBox = new LinkedBlockingQueue<String>();
}
