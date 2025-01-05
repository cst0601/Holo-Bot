package com.alchemist.jsonresponse;

import org.json.JSONObject;

/**
 * Simple structure for response status code and json body.
 */
public class JsonResponse {
  /** Initialize generic JsonResponse from response body. */
  public JsonResponse(int statusCode, String body) {
    this.statusCode = statusCode;
    this.body = body;
    this.json = new JSONObject(body);
  }

  // bad json response
  public JsonResponse() {
    this.statusCode = -1;
  }

  public int getStatus() {
    return statusCode;
  }

  public String getBody() {
    return body;
  }

  public String getStreamUrl() {
    return getStream().getJSONObject("id").getString("videoId");
  }

  public JSONObject getStream() {
    return json.getJSONArray("items").getJSONObject(0);
  }

  private int statusCode;
  private String body;
  private JSONObject json;
}
