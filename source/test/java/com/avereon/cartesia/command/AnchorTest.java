package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith( MockitoExtension.class )
public class AnchorTest extends CommandBaseTest {

	private final Anchor command = new Anchor();

	@Test
	void testExecuteWithNoParameterOrEvent() throws Exception {
		// given
		CommandTask commandTask = new CommandTask( context, tool, trigger, event, command );

		// when
		Object result = command.execute( commandTask );

		// then
		assertThat( result ).isEqualTo( Command.FAILURE );
		verify( context, times( 0 ) ).setScreenAnchor( any() );
		verify( context, times( 0 ) ).setWorldAnchor( any() );
	}

	@Test
	void testExecuteWithParameter() throws Exception {
		// given
		CommandTask commandTask = new CommandTask( context, tool, trigger, event, command, "1/2,-3" );
		when( tool.worldToScreen( eq( new Point3D( 0.5, -3, 0 ) ) ) ).thenReturn( new Point3D( 84, 127, 0 ) );

		// when
		Object result = command.execute( commandTask );

		// then
		assertThat( result ).isEqualTo( Command.SUCCESS );
		verify( context, times( 1 ) ).setScreenAnchor( eq( new Point3D( 84, 127, 0 ) ) );
		verify( context, times( 1 ) ).setWorldAnchor( eq( new Point3D( 0.5, -3, 0 ) ) );
	}

	@Test
	void testExecuteWithEvent() throws Exception {
		// given
		InputEvent event = createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, false, false, false, 48, 17 );
		CommandTask commandTask = new CommandTask( context, tool, trigger, event, command );
		when( tool.screenToWorld( eq( new Point3D( 48, 17, 0 ) ) ) ).thenReturn( new Point3D( -2, 1, 0 ) );

		// when
		Object result = command.execute( commandTask );

		// then
		assertThat( result ).isEqualTo( Command.SUCCESS );
		verify( context, times( 1 ) ).setScreenAnchor( eq( new Point3D( 48, 17, 0 ) ) );
		verify( context, times( 1 ) ).setWorldAnchor( eq( new Point3D( -2, 1, 0 ) ) );
	}

}
