package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignLine;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DeleteTest extends BaseCommandTest {

	private final Delete command = new Delete();

	// Script Tests --------------------------------------------------------------

	@Test
	void testExecuteWithNoSelectedShapes() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void testExecuteWithSelectedShapes() throws Exception {
		// given
		DesignLine line = new DesignLine( 0, 0, 0, 0 );
		currentLayer = Mockito.spy( new DesignLayer() );
		currentLayer.addShape( line );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		when( tool.getSelectedShapes() ).thenReturn( List.of( line ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( currentLayer, times( 1 ) ).removeShape( line );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	// Bad Parameter Tests -------------------------------------------------------

	@Test
	void testExecuteWithBadParameterOneIsIgnored() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, BAD_TEXT_PARAMETER );

		// when
		Object result = task.runTaskStep();

		// then
		//verify( currentLayer, times( 1 ) ).addShape( any( DesignLine.class ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
