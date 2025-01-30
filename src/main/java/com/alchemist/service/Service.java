package com.alchemist.service;

import java.util.List;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * Interface for Services.
 */
public abstract interface Service {

  /**
   * Get the name of service for manual, usually the command prefix, e.g. "holo"
   */
  public default String getServiceManualName() {
    return null;
  }

  /**
   * Get the service's manual.
   */
  public default String getServiceMan() {
    return null;
  }

  /**
   * Override this method if service termination needs to be notified.
   */
  public default void terminate() {}

  /**
   * Get the service name (class name) for su.
   */
  public default String getServiceName() {
    return getClass().getName();
  }

  /**
   * Get List of slash commands.
   */
  public default List<CommandData> getSlashCommands() { 
    return null; 
  }
}
