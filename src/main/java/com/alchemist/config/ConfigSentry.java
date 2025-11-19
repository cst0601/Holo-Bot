package com.alchemist.config;

import org.json.JSONObject;

/**
 * Read config for Sentry.
 */
public class ConfigSentry {
  /** Read config from json. */
  public ConfigSentry(JSONObject json) {
    dsn = json.getString("dsn");
    environment = json.getString("environment");
    sendDefaultPii = json.getBoolean("send_default_pii");
    debug = json.getBoolean("debug");
  }

  public final String dsn;
  public final String environment;
  public final boolean sendDefaultPii;
  public final boolean debug;
}
