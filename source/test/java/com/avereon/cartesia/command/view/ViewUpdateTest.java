package com.avereon.cartesia.command.view;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.CommandTask;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ViewUpdateTest extends CommandBaseTest {

	@Mock
	private DesignView view;

	private final ViewUpdate command = new ViewUpdate();

	@Test
	void runTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Pretend there is not a current view
		when( tool.getCurrentView() ).thenReturn( null );

		// when
		Object result = task.runTaskStep();

		// then
		verify( design, times( 0 ) ).addView( any() );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void runTaskStepWithCurrentView() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Pretend there is a current view
		when( tool.getCurrentView() ).thenReturn( view );

		// when
		Object result = task.runTaskStep();

		// then
		verify( view, times( 1 ) ).setOrigin( any() );
		verify( view, times( 1 ) ).setZoom( any() );
		verify( view, times( 1 ) ).setRotate( any() );
		verify( view, times( 1 ) ).setLayers( any() );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
