package com.alchemist;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;

/**
 * API base class, initializes HttpClient.
 */
public class Api {
  protected Api() {
    client = HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .followRedirects(Redirect.NORMAL)
                .build();
  }

  protected HttpClient client;
  protected HttpRequest request;
}
