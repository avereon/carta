package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@CustomLog
@ExtendWith( MockitoExtension.class )
public class AnchorTest extends CommandBaseTest {

	private final Anchor command = new Anchor();

	@Test
	void testExecuteWithNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 0 ) ).setScreenAnchor( any() );
		verify( commandContext, times( 0 ) ).setWorldAnchor( any() );
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( any() );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testExecuteWithParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1/2,-3" );
		when( tool.worldToScreen( eq( new Point3D( 0.5, -3, 0 ) ) ) ).thenReturn( new Point3D( 84, 127, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).setScreenAnchor( eq( new Point3D( 84, 127, 0 ) ) );
		verify( commandContext, times( 1 ) ).setWorldAnchor( eq( new Point3D( 0.5, -3, 0 ) ) );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void testExecuteWithEvent() throws Exception {
		// given
		CommandTrigger trigger = getMod().getCommandMap().getTriggerByAction( "anchor" );
		InputEvent event = createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, false, false, false, 48, 17 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
		when( tool.screenToWorld( eq( new Point3D( 48, 17, 0 ) ) ) ).thenReturn( new Point3D( -2, 1, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).setScreenAnchor( eq( new Point3D( 48, 17, 0 ) ) );
		verify( commandContext, times( 1 ) ).setWorldAnchor( eq( new Point3D( -2, 1, 0 ) ) );
		assertThat( event.isConsumed() ).isTrue();
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void testExecuteWithBadParameter() {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "bad parameter" );

		// when
		InvalidInputException exception = catchThrowableOfType( InvalidInputException.class, task::runTaskStep );

		// then
		verify( commandContext, times( 0 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( currentLayer, times( 0 ) ).addShape( any() );
		assertThat( exception.getInputRbKey() ).isEqualTo( "select-point" );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

}
