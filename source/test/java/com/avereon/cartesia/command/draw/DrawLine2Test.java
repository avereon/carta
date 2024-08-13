package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandTask;
import javafx.scene.Cursor;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DrawLine2Test extends CommandBaseTest {

	private final DrawLine2 command = new DrawLine2();

	/**
	 * Draw line with no parameters or event, should prompt the
	 * user to select an origin point. The result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testRunTaskStepNoParameters() throws Exception {
		// given
		//DesignLayer preview = new DesignLayer();
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Use the CLOSED_HAND cursor as a reticle cursor
		when( tool.getReticleCursor() ).thenReturn( Cursor.CLOSED_HAND );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( Cursor.CLOSED_HAND );
		assertThat( command.getPreview().stream().findFirst().orElse( null ) ).isInstanceOf( DesignLine.class );
		assertThat( command.getPreview() ).hasSize( 1 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Draw line with one parameter should set the line origin. The
	 * result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithOneParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,3" );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( command.getPreview().stream().findFirst().orElse( null ) ).isInstanceOf( DesignLine.class );
		assertThat( command.getPreview() ).hasSize( 1 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Draw line with two parameters should set both the origin
	 * and the point, and then add the line to the current layer. The
	 * result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithTwoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,3", "3,-3" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( currentLayer, times( 1 ) ).addShape( any( DesignLine.class ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void testRunTaskStepWithInvalidParameterOne() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "bad parameter" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( currentLayer, times( 0 ) ).addShape( any() );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( INVALID );
	}

	@Test
	void testRunTaskStepWithInvalidParameterTwo() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1,3", "bad parameter" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( currentLayer, times( 0 ) ).addShape( any() );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( INVALID );
	}

	@Test
	void testExecuteWithBadParameterThreeIsIgnored() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,3", "3,-3", "bad parameter" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( currentLayer, times( 1 ) ).addShape( any( DesignLine.class ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
