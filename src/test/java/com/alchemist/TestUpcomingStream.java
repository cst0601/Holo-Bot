package com.alchemist;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.dv8tion.jda.api.utils.messages.MessageCreateData;


class TestUpcomingStream {
	
	private ZonedDateTime now;
	
	private UpcomingStream createTestStream(ZonedDateTime time) {
		LiveStream liveStream = null;
		try {
			liveStream = new LiveStream("さくらみこ", "9_oc4fi_VJQ",
					"【 マリオカート8DX 】耐久１位を取るまで終われまてん開幕🏆！！！！！！！【ホロライブ/さくらみこ】",
					DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(time), "Miko Ch. さくらみこ");
		} catch (ParseException e) {
			e.printStackTrace();
			fail("time parse failed when creating liveStream");
		}
		return new UpcomingStream(liveStream, new MockRole());
	}
	
	@BeforeEach
	private void init() {
		now = ZonedDateTime.now(ZoneId.of("UTC"));
	}

	@Test
	void testBroadcastToNotified() {
		UpcomingStream upcomingStream = createTestStream(now.plusMinutes(1));
		MessageCreateData message = upcomingStream.broadcast();
		
		assertEquals(String.format(
				"頻道有新動靜！快去看看！\n"
				+ "預定開始時間: <t:%s:F>, <t:%s:R>\n"
				+ "https://www.youtube.com/watch?v=9_oc4fi_VJQ",
				now.plusMinutes(1).toInstant().toEpochMilli() / 1000L,
				now.plusMinutes(1).toInstant().toEpochMilli() / 1000L
				),
				message.getContent());
	}
	
	@Test
	void testBroadcastToUpcoming() {
		UpcomingStream upcomingStream = createTestStream(now.plusMinutes(4));
		
		String broadcastContent = String.format(
				"頻道有新動靜！快去看看！\n"
				+ "預定開始時間: <t:%s:F>, <t:%s:R>\n"
				+ "https://www.youtube.com/watch?v=9_oc4fi_VJQ",
				now.plusMinutes(4).toInstant().toEpochMilli() / 1000L,
				now.plusMinutes(4).toInstant().toEpochMilli() / 1000L);
		
		assertEquals(broadcastContent,
				upcomingStream.broadcast().getContent());
		assertEquals("再過五分鐘配信開始！\nhttps://www.youtube.com/watch?v=9_oc4fi_VJQ",
				upcomingStream.broadcast().getContent());
		assertNull(upcomingStream.broadcast());
	}
	
	@Test
	void testBroadcastToStarted() {
		UpcomingStream upcomingStream = createTestStream(now.plusMinutes(-1));
		assertTrue(upcomingStream.hasStarted());
		assertNull(upcomingStream.broadcast());
	}
}
