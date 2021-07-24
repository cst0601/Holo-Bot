package com.alchemist;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

import net.dv8tion.jda.api.entities.Message;


class TestUpcomingStream {

	@Test
	void testBroadcastInit() {
		LiveStream liveStream = null;
		try {
			liveStream = new LiveStream("さくらみこ", "9_oc4fi_VJQ",
					"【 マリオカート8DX 】耐久１位を取るまで終われまてん開幕🏆！！！！！！！【ホロライブ/さくらみこ】",
					"2021-07-24T00:00:00.000Z", "Miko Ch. さくらみこ");
		} catch (ParseException e) {
			e.printStackTrace();
			fail("time parse failed when creating liveStream");
		}
		
		UpcomingStream upcomingStream = new UpcomingStream(liveStream, new MockRole());
		Message message = upcomingStream.broadcast();
		
		assertEquals("頻道有新動靜！快去看看！\nhttps://www.youtube.com/watch?v=9_oc4fi_VJQ",
				     message.getContentRaw());
	}
}
