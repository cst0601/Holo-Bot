package com.alchemist;

import java.util.ArrayList;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

public class TwitterBroadcasterRunner extends Thread {
	public TwitterBroadcasterRunner(JDA jda, ArrayList<TwitterSubscription> subscriptions) {
		this.jda = jda;
		this.subscriptions = subscriptions;
	}
	
	public void run() {
		while (true) {
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
	
	private JDA jda;
	private ArrayList<TwitterSubscription> subscriptions;
	
}
