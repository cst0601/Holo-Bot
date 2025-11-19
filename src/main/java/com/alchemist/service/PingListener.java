package com.alchemist.service;

import com.alchemist.ArgParser;
import com.alchemist.config.Config;
import com.alchemist.exceptions.ArgumentParseException;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for some basic commands: ping, sudo exit, about.
 */
public class PingListener extends ListenerAdapter implements Service {

  private ArgParser parser = null;

  public PingListener() {
    logger = LoggerFactory.getLogger(PingListener.class);
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    Message message = event.getMessage();
    MessageChannelUnion channel = event.getChannel();

    String msg = message.getContentDisplay(); // get readable version of the message

    Member member = event.getMember();
    parser = new ArgParser(msg);

    // only parse if command comes with >ping or >sudo
    // BTW this code is fugly :((
    if (parser.getCommand().equals(">ping") || parser.getCommand().equals(">sudo")) {
      try {
        parser.parse();
      } catch (ArgumentParseException e) {
        e.printStackTrace();
        return;
      }
    }

    if (parser.getCommand().equals(">ping")) {
      ping(channel);
    } else if (parser.getCommandSize() > 1) {
      if (parser.getCommand().equals(">sudo") && parser.getCommand(1).equals("exit")) {
        if (!Config.getConfig().isAdmin(member.getIdLong())) {
          channel.sendMessage("Sorry! You don't have the permission to do this!").queue();
        } else {
          channel.sendMessage("Exiting...").queue();
          logger.info("Received exit command, terminating...");

          for (Object listener : event.getJDA().getRegisteredListeners()) {
            ((Service) listener).terminate();
          }

          System.exit(0);
        }
      }
    }
  }

  @SuppressWarnings("all")
  private void ping(MessageChannelUnion channel) {
    if (parser.getCommandSize() > 1) {
      if (parser.getCommand(1).equals("sl")) {
        channel.sendMessage(new MessageCreateBuilder()
          .addContent(MarkdownUtil.bold("YOU HAVE PINGED A STEAM LOCOMOTIVE !!!"))
          .addContent(MarkdownUtil.codeblock("The steam locomotive is now under maintance...:("))
          .build()).queue();
      } else if (parser.getCommand(1).equals("ls")) {
        channel.sendMessage(new MessageCreateBuilder()
          .addContent(MarkdownUtil.bold("P\nO\nN\nG"))
          .build()).queue();
      }
    } else {
      channel.sendMessage("pong!").queue();
    }

  }

  @Override
  public String getServiceManualName() {
    return "ping";
  }

  @Override
  public String getServiceMan() {
    return
      "NAME\n"
      + "      ping - pong!\n\n"
      + "SYNOPSIS\n"
      + "      ping [options]\n\n"
      + "OPTIONS\n"
      + "      ls: A listed version of ping\n"
      + "      sl: Steam Locomotive";
  }

  private Logger logger;
}
