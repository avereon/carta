package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CameraZoomInTest extends BaseCommandTest {

	private final Command command = new CameraZoomIn();

	@Test
	void execute() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		when( tool.getViewCenter() ).thenReturn( new Point3D( 1, 2, 3 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).zoom( eq( new Point3D( 1, 2, 3 ) ), eq( BaseDesignTool.ZOOM_IN_FACTOR ) );
		assertThat( result ).isEqualTo( Command.Result.SUCCESS );
	}

}
