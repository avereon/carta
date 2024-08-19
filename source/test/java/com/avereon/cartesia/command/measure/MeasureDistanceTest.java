package com.avereon.cartesia.command.measure;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.data.DesignLine;
import javafx.scene.Cursor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MeasureDistanceTest extends CommandBaseTest {

	private final MeasureDistance command = new MeasureDistance();

	// Script Tests --------------------------------------------------------------

	/**
	 * Measure distance with all parameters should calculate the distance and
	 * display it as a notice. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,3", "3,-3" );
		when( commandContext.isInteractive() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( noticeManager, times( 1 ) ).addNotice( any() );
		assertThat( result ).isEqualTo( 8.48528137423857 );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	// Interactive Tests ---------------------------------------------------------

	/**
	 * Measure distance with no parameters or event, should prompt the
	 * user to select a start point. The result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testRunTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		when( tool.getReticleCursor() ).thenReturn( Cursor.CROSSHAIR );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( Cursor.CROSSHAIR );
		assertThat( command.getReference().stream().findFirst().orElse( null ) ).isInstanceOf( DesignLine.class );
		assertThat( command.getReference() ).hasSize( 1 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testRunTaskStepWithOneParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "8,3" );
		when( tool.getReticleCursor() ).thenReturn( Cursor.CROSSHAIR );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( Cursor.CROSSHAIR );
		assertThat( command.getReference().getFirst() ).isInstanceOf( DesignLine.class );
		assertThat( command.getReference() ).hasSize( 1 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	// Bad Parameter Tests -------------------------------------------------------

	@ParameterizedTest
	@MethodSource( "provideParametersForTestWithParameters" )
	void testRunTaskStepWithBadParameters( Object[] parameters, String rbKey ) {
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
		return Stream.of( Arguments.of( new String[]{ "bad parameter" }, "start-point" ), Arguments.of( new String[]{ "-3,3", "bad parameter" }, "end-point" ) );
	}

	@Test
	void testExecuteWithBadParameterThreeIsIgnored() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,3", "3,-3", "bad parameter" );
		when( commandContext.isInteractive() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( noticeManager, times( 1 ) ).addNotice( any() );
		assertThat( result ).isEqualTo( 8.48528137423857 );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

}
