package com.alchemist.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alchemist.ArgParser;
import com.alchemist.SyncMessage;
import com.alchemist.TwitterBroadcastConfig;
import com.alchemist.TwitterBroadcastRunner;
import com.alchemist.TwitterSubscription;
import com.alchemist.exceptions.ArgumentParseException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import twitter4j.Twitter;


public class TwitterBroadcastService extends ListenerAdapter implements Service {
	public TwitterBroadcastService() {
		logger = LoggerFactory.getLogger(TwitterBroadcastService.class);
	}
	
	@Override
	public void onReady(ReadyEvent event) {
		TwitterBroadcastConfig config;
		try {
			config = new TwitterBroadcastConfig();
			isTwitterBroadcastConfigVaild(event.getJDA(), config.getTwitterSubscriptions());
			broadcastRunner = new TwitterBroadcastRunner(
					event.getJDA(), initTwitterApi(), config);
			broadcastRunner.start();
			logger.info("TwitterBroadcastService ready!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.warn("Failed to read twitter service configuration file.");
			return;
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		MessageChannelUnion channel = event.getChannel();
		ArgParser parser = new ArgParser(event.getMessage().getContentDisplay());
		
		if (parser.getCommand().equals(">sudo")) {
			try {
				parser.parse();
			} catch (ArgumentParseException e) {
				e.printStackTrace();
				return;
			}
		}

		try {
			if (parser.getCommandSize() >= 1)
			if (parser.getCommand(1).equals("twitter")) {
				if (parser.getCommandSize() < 5) {
					throw new IllegalArgumentException("Command missing argument");
				}
				else if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
					channel.sendMessage("Sorry! You don't have the permission to do this!").queue();
					return;
				}
				else if (parser.getCommand(2).equals("blacklist")) {
					broadcastRunner.interrupt();
					try {
						Long.parseLong(parser.getCommand(4));	// try to parse parameter
						if (parser.getCommand(3).equals("add")) {
							broadcastRunner.sendMessage(new SyncMessage("add", parser.getCommand(4)));
							channel.sendMessage("Add " + parser.getCommand(4) + " to twitter broadcast blacklist.").queue();
						}
						else if (parser.getCommand(3).equals("remove")) {
							broadcastRunner.sendMessage(new SyncMessage("remove", parser.getCommand(4)));
							channel.sendMessage("Remove " + parser.getCommand(4) + " from twitter broadcast blacklist.").queue();
						}
						else {
							throw new IllegalArgumentException("Blacklist argument must be add or remove.");
						}
					} catch (InterruptedException e) {
						logger.warn("Interrupted occured when sending message to runner.");
						e.printStackTrace();
					} catch (NumberFormatException e) {
						channel.sendMessage("Error: Parameter format").queue();
					}
				}
				else {
					throw new IllegalArgumentException("Invalid argument for twitter broadcast service.");
				}
			}
		}
		catch (IllegalArgumentException e) {
			MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
					.addContent("Error: ")
					.addContent(e.getMessage())
					.addContent("\nUsage: ")
					.addContent(MarkdownUtil.codeblock("twitter [blacklist] [add | remove] args"));
			channel.sendMessage(messageBuilder.build()).queue();
		}
		
	}
	
	public void terminate() {
		logger.info("Terminating TwitterBroadcastService...");
		try {
			broadcastRunner.sendMessage(new SyncMessage("stop"));
			broadcastRunner.interrupt();
			broadcastRunner.join();
		} catch (InterruptedException e) {
			logger.warn("Interrupt occurred when stopping runner.");
			e.printStackTrace();
		}
	}
	
	private void isTwitterBroadcastConfigVaild(
			JDA jda,
			ArrayList<TwitterSubscription> subscriptions) {
		logger.info("TwitterBroadcastService configuration verification start ...");
		boolean isVaild = true;
		for (TwitterSubscription subscription: subscriptions) {
			for (long textChannelId: subscription.getTargetChannels()) {
				if (jda.getTextChannelById(textChannelId) == null) {
					logger.warn("Text channel with ID: " + textChannelId + " does not exist. >>> Continue verification.");
					isVaild = false;
				}
			}
		}
		
		if (!isVaild)
			logger.warn("TwitterBroadcastService configuraiton verification failed.");
		else
			logger.info("TwitterBroadcastService configuration ... OK");
	}
	
	/**
	 * reads twitter4j properties and init an instance
	 * @return
	 */
	private Twitter initTwitterApi() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("config/credentials/twitter4j.properties"));
		} catch (Exception e) {
			logger.error("Failed to read twitter4j token file");
			return null;
		}
		
		return Twitter.newBuilder()
				.oAuthConsumer(
						properties.getProperty("oauth.consumerKey"),
						properties.getProperty("oauth.consumerSecret"))
				.oAuthAccessToken(
						properties.getProperty("oauth.accessToken"),
						properties.getProperty("oauth.accessTokenSecret"))
				.build();
	}
	
	private TwitterBroadcastRunner broadcastRunner;
	private Logger logger;
}
