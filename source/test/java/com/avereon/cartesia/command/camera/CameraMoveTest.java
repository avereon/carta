package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.command.Value;
import com.avereon.cartesia.command.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.input.InputEvent;
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

public class CameraMoveTest extends CommandBaseTest {

	private final CameraMove command = new CameraMove();

	/**
	 * Camera move with no parameters or event, should prompt the
	 * user to select an anchor point. The result should be incomplete.
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
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Camera move with no parameters, but an event, should submit a
	 * {@link Value} command to pass the anchor point back to this command. The
	 * result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithNoParametersAndEvent() throws Exception {
		// given
		CommandTrigger trigger = getMod().getCommandMap().getTriggerByAction( "camera-move" );
		InputEvent event = createMouseEvent( trigger, 48, 17 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
		// Pretend the world anchor has been set
		when( commandContext.getWorldAnchor() ).thenReturn( new Point3D( -2, 1, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Value.class ), eq( new Point3D( -2, 1, 0 ) ) );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Camera move with one parameter should set the anchor. The
	 * result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithOneParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-1,1" );
		// Use the CLOSED_HAND cursor as a reticle cursor
		when( tool.getReticleCursor() ).thenReturn( Cursor.CLOSED_HAND );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( Cursor.CLOSED_HAND );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Camera move with two parameters should set both the anchor and the target,
	 * and then move the camera accordingly. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithTwoParameters() throws Exception {
		// given
		command.setOriginalViewPoint( new Point3D( 0, 0, 0 ) );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,3", "3,-3" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setViewPoint( eq( new Point3D( -6, 6, 0 ) ) );
		verify( tool, times( 0 ) ).setCursor( any() );
		verify( commandPrompt, times( 0 ) ).clear();
		assertThat( result ).isEqualTo( SUCCESS );
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
			Arguments.of( new String[]{ "bad parameter" }, "pan-anchor" ),
			Arguments.of( new String[]{ "1,3", "bad parameter" }, "pan-target" )
		);
	}

}
