package com.avereon.cartesia.command.view;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.CommandTask;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ViewCreateTest extends CommandBaseTest {

	private final ViewCreate command = new ViewCreate();

	@Test
	void testRunTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( any() );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testRunTaskStepWithOneParameter() throws Exception {
		String viewName = "Custom View";

		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, viewName );

		// when
		Object result = task.runTaskStep();

		// then
		// Check the created view
		assertThat( result ).isInstanceOf( DesignView.class );
		assertThat( ((DesignView)result).getName() ).isEqualTo( viewName );

		// Check the interactions
		verify( design, times( 1 ) ).addView( any() );
		verify( tool, times( 1 ) ).setCurrentView( (DesignView)result );
	}

}
