package com.alchemist.service;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class TwitterUrlReplaceListener extends ListenerAdapter implements Service {

	public final static String TARGET_URL = "//twitter.com";
	public final static String URL_REGEX = "http(?:s)?:\\/\\/(?:www.)?(twitter|x)\\.com\\/([a-zA-Z0-9_]+)(\\/[a-zA-Z0-9]+)(\\/[a-zA-Z0-9]+)";
	public final static Pattern PATTERN = Pattern.compile(URL_REGEX);
	
	public TwitterUrlReplaceListener() {}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		MessageChannelUnion channel = event.getChannel();
		
		if (message.getAuthor().isBot()) return;
		
		String msg = message.getContentDisplay();
		Matcher matcher = PATTERN.matcher(msg);
		ArrayList<String> twitterUrls = new ArrayList<String>();

		while (matcher.find()) {
			String twitterUrl = msg.substring(matcher.start(), matcher.end());
			twitterUrl = twitterUrl.replace("twitter.com", "vxtwitter.com");
			twitterUrl = twitterUrl.replace("x.com", "vxtwitter.com");	// welp...
			twitterUrls.add(twitterUrl);
		}
		
		if (!twitterUrls.isEmpty()) {
			MessageCreateBuilder builder = new MessageCreateBuilder()
					.addContent(message.getAuthor().getAsMention() + " sends some twitter url:\n");
			for (String url: twitterUrls) {
				builder.addContent(url + "\n");
			}
			
			channel.sendMessage(builder.build()).queue();
		}
	}
	
}
