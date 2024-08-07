package com.avereon.cartesia.tool;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.Anchor;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.command.SelectByPoint;
import com.avereon.cartesia.command.SelectByWindowContain;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
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
		commandContext = spy( new DesignCommandContext( module ) );
		lenient().doReturn( commandPrompt ).when( commandContext ).getCommandPrompt();
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

	@Test
	@SuppressWarnings( "unchecked" )
	void doProcessCommandsWithSelectByWindowContains() throws Exception {
		// given
		// Submitting Anchor without any parameters
		// will cause a Prompt to be added to the stack
		commandContext.submit( tool, new SelectByWindowContain() );
		assertThat( commandContext.getCommandStackDepth() ).isEqualTo( 1 );
		verify( module, times( 1 ) ).task( eq( "process-commands" ), any( Callable.class ) );

		// when
		Object result1 = commandContext.doProcessCommands();

		// then
		verify( module, times( 2 ) ).task( eq( "process-commands" ), any( Callable.class ) );
		assertThat( commandContext.getCommandStackDepth() ).isEqualTo( 2 );
		assertThat( commandContext.getCommand( 0 ).getCommand() ).isInstanceOf( Prompt.class );
		assertThat( commandContext.getCommand( 1 ).getCommand() ).isInstanceOf( SelectByWindowContain.class );
		assertThat( result1 ).isEqualTo( INCOMPLETE );

		// Add an Anchor triggered by a MOUSE_PRESSED event

		// given
		CommandTrigger anchorTrigger = getMod().getCommandMap().getTriggerByAction( "anchor" );
		InputEvent anchorEvent = createMouseEvent( anchorTrigger, 5, 5 );
		CommandTask anchorTask = new CommandTask( commandContext, tool, anchorTrigger, anchorEvent, new Anchor() );
		commandContext.submit( anchorTask );
		assertThat( commandContext.getCommand( 0 ).getCommand() ).isInstanceOf( Anchor.class );
		assertThat( commandContext.getCommandStackDepth() ).isEqualTo( 3 );
		verify( module, times( 3 ) ).task( eq( "process-commands" ), any( Callable.class ) );

		// when
		Object result2 = commandContext.doProcessCommands();

		// then
		// Should be back to the prompt command
		assertThat( commandContext.getCommand( 0 ).getCommand() ).isInstanceOf( Prompt.class );
		assertThat( commandContext.getCommandStackDepth() ).isEqualTo( 2 );
		assertThat( result2 ).isEqualTo( INCOMPLETE );

		// Add a SelectByPoint triggered by a MOUSE_RELEASED event

		// given
		CommandTrigger selectTrigger = getMod().getCommandMap().getTriggerByAction( "select-point" );
		InputEvent selectEvent = createMouseEvent( selectTrigger, 48, 17 );
		CommandTask selectTask = new CommandTask( commandContext, tool, selectTrigger, selectEvent, new SelectByPoint() );
		commandContext.submit( selectTask );
		assertThat( commandContext.getCommand( 0 ).getCommand() ).isInstanceOf( SelectByPoint.class );
		assertThat( commandContext.getCommandStackDepth() ).isEqualTo( 3 );
		verify( module, times( 4 ) ).task( eq( "process-commands" ), any( Callable.class ) );

		// when
		Object result3 = commandContext.doProcessCommands();

		// then
		assertThat( commandContext.getCommand( 0 ).getCommand() ).isInstanceOf( Prompt.class );
		assertThat( commandContext.getCommandStackDepth() ).isEqualTo( 2 );
		assertThat( result3 ).isEqualTo( INCOMPLETE );

		// So far so, so good.
		// NEXT Add the next layer of commands
	}

}
