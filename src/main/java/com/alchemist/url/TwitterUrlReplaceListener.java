package com.alchemist.url;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

import com.alchemist.service.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for converting x.com or twitter.com urls to vxtwitter urls.
 */
public class TwitterUrlReplaceListener extends ListenerAdapter implements Service {
  private Logger logger;

  private VxTwitterApi api = new VxTwitterApi();

  public static final String URL_REGEX = "http(?:s)?:\\/\\/(?:www.)?(twitter|x)"
      + "\\.com\\/([a-zA-Z0-9_]+)(\\/[a-zA-Z0-9]+)(\\/[a-zA-Z0-9]+)";
  public static final Pattern PATTERN = Pattern.compile(URL_REGEX);

  public TwitterUrlReplaceListener() {
    logger = LoggerFactory.getLogger(TwitterUrlReplaceListener.class);
  }

  /** Create global slash commands. */
  public static ArrayList<CommandData> getSlashCommands() {
    return new ArrayList<>(Arrays.asList(
        Commands.slash("embed", "Post a embed message with url.")
          .addOption(STRING, "url", "The url for embed message.", true)
    ));
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    Message message = event.getMessage();

    if (message.getAuthor().isBot()) {
      return;
    }

    String msg = message.getContentDisplay();
    ArrayList<VxTweet> tweets = getVxTweetsFromUrl(msg);

    if (!tweets.isEmpty()) {
      // remove original message embed
      try {
        message.suppressEmbeds(true).queue();
      } catch (Exception e) {
        logger.error("Cannot remove message embed. " + e.getMessage());
      }

      MessageCreateBuilder builder = new MessageCreateBuilder();
      for (VxTweet tweet : tweets) {
        builder.addContent(tweet.getTweetLinks());
      }

      message
          .reply(builder.build())
          .mentionRepliedUser(false)
          .addComponents(
              ActionRow.of(
                // component id format: delete/<member_id>/<original_message_id>
                Button.secondary(
                    "delete/" + event.getMember().getId() + "/" + message.getId(),
                    "Delete")
              )
          )
          .queue();
    }
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (!event.getName().equals("embed")) {
      return;
    }

    String url = event.getOption("url").getAsString();
    ArrayList<VxTweet> tweets = getVxTweetsFromUrl(url);

    if (tweets.isEmpty()) {
      event
          .reply(
            "Not a valid url. Only Twitter/X urls are supported for now."
          )
          .setEphemeral(true)
          .queue();
    } else {
      MessageCreateBuilder builder = new MessageCreateBuilder();
      for (VxTweet tweet : tweets) {
        builder.addContent((tweet.getTweetLinks()));
      }

      // There's no shared interface for reply method between slash command
      // events and Message as of now I think.
      event 
          .reply(builder.build())
          .mentionRepliedUser(false)
          .addComponents(
            ActionRow.of(
              // component id format: delete/<member_id>
              Button.secondary("delete/" + event.getMember().getId(), "Delete")
            )
          )
          .queue();
    }
  }

  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    String[] parts = event.getComponentId().split("/");
    if (parts.length < 2) {
      return;
    }

    if (parts[0].equals("delete")) {
      if (parts[1].equals(event.getUser().getId())) {
        // acknowledge the event and then delete the original message
        event.deferEdit().queue();
        event.getMessage().delete().queue();
      } else {
        event
            .reply("沒辦法刪除不是你貼的連結にぇ。")
            .setEphemeral(true)
            .queue();
      }
    }
  }

  private ArrayList<VxTweet> getVxTweetsFromUrl(String url) {
    ArrayList<VxTweet> tweets = new ArrayList<VxTweet>();
    Matcher matcher = PATTERN.matcher(url);

    try {
      while (matcher.find()) {
        String twitterUrl = url.substring(matcher.start(), matcher.end());
        twitterUrl = twitterUrl.replace("twitter.com", "api.vxtwitter.com");
        twitterUrl = twitterUrl.replace("x.com", "api.vxtwitter.com");
        tweets.add(api.getTweet(twitterUrl));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return tweets;
  }
}
