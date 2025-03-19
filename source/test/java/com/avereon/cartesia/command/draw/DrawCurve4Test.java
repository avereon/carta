package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.base.Prompt;
import com.avereon.cartesia.data.DesignCubic;
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

public class DrawCurve4Test extends BaseCommandTest {

	private final DrawCurve4 command = new DrawCurve4();

	// Script Tests --------------------------------------------------------------

	/**
	 * Draw cubic with all parameters should set the cubic control points. A cubic
	 * should be added to the current layer. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testRunTaskStepWithAllParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,0", "-1,3", "1,-3", "3,0" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( currentLayer, times( 1 ) ).addShape( any( DesignCubic.class ) );
		verify( commandContext, times( 0 ) ).submit( eq( tool ), any( Prompt.class ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	// Interactive Tests ---------------------------------------------------------

	/**
	 * Draw cubic with no parameters or event, should prompt the
	 * user to select a start point. The result should be incomplete.
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
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview().stream().findFirst().orElse( null ) ).isInstanceOf( DesignCubic.class );
		assertThat( command.getPreview() ).hasSize( 1 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Draw cubic with one parameter should set the cubic origin. The
	 * result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithOneParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,0" );
		// Use the CLOSED_HAND cursor as a reticle cursor
		when( tool.getReticleCursor() ).thenReturn( Cursor.CLOSED_HAND );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( Cursor.CLOSED_HAND );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview().stream().findFirst().orElse( null ) ).isInstanceOf( DesignCubic.class );
		assertThat( command.getPreview() ).hasSize( 1 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Draw cubic with two parameters should set the cubic origin and origin
	 * control point. The result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithTwoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,0", "-1,3" );
		// Use the CLOSED_HAND cursor as a reticle cursor
		when( tool.getReticleCursor() ).thenReturn( Cursor.CLOSED_HAND );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( Cursor.CLOSED_HAND );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview().stream().findFirst().orElse( null ) ).isInstanceOf( DesignCubic.class );
		assertThat( command.getPreview() ).hasSize( 1 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Draw cubic with three parameters should set the cubic origin, origin
	 * control point and point control point. The result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithThreeParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,0", "-1,3", "1,-3" );
		// Use the CLOSED_HAND cursor as a reticle cursor
		when( tool.getReticleCursor() ).thenReturn( Cursor.CLOSED_HAND );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( Cursor.CLOSED_HAND );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview().stream().findFirst().orElse( null ) ).isInstanceOf( DesignCubic.class );
		assertThat( command.getPreview() ).hasSize( 1 );
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
			Arguments.of( new String[]{ "bad parameter" }, "start-point" ),
			Arguments.of( new String[]{ "-3,0", "bad parameter" }, "control-point" ),
			Arguments.of( new String[]{ "-3,0", "-1,3", "bad parameter" }, "control-point" ),
			Arguments.of( new String[]{ "-3,0", "-1,3", "1,-3", "bad parameter" }, "end-point" )
		);
	}

	@Test
	void testExecuteWithBadParameterFiveIsIgnored() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,0", "-1,3", "1,-3", "3,0", "bad parameter" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( currentLayer, times( 1 ) ).addShape( any( DesignCubic.class ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
