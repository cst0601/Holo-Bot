package com.alchemist;

import com.alchemist.service.Service;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * All slash commands should be registered here.
 */
public class BotCommands {

  /** Add things. */
  public static void addCommands(JDA jda) {
    Logger logger = LoggerFactory.getLogger(BotCommands.class);
    CommandListUpdateAction commands = jda.updateCommands();

    for (Service service : getRunningServices(jda)) {
      if (service.getSlashCommands() == null) {
        continue;
      }
      logger.info("Registering slash command from: " + service.getServiceName());
      commands.addCommands(service.getSlashCommands());
    }

    commands.queue();
  }

  @SuppressWarnings("unchecked")
  private static List<Service> getRunningServices(JDA jda) {
    return (List<Service>) (Object) jda.getRegisteredListeners();
  }
}
