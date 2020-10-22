package com.avereon.cartesia.tool;

import com.avereon.cartesia.Design2dAssetType;
import com.avereon.cartesia.MockProgramProduct;
import com.avereon.cartesia.NumericTest;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.zarra.test.FxPlatformTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

public class CommandContextTest extends FxPlatformTestCase implements NumericTest {

	private ProgramProduct product;

	private Asset asset;

	private CommandContext processor;

	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		product = new MockProgramProduct();
		asset = new Asset( URI.create( "" ), new Design2dAssetType( product ) );
		processor = new CommandContext( product, null );
	}

	@Test
	void testFullCommand() {
		String command = "ll 0,0 1,1";
		// Executing this should give a line from 0,0 to 1,1
	}

	@Test
	void testSimpleCommand() {
		//DesignTool tool = new Design2dEditor( product, asset );
		// processor.submit( tool, command, parameters... );
	}

}
