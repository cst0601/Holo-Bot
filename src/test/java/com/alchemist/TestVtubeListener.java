package com.alchemist;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.alchemist.service.VtubeListener;

class TestVtubeListener {

	@Test
	void testPraseArgv() {
		VtubeListener listener = new VtubeListener(null);
		String command = ">holo  miko     watame";
		assertEquals(">holo", listener.parseArgv(command)[0]);
		assertEquals("miko", listener.parseArgv(command)[1]);
		assertEquals("watame", listener.parseArgv(command)[2]);
	}

}
