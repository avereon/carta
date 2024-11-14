package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.*;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CameraZoomTest extends BaseCommandTest {

	private final Command command = new CameraZoom();

	/**
	 * Camera zoom with no parameters or event, should prompt the
	 * user to select an anchor point. The result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testRunTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( null );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Camera zoom with no parameters, but an event, should submit a
	 * {@link Value} command to pass the anchor point back to this command. The
	 * result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithZoomEvent() throws Exception {
		// given
		CommandTrigger trigger = getMod().getCommandMap().getTriggerByAction( "camera-zoom" );
		InputEvent event = createZoomEvent( trigger, 42, 87, Math.sqrt( 2 ) );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
		when( tool.screenToWorkplane( eq( 42.0 ), eq( 87.0 ), eq( 0.0 ) ) ).thenReturn( new Point3D( 1, 2, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).zoom( new Point3D( 1, 2, 0 ), Math.sqrt( 2 ) );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	/**
	 * Camera zoom with no parameters, but an event, should submit a
	 * {@link Value} command to pass the anchor point back to this command. The
	 * result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithScrollEventZoomIn() throws Exception {
		// given
		CommandTrigger trigger = getMod().getCommandMap().getTriggerByAction( "camera-zoom" );
		InputEvent event = createScrollEvent( trigger, 42, 87, 0, 1 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
		when( tool.screenToWorkplane( eq( 42.0 ), eq( 87.0 ), eq( 0.0 ) ) ).thenReturn( new Point3D( 1, 2, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).zoom( new Point3D( 1, 2, 0 ), BaseDesignTool.ZOOM_IN_FACTOR );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	/**
	 * Camera zoom with no parameters, but a scroll event, should submit a
	 * {@link Value} command to pass the anchor point back to this command. The
	 * result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithScrollEventZoomOut() throws Exception {
		// given
		CommandTrigger trigger = getMod().getCommandMap().getTriggerByAction( "camera-zoom" );
		InputEvent event = createScrollEvent( trigger, 42, 87, 0, -1 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
		when( tool.screenToWorkplane( eq( 42.0 ), eq( 87.0 ), eq( 0.0 ) ) ).thenReturn( new Point3D( 1, 2, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).zoom( new Point3D( 1, 2, 0 ), BaseDesignTool.ZOOM_OUT_FACTOR );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	/**
	 * Camera zoom with no parameters or event, should prompt the
	 * user to select an anchor point. The result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testRunTaskStepOneParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "sqrt(2)" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setZoom( Math.sqrt( 2 ) );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	/**
	 * Camera zoom with no parameters or event, should prompt the
	 * user to select an anchor point. The result should be incomplete.
	 *
	 */
	@Test
	void testRunTaskStepBadParameter() {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "bad parameter" );

		// when
		InvalidInputException exception = catchThrowableOfType( InvalidInputException.class, task::runTaskStep );

		// then
		verify( commandContext, times( 0 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( currentLayer, times( 0 ) ).addShape( any() );
		assertThat( exception.getInputRbKey() ).isEqualTo( "zoom" );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

}
