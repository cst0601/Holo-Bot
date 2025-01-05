package com.alchemist;

import com.alchemist.jsonresponse.HoloApiJsonResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.util.ArrayList;

/**
 * HoloSchedule API interface.
 */
public class HoloApi extends Api {
  public HoloApi() {
    super();
  }

  /**
   * Sends request to API.
   */
  public ArrayList<Schedule> request(String group)
      throws IOException, InterruptedException, ConnectException {
    request = HttpRequest.newBuilder()
        .uri(URI.create(String.format(defaultUrl, group)))
        .build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    HoloApiJsonResponse jsonResponse = new HoloApiJsonResponse(
        response.statusCode(), response.body());
    updateTime = jsonResponse.getUpdateTime();

    // if no schedules yet, no data could be extract from request
    if (jsonResponse.getSchedules().size() > 0) {
      date = jsonResponse.getSchedules().get(0).getDate();
    }

    return jsonResponse.getSchedules();
  }

  /**
   * Get the updated time of the API.
   */
  public Instant getUpdateTime() {
    return Instant.parse(updateTime);
  }

  /**
   * Get the date of the requested schedules.
   */
  public String getDateOfSchedule() {
    return date;
  }

  //2023-03-20 00:06:09
  private final String defaultUrl = "http://127.0.0.1:5000/%s/schedules/today";
  private HttpRequest request;
  private String updateTime = "";
  private String date = "";
}
