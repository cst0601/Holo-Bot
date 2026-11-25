package com.alchemist.notifier;

import com.alchemist.ArgParser;
import com.alchemist.exceptions.ArgumentParseException;
import com.alchemist.service.Service;
import io.sentry.Sentry;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for sending notifications for live stream.
 */
public class StreamNotifierService extends ListenerAdapter implements Service {

  private Logger logger = LoggerFactory.getLogger(StreamNotifierService.class);
  private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
  private ScheduledFuture<?> handle = null;
  private StreamNotifierRunner streamNotifierRunner = null;
  private ArgParser parser = null;
  private BlockingQueue<String> serviceMessageBox = new LinkedBlockingQueue<String>();

  @Override
  public void onReady(ReadyEvent event) {
    try {
      streamNotifierRunner = new StreamNotifierRunner(event.getJDA(), serviceMessageBox);
      handle = executor.scheduleWithFixedDelay(streamNotifierRunner, 0, 1, TimeUnit.MINUTES);
      logger.info("StreamNotifierService ready!");
    } catch (Exception e) {
      e.printStackTrace();
      Sentry.captureException(e);
      logger.warn("Error occured when reading config file, skip runner build.");
    }
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    MessageChannelUnion channel = event.getChannel();
    String msg = event.getMessage().getContentDisplay();  // get readable version of the message

    Member member = event.getMember();
    parser = new ArgParser(msg);

    if (parser.getCommand().equals(">sudo") && member.hasPermission(Permission.ADMINISTRATOR)) {
      try {
        parser.parse();
      } catch (ArgumentParseException e1) {
        e1.printStackTrace();
        Sentry.captureException(e1);
      }

      if (parser.getCommandSize() > 1) {
        if (parser.getCommand().equals(">sudo")
            && parser.getCommand(1).equals("stream_flush")) {

          try {
            executor.schedule(() -> {
              streamNotifierRunner.flushStreams();
            }, 0, TimeUnit.SECONDS);
            channel.sendMessage("upcoming stream cache cleared.").queue();
          } catch (RejectedExecutionException | NullPointerException e) {
            logger.warn("Failed to execute flush command.");
            e.printStackTrace();
          }
        } else if (parser.getCommand().equals(">sudo")
                   && parser.getCommand(1).equals("stream_list")) {
          try {
            executor.schedule(() -> {
              try {
                streamNotifierRunner.listAllUpcomingStreams(channel.getId());
              } catch (InterruptedException e) {
                logger.warn("Failed to execute flush command.");
                e.printStackTrace();
              }
            }, 0, TimeUnit.SECONDS);
          } catch (RejectedExecutionException | NullPointerException e) {
            logger.warn("Failed to execute steam_list command.");
            e.printStackTrace();
          }
        }

      }
    }
  }

  /**
   * Send interrupt message to threads for termination.
   */
  public void terminate() {
    logger.info("Terminating stream notifier...");
    try {
      handle.get();
    } catch (CancellationException e) {
      logger.info("Terminated steam notifier task.");
    } catch (InterruptedException | ExecutionException e) {
      logger.error("Scheduled task died: " + e.getMessage());
    } finally {
      executor.shutdown();
    }
  }
}
