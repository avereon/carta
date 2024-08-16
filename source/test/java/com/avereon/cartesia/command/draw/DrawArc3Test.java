package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.command.CommandTask;
import javafx.scene.Cursor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DrawArc3Test extends CommandBaseTest {

	private final DrawArc3 command = new DrawArc3();

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
	void testRunTaskStepWithThreeParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "8,3", "1,0", "1,1" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 0 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( currentLayer, times( 1 ) ).addShape( any( DesignArc.class ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	// Stepped tests -------------------------------------------------------------

	@Test
	void testRunTaskStepWithOneStep() throws Exception {
		// given
		CommandTask task1 = new CommandTask( commandContext, tool, null, null, command );
		task1.runTaskStep();

		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "8,3" );
		// Use the CLOSED_HAND cursor as a reticle cursor
		when( tool.getReticleCursor() ).thenReturn( Cursor.CLOSED_HAND );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 2 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( Cursor.CLOSED_HAND );
		// There is not enough information to provide a preview arc, so a reference line is used
		assertThat( command.getReference().stream().findFirst().orElse( null ) ).isInstanceOf( DesignLine.class );
		assertThat( command.getReference() ).hasSize( 1 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testRunTaskStepWithTwoStep() throws Exception {
		// given
		CommandTask task1 = new CommandTask( commandContext, tool, null, null, command );
		task1.runTaskStep();
		CommandTask task2 = new CommandTask( commandContext, tool, null, null, command, "8,3" );
		task2.runTaskStep();

		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "8,3", "1,0" );
		// Use the CLOSED_HAND cursor as a reticle cursor
		when( tool.getReticleCursor() ).thenReturn( Cursor.CLOSED_HAND );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 3 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( Cursor.CLOSED_HAND );
		// The reference line is replaced with a preview arc
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview().stream().findFirst().orElse( null ) ).isInstanceOf( DesignArc.class );
		assertThat( command.getPreview() ).hasSize( 1 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testRunTaskStepWithThreeSteps() throws Exception {
		// given
		CommandTask task1 = new CommandTask( commandContext, tool, null, null, command );
		task1.runTaskStep();
		CommandTask task2 = new CommandTask( commandContext, tool, null, null, command, "8,3" );
		task2.runTaskStep();
		CommandTask task3 = new CommandTask( commandContext, tool, null, null, command, "8,3", "1,0" );
		task3.runTaskStep();

		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "8,3", "1,0", "1,1", "0" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 3 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( currentLayer, times( 1 ) ).addShape( any( DesignArc.class ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	// Bad Parameter Tests -------------------------------------------------------

	@ParameterizedTest
	@MethodSource( "provideParametersForTestWithParameters" )
	void testRunTaskStepWithBadParameters( Object[] parameters, String rbKey ) throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, parameters );

		// when
		InvalidInputException exception = catchThrowableOfType( InvalidInputException.class, task::runTaskStep );

		// then
		verify( commandContext, times( 0 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( currentLayer, times( 0 ) ).addShape( any() );
		assertThat( exception.getInputRbKey() ).isEqualTo( rbKey );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	private static Stream<Arguments> provideParametersForTestWithParameters() {
		return Stream.of(
			Arguments.of( new String[]{ "bad parameter" }, "start-point" ),
			Arguments.of( new String[]{ "8,3", "bad parameter" }, "mid-point" ),
			Arguments.of( new String[]{ "8,3", "1,0", "bad parameter" }, "end-point" )
		);
	}

	@Test
	void testExecuteWithBadParameterFourIsIgnored() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "8,3", "1,0", "1,1", "bad parameter" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( currentLayer, times( 1 ) ).addShape( any( DesignArc.class ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
