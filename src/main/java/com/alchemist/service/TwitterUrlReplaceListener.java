package com.alchemist.service;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alchemist.VxTwitterApi;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class TwitterUrlReplaceListener extends ListenerAdapter implements Service {

	private VxTwitterApi api = new VxTwitterApi();
	
	public final static String URL_REGEX = "http(?:s)?:\\/\\/(?:www.)?(twitter|x)\\.com\\/([a-zA-Z0-9_]+)(\\/[a-zA-Z0-9]+)(\\/[a-zA-Z0-9]+)";
	public final static Pattern PATTERN = Pattern.compile(URL_REGEX);
	
	public TwitterUrlReplaceListener() {}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		
		if (message.getAuthor().isBot()) return;
		
		String msg = message.getContentDisplay();
		Matcher matcher = PATTERN.matcher(msg);
		ArrayList<MessageEmbed> tweets = new ArrayList<MessageEmbed>();

		try {
			while (matcher.find()) {
				String twitterUrl = msg.substring(matcher.start(), matcher.end());
				twitterUrl = twitterUrl.replace("twitter.com", "api.vxtwitter.com");
				twitterUrl = twitterUrl.replace("x.com", "api.vxtwitter.com");
				tweets.add(api.getTweet(twitterUrl).toMessageEmbed());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (!tweets.isEmpty()) {
			MessageCreateBuilder builder = new MessageCreateBuilder()
				.addEmbeds(tweets);
			
			message
				.reply(builder.build())
				.addActionRow(
						// component id format: delete/<member_id>/<original_message_id>
						Button.danger(
								"delete/" + event.getMember().getId() + "/" + message.getId() ,
								"delete"))
				.queue();
		}
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		String[] parts = event.getComponentId().split("/");
		if (parts.length != 3) return;
		
		if (parts[0].equals("delete")) {
			if (parts[1].equals(event.getUser().getId())) {
				event.getMessage().delete().queue();
			}
			else {
				event.reply("Does not have permission to do this.").queue();
			}
		}
	}
	
}
