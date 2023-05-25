package com.avereon.cartesia.tool;

import com.avereon.cartesia.BaseCartesiaTest;
import com.avereon.xenon.ProgramTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// NEXT Well, time to make some choices. Either tool tests really require the
// program to be running, or we need to simplify how tool tests are run. It begs
// the question, can a tool be tested without the program running. Probably not
// very well since many of the supporting framework for tools is in the program.
// I'm sure some pieces can be unit tested, but the tool as a whole probably
// need the program running. This would certainly be true for UI tests.

// NOTE Then I moved on to improving the base unit tests. Once again I tried to
// put the base unit tests in Xenon itself. Turns out, JUnit really doesn't like
// it when you do that. So..after burning even more time a separate library for
// the base mod tests will need to be created.

// NOTE Wanting to also have base program tests, "it would be nice" if there was
// a separate library to contain the base tests for the program. I suppose these
// are already in the Zerra library, but duplicates also seem to be in the Xenon
// library. Really, it looks like the Zerra library should be for mods and
// should not be used for Xenon.

//@Disabled
public class FxRenderDesignToolTest extends BaseCartesiaTest {

	private FxRenderDesignTool tool;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();

		URI uri = getClass().getResource( "/design-tool-test.cartesia2d" ).toURI();
		Future<ProgramTool> future = getProgram().getAssetManager().openAsset( uri, FxRenderDesignTool.class );
		tool = (FxRenderDesignTool)future.get();
	}

	@Test
	void canRun() {
		assertNotNull( tool );
	}

}
