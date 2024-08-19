package com.avereon.cartesia.command.measure;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.data.DesignLine;
import javafx.scene.Cursor;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MeasureAngleTest extends CommandBaseTest {

	private final MeasureAngle command = new MeasureAngle();

	// Script Tests --------------------------------------------------------------

	/**
	 * Measure angle with three parameters should calculate the angle and display
	 * it as a notice. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,3", "3,-3", "3,3" );

		// when
		Object result = task.runTaskStep();

		// then
		//verify( currentLayer, times( 1 ) ).addShape( any( DesignLine.class ) );
		assertThat( command.getReference() ).hasSize( 0 );
		//assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( 45.0 );
	}

	// Interactive Tests ---------------------------------------------------------

	/**
	 * Draw arc with no parameters or event, should prompt the
	 * user to select an origin point. The result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testRunTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Use the CLOSED_HAND cursor as a reticle cursor
		when( tool.getReticleCursor() ).thenReturn( Cursor.CLOSED_HAND );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( Cursor.CLOSED_HAND );
		assertThat( command.getReference().stream().findFirst().orElse( null ) ).isInstanceOf( DesignLine.class );
		assertThat( command.getReference() ).hasSize( 1 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testRunTaskStepWithOneParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "8,3" );
		// Use the CLOSED_HAND cursor as a reticle cursor
		when( tool.getReticleCursor() ).thenReturn( Cursor.CLOSED_HAND );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( Cursor.CLOSED_HAND );
		assertThat( command.getReference().stream().findFirst().orElse( null ) ).isInstanceOf( DesignLine.class );
		//assertThat( command.getReference().stream().findFirst().orElse( null ) ).isInstanceOf( DesignLine.class );
		assertThat( command.getReference() ).hasSize( 2 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

}
