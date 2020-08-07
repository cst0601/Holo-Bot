package com.alchemist;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.alchemist.service.CommandUtil;

class TestCommandUtil {

	@Test
	void testParseCommand() {
		String command = ">holo  miko     watame";
		assertEquals(">holo", CommandUtil.parseCommand(command)[0]);
		assertEquals("miko", CommandUtil.parseCommand(command)[1]);
		assertEquals("watame", CommandUtil.parseCommand(command)[2]);
	}

}
