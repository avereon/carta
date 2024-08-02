package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.CommandMap;
import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SelectToggleTest extends CommandBaseTest {

	private final SelectToggle command = new SelectToggle();

	@Test
	void testExecuteWithNoParameters() throws Exception {
		// Select by point with no parameters prompts the user to select a point

		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = command.execute( task );

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( any() );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testExecuteWithOneParameter() throws Exception {
		// Select by point with one parameter should cause select to be called

		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1,1" );

		// when
		Object result = command.execute( task );

		// then
		verify( tool, times( 1 ) ).worldPointSelect( eq( new Point3D( 1, 1, 0 ) ), eq( true ) );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void testExecuteWithEvent() throws Exception {
		// Select by point with event should cause select to be called

		// given
		CommandTrigger trigger = CommandMap.getTriggerByAction( "select-point" );
		InputEvent event = createMouseEvent( MouseEvent.MOUSE_RELEASED, MouseButton.PRIMARY, false, false, false, false, false, 48, 17 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );

		// when
		Object result = command.execute( task );

		// then
		verify( tool, times( 1 ) ).screenPointSelect( eq( new Point3D( 48, 17, 0 ) ), eq( true ) );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void testExecuteWithBadParameter() throws Exception {
		// Select by point with one parameter should cause select to be called

		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "bad parameter" );

		// when
		Object result = command.execute( task );

		// then
		assertThat( result ).isEqualTo( FAILURE );
	}

}
