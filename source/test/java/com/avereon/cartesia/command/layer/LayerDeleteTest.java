package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLayer;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
		verify( tool ).setCurrentLayer( eq( parent ) );
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
		verify( tool ).setCurrentLayer( eq( parent ) );
	}

	// GetNextValidLayer Tests ---------------------------------------------------

	@Test
	void testGetNextValidLayerAtFirstLayer() {
		// given
		DesignLayer parent = new DesignLayer();
		DesignLayer layer0 = new DesignLayer().setOrder( 0 );
		DesignLayer layer1 = new DesignLayer().setOrder( 1 );
		parent.addLayer( layer0 );
		parent.addLayer( layer1 );

		// when
		DesignLayer next = command.getNextValidLayer( layer0 );

		// then
		assertThat( next ).isEqualTo( layer1 );
	}

	@Test
	void testGetNextValidLayerAtLastLayer() {
		// given
		DesignLayer parent = new DesignLayer();
		DesignLayer layer0 = new DesignLayer().setOrder( 0 );
		DesignLayer layer1 = new DesignLayer().setOrder( 1 );
		parent.addLayer( layer0 );
		parent.addLayer( layer1 );

		// when
		DesignLayer next = command.getNextValidLayer( layer1 );

		// then
		assertThat( next ).isEqualTo( layer0 );
	}

	@Test
	void testGetNextValidLayerWithOneChild() {
		// given
		DesignLayer parent = new DesignLayer();
		DesignLayer layer0 = new DesignLayer().setOrder( 0 );
		parent.addLayer( layer0 );

		// when
		DesignLayer next = command.getNextValidLayer( layer0 );

		// then
		assertThat( next ).isEqualTo( parent );
	}

}
