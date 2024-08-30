package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.command.CommandTask;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CameraZoomSelectedTest extends BaseCommandTest {

	private final Command command = new CameraZoomSelected();

	@Test
	void execute() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		when( tool.getSelectedShapes() ).thenReturn( List.of( new DesignLine( -1, -1, 1, 1 ) ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setScreenViewport( any() );
		assertThat( result ).isEqualTo( Command.Result.SUCCESS );
	}

}
