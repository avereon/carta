package com.avereon.cartesia;

import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.asset.Asset;
import com.avereon.zarra.javafx.Fx;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

public abstract class DesignToolBaseTest extends BaseCartesiaUnitTest {

	@Mock
	protected DesignTool tool;

	@Mock
	protected Asset asset;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();

		// All tools will want FX started
		Fx.startup();

		lenient().when( tool.getAsset() ).thenReturn( asset );
		lenient().when( tool.snapToWorkplane( any() ) ).then( i -> i.getArgument( 0 ) );
		lenient().when( tool.getScreenToWorldTransform() ).thenReturn( Fx.IDENTITY_TRANSFORM );
	}

}
