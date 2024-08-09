package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CameraZoomWindowTest extends CommandBaseTest {

	private final Command command = new CameraZoomWindow();

	/**
	 * Camera zoom by window with no parameters or event, should prompt the
	 * user to select an anchor point. The result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testRunTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		when( tool.getAsset() ).thenReturn( asset );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( null );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Camera zoom by window with one parameter should set the anchor. The
	 * result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithOneParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-3,3" );
		//when( tool.worldToScreen( eq( new Point3D( -3, 3, 0 ) ) ) ).thenReturn( new Point3D( 72, 144, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 0 ) ).setWorldAnchor( eq( new Point3D( -3, 3, 0 ) ) );
		verify( commandContext, times( 0 ) ).setScreenAnchor( eq( new Point3D( 72, 144, 0 ) ) );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Camera zoom by window with two parameters should set both the anchor
	 * and the corner, and then zoom the view to the window bounds. The
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
		verify( tool, times( 1 ) ).setWorldViewport( eq( new BoundingBox( -3, -3, 6, 6 ) ) );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
