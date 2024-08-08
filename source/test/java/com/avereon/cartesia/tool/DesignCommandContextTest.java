package com.avereon.cartesia.tool;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.Anchor;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.command.SelectByPoint;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DesignCommandContextTest extends CommandBaseTest {

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		this.commandContext = spy( new DesignCommandContext( module ) );
		lenient().doReturn( commandPrompt ).when( commandContext ).getCommandPrompt();
	}

	@Test
	void handleWithMouseEventAndTrigger() {
		// given
		MouseEvent event = createMouseEvent( tool, null, MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, false, false, false, 48, 17 );

		// when
		commandContext.handle( event );

		// then
		verify( commandContext, times( 1 ) ).submitCommand( any( CommandTask.class ) );
		verify( commandContext, times( 0 ) ).forwardCommandToCommandStack( any() );
	}

	@Test
	void handleWithMouseEventNoTrigger() {
		// given
		MouseEvent event = createMouseEvent( tool, null, MouseEvent.ANY, MouseButton.PRIMARY, false, false, false, false, false, 48, 17 );

		// when
		commandContext.handle( event );

		// then
		verify( commandContext, times( 0 ) ).submitCommand( any( CommandTask.class ) );
		verify( commandContext, times( 1 ) ).forwardCommandToCommandStack( any() );
	}

	/**
	 * This is about the simplest command test possible and still be realistic.
	 * This test uses the Anchor command to verify the
	 * {@link DesignCommandContext#doProcessCommands} logic.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	@SuppressWarnings( "unchecked" )
	void doProcessCommandsWithAnchor() throws Exception {
		// given
		// Submitting Anchor without any parameters
		// will cause a Prompt to be added to the stack
		commandContext.submit( tool, new Anchor() );
		assertThat( commandContext.getCommandStackDepth() ).isEqualTo( 1 );
		verify( module, times( 1 ) ).task( eq( "process-commands" ), any( Callable.class ) );

		// when
		Object result1 = commandContext.doProcessCommands();

		// then
		verify( module, times( 2 ) ).task( eq( "process-commands" ), any( Callable.class ) );
		assertThat( commandContext.getCommandStackDepth() ).isEqualTo( 2 );
		assertThat( commandContext.getCommand( 0 ).getCommand() ).isInstanceOf( Prompt.class );
		assertThat( commandContext.getCommand( 1 ).getCommand() ).isInstanceOf( Anchor.class );
		assertThat( result1 ).isEqualTo( INCOMPLETE );

		// given
		commandContext.submit( tool, new SelectByPoint(), "47,13" );
		verify( module, times( 3 ) ).task( eq( "process-commands" ), any( Callable.class ) );
		assertThat( commandContext.getCommandStackDepth() ).isEqualTo( 3 );

		// when
		Object result2 = commandContext.doProcessCommands();

		// then
		assertThat( commandContext.getCommandStackDepth() ).isEqualTo( 0 );
		assertThat( result2 ).isEqualTo( SUCCESS );
		assertThat( commandContext.getWorldAnchor() ).isEqualTo( new Point3D( 47, 13, 0 ) );
	}

}
