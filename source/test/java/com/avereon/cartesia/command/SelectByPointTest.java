package com.avereon.cartesia.command;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.base.Prompt;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SelectByPointTest extends BaseCommandTest {

	private final Command command = new SelectByPoint();

	@Test
	void testExecuteWithNoParameters() throws Exception {
		// Select by point with no parameters prompts the user to select a point

		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

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
		when( commandContext.isSelectMode() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).worldPointSelect( eq( new Point3D( 1, 1, 0 ) ), eq( false ) );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void testExecuteWithOneParameterAndCommandStack() throws Exception {
		// Select by point with one parameter, and commands on the command stack, should return a world point

		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1,1" );
		// Pretend there is another command on the stack
		when( commandContext.isSelectMode() ).thenReturn( false );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 0 ) ).screenPointSelect( any(), anyBoolean() );
		assertThat( result ).isEqualTo( new Point3D( 1, 1, 0 ) );
	}

	@Test
	void testExecuteWithEvent() throws Exception {
		// Select by point with event should cause select to be called

		// given
		CommandTrigger trigger = getMod().getCommandMap().getTriggerByAction( "select-point" );
		InputEvent event = createMouseEvent( trigger, 48, 17 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
		when( commandContext.isSelectMode() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).screenPointSelect( eq( new Point3D( 48, 17, 0 ) ), eq( false ) );
		assertThat( event.isConsumed() ).isTrue();
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void testExecuteWithEventAndCommandStack() throws Exception {
		// Select by point with event, and commands on the command stack, should return a world point

		// given
		CommandTrigger trigger = getMod().getCommandMap().getTriggerByAction( "select-point" );
		InputEvent event = createMouseEvent( trigger, 48, 17 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
		// Pretend there is another command on the stack
		when( commandContext.isSelectMode() ).thenReturn( false );
		when( tool.screenToWorkplane( new Point3D( 48, 17, 0 ) ) ).thenReturn( new Point3D( 1, 1, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).screenToWorkplane( eq( new Point3D( 48, 17, 0 ) ) );
		verify( tool, times( 0 ) ).screenPointSelect( any(), anyBoolean() );
		verify( tool, times( 0 ) ).screenToWorld( any( Point3D.class ) );
		assertThat( event.isConsumed() ).isTrue();
		assertThat( result ).isEqualTo( new Point3D( 1, 1, 0 ) );
	}

	@Test
	void testExecuteWithBadParameter() {
		// Select by point with one parameter should cause select to be called

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
