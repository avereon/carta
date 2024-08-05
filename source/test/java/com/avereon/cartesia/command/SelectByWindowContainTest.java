package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.CommandMap;
import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SelectByWindowContainTest extends CommandBaseTest {

	private final SelectByWindowContain command = new SelectByWindowContain();

	/**
	 * Select by window contain with no parameters or event, should prompt the
	 * user to select an anchor point. The result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( any() );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Select by window contain with no parameters, but an event, should submit a
	 * {@link Value} command to pass the anchor point back to this command. The
	 * result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithNoParametersAndEvent() throws Exception {
		// given
		CommandTrigger trigger = CommandMap.getTriggerByAction( "select-window-contain" );
		InputEvent event = createMouseEvent( trigger, 48, 17 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command );
		// Pretend the world anchor has been set
		when( commandContext.getWorldAnchor() ).thenReturn( new Point3D( -2, 1, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Value.class ), eq( new Point3D( -2, 1, 0 ) ) );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Select by window contain with one parameter should set the anchor. The
	 * result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithOneParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-1,1" );
		when( tool.worldToScreen( eq( new Point3D( -1, 1, 0 ) ) ) ).thenReturn( new Point3D( 72, 144, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).setWorldAnchor( eq( new Point3D( -1, 1, 0 ) ) );
		verify( commandContext, times( 1 ) ).setScreenAnchor( eq( new Point3D( 72, 144, 0 ) ) );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testExecuteWithOneParameterAndEvent() throws Exception {
		// given
		CommandTrigger trigger = CommandMap.getTriggerByAction( "select-window-contain" );
		InputEvent event = createMouseEvent( trigger, 48, 17 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command, new Point3D( -3, 3, 0 ) );
		when( tool.screenToWorld( eq( new Point3D( 48, 17, 0 ) ) ) ).thenReturn( new Point3D( 3, -3, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).worldWindowSelect( eq( new Point3D( -3, 3, 0 ) ), eq( new Point3D( 3, -3, 0 ) ), eq( false ), eq( false ) );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	/**
	 * Select by window contain with one parameter should set the anchor. The
	 * result should be incomplete.
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
		verify( tool, times( 1 ) ).worldWindowSelect( eq( new Point3D( -3, 3, 0 ) ), eq( new Point3D( 3, -3, 0 ) ), eq( false ), eq( false ) );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
