package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.CommandBaseTest;
import org.junit.jupiter.api.Test;

public class JoinTest extends CommandBaseTest {

	private final Join command = new Join();

	// Script Tests --------------------------------------------------------------

	/**
	 * Join should ask the user for two shapes to join, by trimming the shapes to
	 * the nearest common intersection point. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParameters() throws Exception {
		// NEXT Set up the test for the Join command
	}

}
