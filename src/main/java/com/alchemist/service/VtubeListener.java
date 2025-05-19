package com.alchemist.service;

import com.alchemist.ArgParser;
import com.alchemist.HoloDexApi;
import com.alchemist.LiveStream;
import com.alchemist.data.HoloMemberData;
import com.alchemist.exceptions.ArgumentParseException;
import com.alchemist.holomodel.HoloMemberListModel;
import com.alchemist.holomodel.HoloScheduleModel;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message command >holo.
 */
public class VtubeListener extends ListenerAdapter implements Service {
  private HoloScheduleModel holoScheduleModel;
  private HoloDexApi holoDexApi;
  private Logger logger;
  private ArgParser parser = null;

  /**
   * Initialize Model and API.
   */
  public VtubeListener() {
    holoScheduleModel = new HoloScheduleModel();
    holoDexApi = new HoloDexApi();
    logger = LoggerFactory.getLogger(VtubeListener.class.getName());
  }

  public String contentFormat() {
    return "";
  }

  /**
   * Triggers when received message (MessageReceivedEvent).
   * Handles >holo command.
   */
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    Message message = event.getMessage();
    MessageChannelUnion channel = event.getChannel();

    String msg = message.getContentDisplay();

    parser = new ArgParser(msg);

    if (parser.getCommand().equals(">holo")) {

      // actual parse the command if sure this is a command
      try {
        parser.parse();
      } catch (ArgumentParseException e1) {
        channel.sendMessage("Command format error.\n" + e1.getMessage()).queue();
        return;
      }

      if (parser.getParamSize() < 2) {
        channel.sendMessage(getMemberNotFoundMessage()).queue();
        return;
      }

      try {
        if (parser.containsArgs("list")) {
          getHoloMemberList(channel, parser);
        } else if (parser.containsArgs("schedules")) {
          getSchedules(channel, parser);
        } else if (parser.containsArgs("live")) {
          getLive(channel);
        } else if (HoloMemberData.getInstance().getMemberByName(parser.getCommand(1)) != null) {
          // get stream
          // if arg member name not avaliable
          LiveStream liveStream = holoDexApi.getLiveStreamOfMember(parser.getCommand(1));

          if (liveStream == null) {
            channel.sendMessage("目前並沒有直播 :(").queue();
          } else {
            channel.sendMessage(
              "目前的直播：\n" + liveStream.getTitle() + "\n" + liveStream.toString()
              ).queue();
          }
        } else { // no member found
          channel.sendMessage(getErrorMessage()).queue();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  }

  // does not bother to test these methods :)
  private void getSchedules(MessageChannelUnion channel, ArgParser parser) {
    try {
      if (parser.containsArgs("jp") || parser.getCommandSize() == 2) {
        channel.sendMessageEmbeds(holoScheduleModel.getHoloSchedule()).queue();
      } else if (parser.containsArgs("en")) {
        channel.sendMessageEmbeds(holoScheduleModel.getHoloEnSchedule()).queue();
      } else if (parser.containsArgs("id")) {
        channel.sendMessageEmbeds(holoScheduleModel.getHoloIdSchedule()).queue();
      } else {
        channel.sendMessage("Error: Unknown group of Hololive").queue();
      }
    } catch (Exception e) { // schedule api exceptions
      channel.sendMessage("Looks like schedule api went on vacation.  :((\n"
          + "Contact admin to get help.").queue();
      logger.warn("Failed to use schedule api");
      e.printStackTrace();
    }
  }

  private void getHoloMemberList(MessageChannelUnion channel, ArgParser parser) {
    HoloMemberListModel holoMemberList = new HoloMemberListModel();

    if (parser.containsArgs("jp") || parser.getCommandSize() == 2) {
      channel.sendMessageEmbeds(holoMemberList.getHoloMemberList()).queue();
    } else if (parser.containsArgs("en")) {
      channel.sendMessageEmbeds(holoMemberList.getHoloEnMemberList()).queue();
    } else if (parser.containsArgs("id")) {
      channel.sendMessageEmbeds(holoMemberList.getHoloIdMemberList()).queue();
    } else {
      channel.sendMessage("Error: Unknown group of Hololive").queue();
    }
  }

  private void getLive(MessageChannelUnion channel) {
    EmbedBuilder builder = new EmbedBuilder()
        .setTitle(">holo live")
        .setColor(Color.red)
        .setDescription(":red_circle: Streams now")
        .setFooter("Holo Bot", "https://i.imgur.com/DOb1GZ1.png");

    try {
      for (LiveStream stream : holoDexApi.getLiveStreams()) {
        builder.addField(stream.getMemberName(), stream.toMarkdownLink(), false);
      }

      channel.sendMessageEmbeds(builder.build()).queue();

    } catch (Exception e) {
      logger.warn("Failed to get request from holoToolsApi.");
      e.printStackTrace();
    }
  }

  private MessageCreateData getErrorMessage() {
    return new MessageCreateBuilder()
        .addContent("Error: member not found.\n")
        .addContent("Use ")
        .addContent(MarkdownUtil.codeblock(">holo list"))
        .addContent(" to get a full list of available members.")
        .build();
  }

  private MessageCreateData getMemberNotFoundMessage() {
    MessageCreateBuilder builder = new MessageCreateBuilder()
        .addContent("Error: Usage: ")
        .addContent(MarkdownUtil.codeblock(">holo <member>"))
        .addContent(".\nUse ")
        .addContent(MarkdownUtil.codeblock(">holo list"))
        .addContent(" to get a full list of available members.\n")
        .addContent(MarkdownUtil.codeblock(">holo schedules"))
        .addContent(" to get schedules of today.");
    return builder.build();
  }

  @Override
  public String getServiceManualName() {
    return "holo";
  }

  @Override
  public String getServiceMan() {
    return "NAME\n"
        + "        holo - Hololive event tracker\n\n"
        + "SYNOPSIS\n"
        + "        holo <command> [args]\n\n"
        + "COMMANDS\n"
        + "        list: List all available hololive members.\n"
        + "        live: Get all streams that are currently live.\n"
        + "        <member_name>: Fetch streams currently going on.\n"
        + "        schedules: Get all schedules of today (JST).\n";
  }
}
