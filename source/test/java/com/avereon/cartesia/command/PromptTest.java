package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignCommandContext;
import org.junit.jupiter.api.Test;

public class PromptTest extends CommandBaseTest {

	@Test
	void testExecute() throws Exception {
		// given
		Prompt command = new Prompt("Hello world:", DesignCommandContext. Input.TEXT);
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
		// Pretend the grid is visible
		//when( tool.isGridVisible() ).thenReturn( true );

		// when
		Object result = command.execute( task );

		// NEXT Verify the command results

		// then
		///verify( tool, times( 1 ) ).setGridVisible( false );
		//assertThat( result ).isEqualTo( SUCCESS );
	}


}
