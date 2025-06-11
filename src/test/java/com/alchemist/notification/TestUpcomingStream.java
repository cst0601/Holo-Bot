package com.alchemist.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestUpcomingStream {

  private ZonedDateTime now;

  private UpcomingStream createTestStream(ZonedDateTime time, boolean isMentionedStream) {
    LiveStream liveStream = null;
    try {
      liveStream = new LiveStream("さくらみこ", "9_oc4fi_VJQ",
          "【 マリオカート8DX 】耐久１位を取るまで終われまてん開幕🏆！！！！！！！【ホロライブ/さくらみこ】",
          time, "Miko Ch. さくらみこ", isMentionedStream);
    } catch (ParseException e) {
      e.printStackTrace();
      fail("time parse failed when creating liveStream");
    }
    return new UpcomingStream(liveStream, null);
  }

  @BeforeEach
  void init() {
    now = ZonedDateTime.now(ZoneId.of("UTC"));
  }

  @Test
  void testBroadcastToNotified() {
    UpcomingStream upcomingStream = createTestStream(now.plusMinutes(1), false);
    MessageCreateData message = upcomingStream.broadcast().get(0).build();

    assertEquals(String.format(
        "頻道有新動靜！快去看看！\n"
            + "預定開始時間: <t:%s:F>, <t:%s:R>\n"
            + "https://www.youtube.com/watch?v=9_oc4fi_VJQ",
        now.plusMinutes(1).toInstant().toEpochMilli() / 1000L,
        now.plusMinutes(1).toInstant().toEpochMilli() / 1000L),
        message.getContent());
  }

  @Test
  void testMentionedBroadcastToNotified() {
    UpcomingStream upcomingStream = createTestStream(now.plusMinutes(1), true);
    MessageCreateData message = upcomingStream.broadcast().get(0).build();

    assertEquals(String.format(
        "在其他頻道被提及了！快去看看！\n"
            + "預定開始時間: <t:%s:F>, <t:%s:R>\n"
            + "https://www.youtube.com/watch?v=9_oc4fi_VJQ",
        now.plusMinutes(1).toInstant().toEpochMilli() / 1000L,
        now.plusMinutes(1).toInstant().toEpochMilli() / 1000L),
        message.getContent());
  }

  @Test
  void testBroadcastToUpcoming() {
    UpcomingStream upcomingStream = createTestStream(now.plusMinutes(4), false);

    String broadcastContent = String.format(
        "頻道有新動靜！快去看看！\n"
            + "預定開始時間: <t:%s:F>, <t:%s:R>\n"
            + "https://www.youtube.com/watch?v=9_oc4fi_VJQ",
        now.plusMinutes(4).toInstant().toEpochMilli() / 1000L,
        now.plusMinutes(4).toInstant().toEpochMilli() / 1000L);

    assertEquals(broadcastContent,
        upcomingStream.broadcast().get(0).build().getContent());
    assertEquals("再過五分鐘配信開始！\nhttps://www.youtube.com/watch?v=9_oc4fi_VJQ",
        upcomingStream.broadcast().get(0).build().getContent());
    assertNull(upcomingStream.broadcast());
  }

  @Test
  void testMentionedBroadcastToUpcoming() {
    UpcomingStream upcomingStream = createTestStream(now.plusMinutes(4), true);

    upcomingStream.broadcast();
    assertEquals("再過五分鐘配信開始！\nhttps://www.youtube.com/watch?v=9_oc4fi_VJQ",
        upcomingStream.broadcast().get(0).build().getContent());
    assertNull(upcomingStream.broadcast());
  }

  @Test
  void testBroadcastToStarted() {
    UpcomingStream upcomingStream = createTestStream(now.plusMinutes(-1), false);
    assertTrue(upcomingStream.hasStarted());
    assertNull(upcomingStream.broadcast());
  }

  @Test
  void testMentionedBroadcastToStarted() {
    UpcomingStream upcomingStream = createTestStream(now.plusMinutes(-1), true);
    assertTrue(upcomingStream.hasStarted());
    assertNull(upcomingStream.broadcast());
  }

  @Test
  void testCheckStreamStartTime() {
    UpcomingStream upcomingStream = createTestStream(now.plusMinutes(1), false);

    ArrayList<MessageCreateBuilder> emptyMessageBuilders = upcomingStream.checkStreamStartTime(
        createTestStream(now.plusMinutes(1), false));
    assertNull(emptyMessageBuilders);

    ArrayList<MessageCreateBuilder> messageBuilders = upcomingStream.checkStreamStartTime(
        createTestStream(now.plusHours(1), false));
    assertNotNull(messageBuilders);
  }
}
