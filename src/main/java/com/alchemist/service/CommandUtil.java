package com.alchemist.service;

public class CommandUtil {
	public static String [] parseCommand(String command) {
		return command.split("\\s+");
	}
}
