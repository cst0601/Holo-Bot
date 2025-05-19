package com.alchemist;

import com.alchemist.helper.TestDataReader;
import com.alchemist.jsonresponse.HoloDexLiveJsonResponse;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/** Test for HoloDexApi. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestHoloDexApi {
  private HoloDexApi api;
  private ArrayList<LiveStream> streams;

  void setupApiMockData(String fileName) throws Exception {
    String responseBody = TestDataReader.getReader().readTestData(fileName);
    Mockito.doReturn(new HoloDexLiveJsonResponse(200, responseBody))
        .when(api)
        .request(ArgumentMatchers.anyString());
  }

  @BeforeAll
  void setup() throws Exception {
    api = Mockito.spy(new HoloDexApi());
  }

  @Test
  void testGetStreamOfMember() throws Exception {
    setupApiMockData("HoloDexApiSingleMember.json");
    streams = api.getStreamOfMember("suisei", "upcoming");

    Assertions.assertEquals(2, streams.size());
  }

  @Test
  void testGetStreamOfMemberNameNotFound() throws Exception {
    setupApiMockData("HoloDexApi.json");
    try {
      streams = api.getStreamOfMember("random name", "upcoming");
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    
    Assertions.assertEquals(0, streams.size());
  }

  @Test
  void testGetLiveStreams() throws Exception {
    setupApiMockData("HoloDexApi.json");
    try {
      streams = api.getLiveStreams();
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }

    // current data that I grabbed does not have a member going "live"
    Assertions.assertEquals(0, streams.size());
  }

  @Test
  void testGetStreamMentioningMember() throws Exception {
    setupApiMockData("HoloDexMentionedChannel.json");
    try {
      streams = api.getStreamMentioningMember("raden", "upcoming");
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    System.out.println(streams.get(0).getTitle());
    Assertions.assertEquals(1, streams.size());

    LiveStream liveStream = streams.get(0);
    Assertions.assertEquals("赤井はあと", liveStream.getMemberName());
    Assertions.assertEquals("jelFWBjk4qs", liveStream.getVideoId());
    Assertions.assertEquals(
        "【#でんちゃま】語り合おう、もんじゃで。【ホロライブ/赤井はあと/儒烏風亭らでん #ReGLOSS】",
        liveStream.getTitle()
    );
  }

  @Test
  void testGetStreamMentioningMemberFail() throws Exception {
    setupApiMockData("HoloDexMentionedChannel.json");
    try {
      streams = api.getStreamMentioningMember("nodoka", "upcoming");
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }

    Assertions.assertEquals(0, streams.size());
  }
}
