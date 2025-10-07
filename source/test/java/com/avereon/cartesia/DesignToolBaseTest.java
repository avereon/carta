package com.avereon.cartesia;

import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.asset.Resource;
import com.avereon.zerra.javafx.Fx;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

public abstract class DesignToolBaseTest extends BaseCartesiaUnitTest {

	@Mock
	protected DesignTool tool;

	@Mock
	protected Resource resource;

	static {
		// All tools will want FX started
		Fx.startup();
	}

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();

		lenient().when( tool.getAsset() ).thenReturn( resource );
		lenient().when( tool.snapToGrid( any() ) ).then( i -> i.getArgument( 0 ) );
		lenient().when( tool.getScreenToWorldTransform() ).thenReturn( Fx.IDENTITY_TRANSFORM );
	}

}
