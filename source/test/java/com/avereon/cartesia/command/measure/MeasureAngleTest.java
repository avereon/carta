package com.avereon.cartesia.command.measure;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.base.Prompt;
import com.avereon.cartesia.data.DesignArc;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MeasureAngleTest extends BaseCommandTest {

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
		when( commandContext.isInteractive() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( noticeManager, times( 1 ) ).addNotice( any() );
		assertThat( result ).isEqualTo( 45.0 );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	// Interactive Tests ---------------------------------------------------------

	/**
	 * Measure arc with no parameters or event, should prompt the
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
		assertThat( command.getReference().getFirst() ).isInstanceOf( DesignLine.class );
		assertThat( command.getReference().get( 1 ) ).isInstanceOf( DesignArc.class );
		assertThat( command.getReference() ).hasSize( 2 );
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
		return Stream.of(
			Arguments.of( new String[]{ "bad parameter" }, "center" ),
			Arguments.of( new String[]{ "-3,3", "bad parameter" }, "start" ),
			Arguments.of( new String[]{ "-3,3", "3,-3", "bad parameter" }, "extent" ),
			Arguments.of( new String[]{ "-3,3", "3,-3", "3,3", "bad parameter" }, "spin" )
		);
	}

	@Test
	void testExecuteWithBadParameterFiveIsIgnored() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,3", "3,-3", "3,3", "0", "bad parameter" );
		when( commandContext.isInteractive() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( noticeManager, times( 1 ) ).addNotice( any() );
		assertThat( result ).isEqualTo( 45.0 );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

}
