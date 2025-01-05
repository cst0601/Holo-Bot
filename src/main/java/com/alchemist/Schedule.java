package com.alchemist;

/**
 * Live stream schedule data class.
 */
public class Schedule {
  /** constructor. */
  public Schedule(String member, String url, String date, String time) {
    this.member = member;
    this.url = url;
    this.date = date;
    this.time = time;
  }

  public String toString() {
    return time + " " + member + " " + url;
  }

  public String toMarkdownLink() {
    return time + " [" + member + "](" + url + ")";
  }

  public String getUrl() {
    return url;
  }

  public String getDate() {
    return date;
  }

  private String member;
  private String url;
  private String date;
  private String time;
}
