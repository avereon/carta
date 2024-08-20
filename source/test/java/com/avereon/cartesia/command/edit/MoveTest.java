package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLine;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class MoveTest extends CommandBaseTest {

	private final Move command = new Move();

	// Script Tests --------------------------------------------------------------

	/**
	 * Move should move the selected shapes. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParameters() throws Exception {
		// given
		DesignLine line = new DesignLine( 0, 0, 0, 10 );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1,2", "2,2" );
		// Selected shapes are required for this command
		when( tool.getSelectedShapes() ).thenReturn( List.of( line ) );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( SUCCESS );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( line.getOrigin() ).isEqualTo( new Point3D( 1, 0, 0 ) );
		assertThat( line.getPoint() ).isEqualTo( new Point3D( 1, 10, 0 ) );
	}

	// Interactive Tests ---------------------------------------------------------

	// Bad Parameter Tests -------------------------------------------------------

}
