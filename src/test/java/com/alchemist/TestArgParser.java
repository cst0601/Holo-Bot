package com.alchemist;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.alchemist.exceptions.ArgumentParseException;

class TestArgParser {

	@Test
	void testCommandSize() {
		try {
			ArgParser parser = new ArgParser(">holo   schedules  -size=100 ");
			parser.parse();
			assertEquals(3, parser.getCommandSize());
		} catch (ArgumentParseException e) {
			fail();
		}
	}
	
	@Test
	void testParserParamWithoutToken() {
		ArgumentParseException thrown = assertThrows(
				ArgumentParseException.class,
				() -> {
					ArgParser parser = new ArgParser(">holo schedules -=100   ");
					parser.parse();
				},
				"Excepted constructor to throw"
		);
		
		assertTrue(thrown.getMessage().equals("Parameters needs token before \"=\""));
	}
	
	@Test
	void testParserParamWithoutValue() {
		ArgumentParseException thrown = assertThrows(
				ArgumentParseException.class,
				() -> {
					ArgParser parser = new ArgParser(">holo schedules -size=   ");
					parser.parse();
				},
				"Excepted constructor to throw"
		);
		
		assertTrue(thrown.getMessage().equals("Parameters needs value after \"=\""));
	}
	
	@Test
	void testGetCommand() {
		try {
			ArgParser parser = new ArgParser("   >holo   schedules  -size=100 ");
			parser.parse();
			assertEquals(">holo", parser.getCommand());
		} catch (ArgumentParseException e) {
			fail();
		}
	}
	
	@Test
	void testCannotConvertToInteger() {
		ArgumentParseException thrown = assertThrows(
				ArgumentParseException.class,
				() -> {
					ArgParser parser = new ArgParser("   >holo   schedules  -size=not_int ");
					parser.parse();
					parser.getInt("size"); 
				},
				"Excepted get int to throw"
		);
		assertTrue(thrown.getMessage().equals("Value not_int cannot be converted to integer."));
	}
	
	@Test
	void testGetIntTokenNotFound() {
		ArgumentParseException thrown = assertThrows(
				ArgumentParseException.class,
				() -> {
					ArgParser parser = new ArgParser("   >holo   schedules  -size=100 ");
					parser.parse();
					parser.getInt("not_valid_token"); 
				},
				"Excepted get int to throw"
		);
		assertTrue(thrown.getMessage().equals("Token \"not_valid_token\" does not exist."));
	}
	
	@Test
	void testGetStringTokenNotFound() {
		ArgumentParseException thrown = assertThrows(
				ArgumentParseException.class,
				() -> {
					ArgParser parser = new ArgParser("   >holo   schedules  -size=100 ");
					parser.parse();
					parser.getString("not_valid_token"); 
				},
				"Excepted get int to throw"
		);
		assertTrue(thrown.getMessage().equals("Token \"not_valid_token\" does not exist."));
	}
	
	@Test
	void testNormalFlag() {
		try {
			ArgParser parser = new ArgParser("   >holo   schedules  -flag ");
			parser.parse();
			assertTrue(parser.containsArgs("-flag"));
		} catch (ArgumentParseException e) {
			fail();
		}
	}

}
