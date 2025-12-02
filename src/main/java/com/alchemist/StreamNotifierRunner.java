package com.alchemist;

import com.alchemist.config.Config;
import com.alchemist.notification.LiveStream;
import com.alchemist.notification.UpcomingStream;
import io.sentry.Sentry;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.OptionalInt;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workhorse of stream notification.
 */
public class StreamNotifierRunner extends Thread {

  /**
   * Thread that check stream and send out messasges.
   */
  public StreamNotifierRunner(JDA jda, BlockingQueue<String> messageBox) {
    Thread.currentThread().setName("StreamNotifierRunner");
    logger = LoggerFactory.getLogger(StreamNotifierRunner.class);
    config = Config.getConfig();
    this.jda = jda;

    serviceMessageBox = messageBox;
    api = new HoloDexApi();
    upcomingStreams = new LinkedList<UpcomingStream>();

    // TODO: add some more message in member target channel
    // memberTargetChannel = jda.getTextChannelById(config.membershipTargetChannelId);
    this.memberName = config.memberName;
  }

  /** Thread main function. */
  public void run() {
    updateUpcomingStreams();  // init upcoming stream list
    notifyUpcomingStreams(false);

    while (true) {
      List<String> message;
      try {
        if ((message = messageBox.poll()) != null) {
          if (message.get(0).equals("stop")) {
            logger.info("StreamNotifierRunner terminating...");
            break;
          } else if (message.get(0).equals("flush")) {
            upcomingStreams.clear();
            updateUpcomingStreams();
            notifyUpcomingStreams(false);
            serviceMessageBox.put("Flushed all cached streams.");
            logger.info("Flushed all cached streams.");
          } else if (message.get(0).equals("list")) {
            listAllUpcomingStreams(message.get(1));
            logger.info("listed all upcoming stream");
          }
        } else {
          updateUpcomingStreams();
          notifyUpcomingStreams(true);
          sleep(60000);  // 1 min
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    logger.info("StreamNotifierRunner exit run.");
  }

  public void sendMessage(List<String> message) throws InterruptedException {
    messageBox.put(message);
  }

  private OptionalInt getUpcomingStreamIndex(UpcomingStream newStream) {
    for (int i = 0; i < upcomingStreams.size(); ++i) {
      if (upcomingStreams.get(i).getStreamUrl().equals(newStream.getStreamUrl())) {
        return OptionalInt.of(i);
      }
    }
    return OptionalInt.empty();  
  }

  private void updateUpcomingStreams() {
    try {
      ArrayList<UpcomingStream> updateStream = new ArrayList<UpcomingStream>();
      ArrayList<LiveStream> liveStreams = api.getStreamOfMember(memberName, "upcoming");
      liveStreams.addAll(api.getStreamMentioningMember(memberName, "upcoming"));

      for (LiveStream stream : liveStreams) {
        UpcomingStream upcomingStream = new UpcomingStream(stream, jda);
        // api might give stream started but state = upcoming. e.g. the bot 
        // started after the streram started. We don't want it to be notified
        // in this case.
        if (!upcomingStream.hasStarted()) {           
          updateStream.add(upcomingStream);
        }
      }

      for (UpcomingStream stream : updateStream) {
        // if stream does not exist, add it to list
        // if does exist, check if scheduled start time needs update
        // TODO: if stream exist in cache but no longer in yt, delete it
        OptionalInt streamIndex = getUpcomingStreamIndex(stream);
        if (streamIndex.isPresent()) {
          ArrayList<MessageCreateBuilder> updateMessages = upcomingStreams
              .get(streamIndex.getAsInt())
              .checkStreamStartTime(stream);

          if (updateMessages != null) {
            sendMessageToChannels(updateMessages, upcomingStreams.get(streamIndex.getAsInt()));
            logger.info("Updated stream start time " + stream.toString());
          }
        } else {
          upcomingStreams.add(stream);
          if (!stream.hasStarted()) {
            logger.info("New upcoming stream " + stream.toString());
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Failed to update upoming streams. " + e.getMessage());
      Sentry.captureException(e);
    } 
  }

  private void notifyUpcomingStreams(boolean sendMessage) {
    ListIterator<UpcomingStream> iter = upcomingStreams.listIterator();
    while (iter.hasNext()) {
      UpcomingStream stream = iter.next();
      ArrayList<MessageCreateBuilder> messageBuilders = stream.broadcast();

      if (messageBuilders != null && sendMessage) {
        sendMessageToChannels(messageBuilders, stream);
        logger.info("Notified stream: " + stream.getStreamUrl());
      }
      if (stream.hasStarted()) {
        logger.info("Remove started stream: " + stream.getStreamUrl());
        iter.remove();
      }
    }
  }

  private void listAllUpcomingStreams(String channelId) throws InterruptedException {
    EmbedBuilder builder = new EmbedBuilder()
        .setTitle("Upcoming Streams")
        .setColor(Color.red)
        .setDescription("List all upcoming streams");
    String message = "# Upcoming streams\n";
    String formattedStreamList = "";
    for (UpcomingStream stream : upcomingStreams) {
      message += stream.toString() + "\n";
      formattedStreamList += stream.toMarkdownString();
    }
    builder.addField("List (start_time, url, state, isMentioned)", formattedStreamList, false);
    serviceMessageBox.put(message);
    logger.info(message);
    jda.getTextChannelById(channelId).sendMessageEmbeds(builder.build()).queue();
  }

  private void sendMessageToChannels(
      ArrayList<MessageCreateBuilder> messageBuilders,
      UpcomingStream stream
  ) {
    messageBuilders = stream.appendMemberOnlyMessage(messageBuilders);
    for (int i = 0; i < config.notifications.size(); i++) {
      MessageChannel channel = jda.getTextChannelById(config.notifications.get(i).targetChannelId);
      logger.info(
          "Send notification for stream: " + stream.getStreamUrl() 
          + " to channel: " + channel.getId()
      );

      channel.sendMessage(messageBuilders.get(i).build()).queue();
    }
  }

  private JDA jda;
  private Logger logger;
  private Config config;
  private BlockingQueue<List<String>> messageBox = new LinkedBlockingQueue<List<String>>();
  private BlockingQueue<String> serviceMessageBox;
  private HoloDexApi api;
  private String memberName;
  // Welp, upcoming stream usually does not have a lot, so...
  private List<UpcomingStream> upcomingStreams;
}