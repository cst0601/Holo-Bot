package com.alchemist;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import net.dv8tion.jda.api.entities.Message;


class TestUpcomingStream {
	
	private String getTime(int bias) {
		ZonedDateTime time = ZonedDateTime.now(ZoneId.of("UTC"));
		return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(
				time.plusMinutes(bias));
	}
	
	private UpcomingStream createTestStream(int bias) {
		LiveStream liveStream = null;
		try {
			liveStream = new LiveStream("さくらみこ", "9_oc4fi_VJQ",
					"【 マリオカート8DX 】耐久１位を取るまで終われまてん開幕🏆！！！！！！！【ホロライブ/さくらみこ】",
					getTime(4), "Miko Ch. さくらみこ");
		} catch (ParseException e) {
			e.printStackTrace();
			fail("time parse failed when creating liveStream");
		}
		return new UpcomingStream(liveStream, new MockRole());
	}

	@Test
	void testBroadcastToNotified() {
		UpcomingStream upcomingStream = createTestStream(0);
		Message message = upcomingStream.broadcast();
		
		assertEquals("頻道有新動靜！快去看看！\nhttps://www.youtube.com/watch?v=9_oc4fi_VJQ",
				     message.getContentRaw());
	}
	
	@Test
	void testBroadcastToUpcoming() {
		UpcomingStream upcomingStream = createTestStream(4);
		
		assertEquals("頻道有新動靜！快去看看！\nhttps://www.youtube.com/watch?v=9_oc4fi_VJQ",
				upcomingStream.broadcast().getContentRaw());
		assertEquals("再過五分鐘配信開始！\nhttps://www.youtube.com/watch?v=9_oc4fi_VJQ",
				upcomingStream.broadcast().getContentRaw());
		assertNull(upcomingStream.broadcast());
	}
	
	@Test
	void testBroadcastToStarted() {
		UpcomingStream upcomingStream = createTestStream(-1);
		
		assertEquals("頻道有新動靜！快去看看！\nhttps://www.youtube.com/watch?v=9_oc4fi_VJQ",
				upcomingStream.broadcast().getContentRaw());
		assertEquals("再過五分鐘配信開始！\nhttps://www.youtube.com/watch?v=9_oc4fi_VJQ",
				upcomingStream.broadcast().getContentRaw());
		assertEquals("にゃっはろ～！配信開始了！\nhttps://www.youtube.com/watch?v=9_oc4fi_VJQ",
				upcomingStream.broadcast().getContentRaw());
		assertNull(upcomingStream.broadcast());
	}
}
