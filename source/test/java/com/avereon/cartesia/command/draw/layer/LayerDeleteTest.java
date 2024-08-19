package com.avereon.cartesia.command.draw.layer;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.layer.LayerDelete;
import com.avereon.cartesia.data.DesignLayer;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class LayerDeleteTest extends CommandBaseTest {

	private final LayerDelete command = new LayerDelete();

	@Mock
	private DesignLayer parent;

	// Script Tests --------------------------------------------------------------

	@Test
	void testExecuteWithAllParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		when( selectedLayer.getLayer() ).thenReturn( parent );
		when( parent.getLayers() ).thenReturn( List.of( currentLayer ) );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( selectedLayer );
	}

	// Bad Parameter Tests -------------------------------------------------------

	@Test
	void testExecuteWithBadParameterOneIsIgnored() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, BAD_PARAMETER );
		when( selectedLayer.getLayer() ).thenReturn( parent );
		when( parent.getLayers() ).thenReturn( List.of( currentLayer ) );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( selectedLayer );
	}

}
