package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.tool.view.DesignPaneMarea;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CameraZoomInTest extends CommandBaseTest {

	private final Command command = new CameraZoomIn();

	@Test
	void execute() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		when( tool.getViewpoint() ).thenReturn( new Point3D( 1, 2, 3 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).zoom( eq( new Point3D( 1, 2, 3 ) ), eq( DesignPaneMarea.ZOOM_IN_FACTOR ) );
		assertThat( result ).isEqualTo( Command.Result.SUCCESS );
	}

}
