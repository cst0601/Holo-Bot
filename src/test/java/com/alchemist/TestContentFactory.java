package com.alchemist;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestContentFactory {

	@Test
	void testCreateLiveSteam() {
		LiveStream content = new ContentFactory().createLiveStream(json);
		assertEquals("tB76TKmsgbA", content.getVideoId());
		assertEquals("【祝杯枠】ARK最終決戦🔥みこ視点動画をみながら祝杯だにぇ！"
				   + "🐱🍶【ホロライブ/さくらみこ】", content.getTitle());
		assertEquals("Miko Ch. さくらみこ", content.getChannelName());
	}

	private String json = "{\n" + 
			"  \"kind\": \"youtube#searchListResponse\",\n" + 
			"  \"etag\": \"LSwY1QFASzan88VzH-J7hWk6_Hk\",\n" + 
			"  \"regionCode\": \"TW\",\n" + 
			"  \"pageInfo\": {\n" + 
			"    \"totalResults\": 1,\n" + 
			"    \"resultsPerPage\": 5\n" + 
			"  },\n" + 
			"  \"items\": [\n" + 
			"    {\n" + 
			"      \"kind\": \"youtube#searchResult\",\n" + 
			"      \"etag\": \"-X5GNVRp2bWpReIAr30_chCJLGQ\",\n" + 
			"      \"id\": {\n" + 
			"        \"kind\": \"youtube#video\",\n" + 
			"        \"videoId\": \"tB76TKmsgbA\"\n" + 
			"      },\n" + 
			"      \"snippet\": {\n" + 
			"        \"publishedAt\": \"2020-05-30T15:06:29Z\",\n" + 
			"        \"channelId\": \"UC-hM6YJuNYVAmUWxeIr9FeA\",\n" + 
			"        \"title\": \"【祝杯枠】ARK最終決戦🔥みこ視点動画をみながら祝杯だにぇ！🐱🍶【ホロライブ/さくらみこ】\",\n" + 
			"        \"description\": \"みこなま をつけて感想をTweetしてくれるとよろこびます！     #みこなま のお約束 ・待機コメントで会話をしない ・伝書鳩行為は他...\",\n" + 
			"        \"thumbnails\": {\n" + 
			"          \"default\": {\n" + 
			"            \"url\": \"https://i.ytimg.com/vi/tB76TKmsgbA/default_live.jpg\",\n" + 
			"            \"width\": 120,\n" + 
			"            \"height\": 90\n" + 
			"          },\n" + 
			"          \"medium\": {\n" + 
			"            \"url\": \"https://i.ytimg.com/vi/tB76TKmsgbA/mqdefault_live.jpg\",\n" + 
			"            \"width\": 320,\n" + 
			"            \"height\": 180\n" + 
			"          },\n" + 
			"          \"high\": {\n" + 
			"            \"url\": \"https://i.ytimg.com/vi/tB76TKmsgbA/hqdefault_live.jpg\",\n" + 
			"            \"width\": 480,\n" + 
			"            \"height\": 360\n" + 
			"          }\n" + 
			"        },\n" + 
			"        \"channelTitle\": \"Miko Ch. さくらみこ\",\n" + 
			"        \"liveBroadcastContent\": \"live\",\n" + 
			"        \"publishTime\": \"2020-05-30T15:06:29Z\"\n" + 
			"      }\n" + 
			"    }\n" + 
			"  ]\n" + 
			"}\n";
}
