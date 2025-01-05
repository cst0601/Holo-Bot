package com.alchemist;

/**
 * SyncMessage - Message for message passing thread synchronization.
 */
public class SyncMessage {
  public SyncMessage(String messageHead) {
    head = messageHead;
    body = "";
  }

  public SyncMessage(String messageHead, String messageBody) {
    head = messageHead;
    body = messageBody;
  }

  public String getMessageHead() {
    return head;
  }

  public String getMessageBody() {
    return body;
  }

  private final String head;
  private final String body;
}
