package com.avereon.cartesia.command.view;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.command.CommandTask;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ViewDeleteTest extends CommandBaseTest {

	private final ViewDelete command = new ViewDelete();

	@Test
	void runTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Pretend there is not a current view
		when( tool.getCurrentView() ).thenReturn( null );

		// when
		Object result = task.runTaskStep();

		// then
		verify( design, times( 0 ) ).removeView( any() );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void runTaskStepWithCurrentView() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Pretend there is a current view
		when( tool.getCurrentView() ).thenReturn( new DesignView().setName( "Custom View" ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( design, times( 1 ) ).removeView( any() );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
