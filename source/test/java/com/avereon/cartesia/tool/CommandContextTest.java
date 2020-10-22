package com.avereon.cartesia.tool;

import com.avereon.cartesia.MockProgramProduct;
import com.avereon.cartesia.NumericTest;
import com.avereon.xenon.ProgramProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandContextTest implements NumericTest {

	private CommandContext processor;

	@BeforeEach
	void setup() {
		ProgramProduct product = new MockProgramProduct();
		processor = new CommandContext( product, null );
	}

	@Test
	void testFullCommand() {
		String command = "ll 0,0 1,1";
		// Executing this should give a line from 0,0 to 1,1
	}

}
