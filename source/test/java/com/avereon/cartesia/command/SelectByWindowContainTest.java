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

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SelectByWindowContainTest extends CommandBaseTest {

	private final SelectByWindowContain command = new SelectByWindowContain();

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
	void testExecuteWithNoParametersAndEvent() throws Exception {
		// given
		CommandTrigger trigger = CommandMap.getTriggerByAction( "select-window-contain" );
		InputEvent event = createMouseEvent( MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY, false, false, false, false, true, 48, 17 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
		// Pretend the world anchor has been set
		when( commandContext.getWorldAnchor() ).thenReturn( new Point3D( -2, 1, 0 ) );

		// when
		Object result = command.execute( task );

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Value.class ), eq( new Point3D( -2, 1, 0 ) ) );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testExecuteWithOneParameter() throws Exception {
		// NEXT Ensure this test is correct
		// Select by point with one parameter should set the anchor

		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-1,1" );

		// when
		Object result = command.execute( task );

		// then
		//verify( tool, times( 1 ) ).worldPointSelect( eq( new Point3D( -1, 1, 0 ) ), eq( false ) );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testExecuteWithOneParameterAndEvent() throws Exception {
		// NEXT Finish this test
		// given
		CommandTrigger trigger = CommandMap.getTriggerByAction( "select-window-contain" );
		InputEvent event = createMouseEvent( MouseEvent.MOUSE_RELEASED, MouseButton.PRIMARY, false, false, false, false, true, 48, 17 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
//		// Pretend the world anchor has been set
//		when( commandContext.getWorldAnchor() ).thenReturn( new Point3D( -2, 1, 0 ) );
//
//		// when
//		Object result = command.execute( task );
//
//		// then
//		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Value.class ), eq( new Point3D( -2, 1, 0 ) ) );
//		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	}
