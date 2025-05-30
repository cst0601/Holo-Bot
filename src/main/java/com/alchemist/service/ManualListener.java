package com.alchemist.service;

import com.alchemist.ArgParser;
import com.alchemist.exceptions.ArgumentParseException;
import java.awt.Color;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

/**
 * Service for showing manuals.
 */
public class ManualListener extends ListenerAdapter implements Service {

  @SuppressWarnings("unchecked")  // convert List<Object> to List<Service>
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    JDA jda = event.getJDA();
    // triggers unchecked warning
    List<Service> listeners = (List<Service>) (Object) jda.getRegisteredListeners();

    Message message = event.getMessage();
    MessageChannelUnion channel = event.getChannel();

    String msg = message.getContentDisplay(); // get readable version of the message

    if (!event.getAuthor().isBot()) {

      ArgParser parser = new ArgParser(msg);

      if (parser.getCommand().equals(">man")) {

        try {
          parser.parse();
        } catch (ArgumentParseException e1) {
          channel.sendMessage("Command format error.\n" + e1.getMessage()).queue();
          return;
        }

        if (parser.getCommandSize() < 2) {
          channel.sendMessageEmbeds(new EmbedBuilder()
            .setTitle("Manual (help)")
            .setColor(Color.red)
            .setDescription("What manual page do you want?")
            .addField("Usage", ">man <man_page>", false)
            .addField("List of services", getManualList(listeners), false)
            .build()).queue();
          return;
        }

        for (int i = 0; i < listeners.size(); ++i) {
          if (parser.getCommand(1).equals(((Service) listeners.get(i)).getServiceManualName())) {
            channel.sendMessage(new MessageCreateBuilder()
              .addContent(MarkdownUtil.codeblock(listeners.get(i).getServiceMan(), "md"))
              .build()).queue();
            return; // man page found
          }
        }
        channel.sendMessage("Not a vaild manual page.").queue(); // not a vaild manual page
      }
    }
  }

  @Override
  public String getServiceManualName() {
    return "man";
  }

  @Override
  public String getServiceMan() {
    return "NAME\n"
        + "        man - Manual, read the doc! :)\n\n"
        + "SYNOPSIS\n"
        + "        man: Get some informations about manual\n"
        + "        man [manual_page]: Get the manual page of a specific service";
  }

  /**
   * Get all the names of services available and form them in to a nice looking
   * format.
   *
   * @param services List of services that are egistered to JDA.
   * @return String contains list of service names
   */
  public String getManualList(List<Service> services) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < services.size(); ++i) {
      if (services.get(i).getServiceManualName() != null) { // preclude non backend listeners
        buffer.append(" - ");
        buffer.append(((Service) services.get(i)).getServiceManualName());
        buffer.append("\n");
      }
    }
    return buffer.toString();
  }
}
