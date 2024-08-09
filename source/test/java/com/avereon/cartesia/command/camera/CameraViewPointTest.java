package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CameraViewPointTest extends CommandBaseTest {

	private final Command command = new CameraViewPoint();

	@Test
	void execute() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 0 ) ).setViewPoint( any() );
		assertThat( result ).isEqualTo( Command.Result.INCOMPLETE );
	}

	@Test
	void executeWithEvent() throws Exception {
		// given
		CommandTrigger trigger = new CommandTrigger( MouseEvent.MOUSE_RELEASED, MouseButton.MIDDLE );
		InputEvent event = createMouseEvent( trigger, 42, 87 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
		when( tool.screenToWorld( eq( new Point3D( 42.0, 87.0, 0.0 ) ) ) ).thenReturn( new Point3D( 1, 2, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setViewPoint( eq( new Point3D( 1, 2, 0 ) ) );
		assertThat( result ).isEqualTo( Command.Result.SUCCESS );
	}

	@Test
	void executeWithParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1,2" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setViewPoint( eq( new Point3D( 1, 2, 0 ) ) );
		assertThat( result ).isEqualTo( Command.Result.SUCCESS );
	}

	@Test
	void executeWithBadParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "bad parameter" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 0 ) ).setViewPoint( any() );
		assertThat( result ).isEqualTo( Command.Result.INVALID );
	}

}
