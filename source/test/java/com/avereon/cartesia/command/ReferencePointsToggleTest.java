package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.tool.CommandTask;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ReferencePointsToggleTest extends CommandBaseTest {

	@Test
	void testExecute() throws Exception {
		// given
		ReferencePointsToggle command = new ReferencePointsToggle();
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
		when( tool.isReferenceLayerVisible() ).thenReturn( true );

		// when
		Object result = command.execute( task );

		// then
		verify( tool, times( 1 ) ).setReferenceLayerVisible( eq( false ) );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
