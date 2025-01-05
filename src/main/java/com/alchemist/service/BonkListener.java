package com.alchemist.service;

import com.alchemist.ArgParser;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * >bonk command.
 */
public class BonkListener extends ListenerAdapter implements Service {

  private Logger logger = LoggerFactory.getLogger(BonkListener.class);
  private final String [] bonkEmojiId = {
      "756142081864630343",
      "819050266078740480",
      "781204705765752882",
      "781204705572290621",
      "817816581416681503"
  }; // TODO: remove this literal constant

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    Message message = event.getMessage();
    MessageChannelUnion channel = event.getChannel();

    String msg = message.getContentDisplay();

    ArgParser parser = new ArgParser(msg);

    if (parser.getCommand().equals(">bonk")) {

      try {
        List<Emoji> emojis = getEmotes(message.getGuild());
        List<Member> mentionedMember = new LinkedList<>(message.getMentions().getMembers());

        getTargetMessage(channel, message.getId(), mentionedMember, emojis);

      } catch (NoSuchElementException e) {
        logger.warn(String.format(
            "Requested from guild: %s, does not contain designated emote.",
            message.getGuild().getId()));
        channel.sendMessage(
          "Error: This sever does not contain the designated bonk emote.").queue();
      }
    }

  }

  private List<Emoji> getEmotes(Guild guild) throws NoSuchElementException {
    List<Emoji> emojis = new ArrayList<Emoji>();
    for (String id : bonkEmojiId) {
      Emoji emoji = guild.getEmojiById(id);
      if (emoji == null) {
        throw new NoSuchElementException("");
      }
      emojis.add(emoji);
    }
    return emojis;
  }

  private void getTargetMessage(
      MessageChannelUnion channel,
      String messageId,
      List<Member> targetMember,
      List<Emoji> emojis) {
    // This is a rest action that waits for completion, might make this method
    // to thread
    MessageHistory history = MessageHistory.getHistoryBefore(channel, messageId)
        .limit(10).complete();
    for (Message message : history.getRetrievedHistory()) {
      try {
        int index = containsMember(targetMember, message.getAuthor());
        targetMember.remove(index);

        for (Emoji emoji : emojis) {
          message.addReaction(emoji).queue();
        }
      } catch (NoSuchElementException e) {
        // pass
      }
    }
  }

  /**
   * Check if there are member with same ID.
   */
  private int containsMember(List<Member> list, User target) throws NoSuchElementException {
    for (int i = 0; i < list.size(); ++i) {
      if (list.get(i).getId().equals(target.getId())) {
        return i;
      }
    }
    throw new NoSuchElementException("Member with ID " + target.getId() + " does not exist.");
  }

  @Override
  public String getServiceManualName() {
    return "bonk";
  }

  @Override
  public String getServiceMan() {
    return "NAME\n"
        + "        bonk - bonk a member!\n\n"
        + "SYNOPSIS\n"
        + "        bonk @member: Bonk a member, member argument should be in mention format.";
  }

}
