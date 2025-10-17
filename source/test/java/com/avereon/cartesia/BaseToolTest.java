package com.avereon.cartesia;

import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.resource.Resource;
import com.avereon.zerra.javafx.Fx;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

public abstract class BaseToolTest extends BaseCartesiaUnitTest {

	@Mock
	protected DesignTool tool;

	@Mock
	protected Resource resource;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();

		lenient().when( tool.getResource() ).thenReturn( resource );
		lenient().when( tool.snapToGrid( any() ) ).then( i -> i.getArgument( 0 ) );
		lenient().when( tool.getScreenToWorldTransform() ).thenReturn( Fx.IDENTITY_TRANSFORM );
	}

}
