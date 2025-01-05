package com.alchemist;

import com.alchemist.exceptions.ArgumentParseException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Parse argument and get values.
 * e.g. >holo schedules -mode=page -page=1
 */
public class ArgParser {
  private String[] commands;
  private String mainCommand = null;
  private ArrayList<String> args = new ArrayList<String>();
  private Dictionary<String, String> param = new Hashtable<String, String>();

  /**
   * Construct ArgParser from string arguments.
   */
  public ArgParser(String arguments) {
    arguments = arguments.trim();
    commands = arguments.split("\\s+");

    mainCommand = commands[0];
  }

  /**
   * Parse string to commands.
   */
  public void parse() throws ArgumentParseException {
    for (String command : commands) {

      // -example_param=100
      if (command.charAt(0) == '-' && command.contains("=")) {

        String[] keyAndValue = command.split("=");
        if (keyAndValue[0].equals("-")) {
          throw new ArgumentParseException("Parameters needs token before \"=\"");
        } else if (keyAndValue.length < 2) {
          throw new ArgumentParseException("Parameters needs value after \"=\"");
        }
        param.put(keyAndValue[0].substring(1), keyAndValue[1]);
      } else {  // sample_args or -example_flag
        args.add(command);
      }
    }
  }

  public int getCommandSize() {
    return args.size() + param.size();
  }

  public int getParamSize() {
    return args.size();
  }

  public String getCommand() {
    return mainCommand;
  }

  public String getCommand(int index) {
    return args.get(index);
  }

  /** Check if the command contains the specified token. */
  public boolean containsArgs(String token) {
    for (String argument : args) {
      if (token.equals(argument)) {
        return true;
      }
    }
    return false;
  }

  public boolean containsParam(String token) {
    return param.get(token) == null ? false : true;
  }

  /** Get string in command of token. */
  public String getString(String token) throws ArgumentParseException {
    if (param.get(token) == null) {
      throw new ArgumentParseException("Token \"" + token + "\" does not exist.");
    }
    return param.get(token);
  }

  /** Get int in command of token. */
  public int getInt(String token) throws ArgumentParseException {
    int value = 0;
    try {
      value = Integer.parseInt(param.get(token));
    } catch (NumberFormatException e) {
      if (param.get(token) == null) {
        throw new ArgumentParseException("Token \"" + token + "\" does not exist.");
      } else {
        throw new ArgumentParseException("Value " + param.get(token)
            + " cannot be converted to integer.");
      }
    }
    return value;
  }
}
