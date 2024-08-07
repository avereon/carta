package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.snap.SnapNearestCp;
import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.avereon.cartesia.command.Command.Result.INVALID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class AutoSnapNearestTest extends CommandBaseTest {

	private final AutoSnap command = new AutoSnap();

	@Test
	void testRunTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( INVALID );
	}

	@Test
	void testRunTaskStepWithSnapOnly() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, new SnapNearestCp() );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( INVALID );
	}

	@Test
	void testRunTaskStepSnapAndEvent() throws Exception {
		// given
		CommandTrigger trigger = getMod().getCommandMap().getTriggerByAction( "snap-auto-nearest" );
		InputEvent event = createMouseEvent( trigger, 48, 17 );
		CommandTask task = new CommandTask( commandContext, tool, trigger, event, command, new SnapNearestCp() );
		when( tool.screenToWorld( eq( new Point3D( 48, 17, 0 ) ) ) ).thenReturn( new Point3D( 0.75, 0.75, 0 ) );
		when( tool.getVisibleShapes() ).thenReturn( List.of() );
		when( tool.nearestReferencePoint( anyList(), eq( new Point3D( 0.75, 0.75, 0 ) ) ) ).thenReturn( new Point3D( 1, 1, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( new Point3D( 1, 1, 0 ) );
	}

	@Test
	void testRunTaskStepSnapAndParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, new SnapNearestCp(), "3/4,3/4" );
		when( tool.getVisibleShapes() ).thenReturn( List.of() );
		when( tool.nearestReferencePoint( anyList(), eq( new Point3D( 0.75, 0.75, 0 ) ) ) ).thenReturn( new Point3D( 1, 1, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( new Point3D( 1, 1, 0 ) );
	}

}
