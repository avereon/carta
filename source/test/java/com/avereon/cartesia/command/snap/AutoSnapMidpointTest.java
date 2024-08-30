package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.snap.SnapMidpoint;
import com.avereon.cartesia.command.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AutoSnapMidpointTest extends BaseCommandTest {

	private final AutoSnap command = new AutoSnap();

	@Spy
	private final Snap snap = new SnapMidpoint();

	@Test
	void testRunTaskStepNoParameters() {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command /* no parameters */ );

		// when
		InvalidInputException exception = catchThrowableOfType( InvalidInputException.class, task::runTaskStep );

		// then
		verify( commandContext, times( 0 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( currentLayer, times( 0 ) ).addShape( any() );
		assertThat( exception.getInputRbKey() ).isEqualTo( "snap" );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	@Test
	void testRunTaskStepWithSnapOnly() {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, snap );

		// when
		InvalidInputException exception = catchThrowableOfType( InvalidInputException.class, task::runTaskStep );

		// then
		verify( commandContext, times( 0 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( currentLayer, times( 0 ) ).addShape( any() );
		assertThat( exception.getInputRbKey() ).isEqualTo( "snap" );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	@Test
	void testRunTaskStepSnapAndEvent() throws Exception {
		// given
		CommandTrigger trigger = getMod().getCommandMap().getTriggerByAction( "snap-auto-midpoint" );
		InputEvent event = createMouseEvent( trigger, 48, 17 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command, snap );
		when( tool.screenToWorld( eq( new Point3D( 48, 17, 0 ) ) ) ).thenReturn( new Point3D( 0.75, 0.75, 0 ) );
		when( tool.worldPointSyncFindOne( new Point3D( 0.75, 0.75, 0 ) ) ).thenReturn( List.of( new DesignLine( 0, 0, 2, 2 ) ) );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( new Point3D( 1, 1, 0 ) );
	}

	@Test
	void testRunTaskStepSnapAndParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, snap, "3/4,3/4" );
		when( tool.worldPointSyncFindOne( new Point3D( 0.75, 0.75, 0 ) ) ).thenReturn( List.of( new DesignLine( 0, 0, 2, 2 ) ) );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( new Point3D( 1, 1, 0 ) );
	}

}

