package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.test.Point3DAssert;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class RotateTest extends CommandBaseTest {

	private final Rotate command = new Rotate();

	// Script Tests --------------------------------------------------------------

	/**
	 * Rotate should rotate the selected shapes. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParametersByAngle() throws Exception {
		// given
		DesignLine line = new DesignLine( 0, 0, 0, 10 );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "0,0", "-90" );
		// Selected shapes are required for this command
		when( tool.getSelectedShapes() ).thenReturn( List.of( line ) );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( SUCCESS );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		Point3DAssert.assertThat( line.getOrigin() ).isCloseTo( new Point3D( 0, 0, 0 ) );
		Point3DAssert.assertThat( line.getPoint() ).isCloseTo( new Point3D( 10, 0, 0 ) );
	}

	/**
	 * Rotate should rotate the selected shapes. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParametersByPoints() throws Exception {
		// given
		DesignLine line = new DesignLine( 0, 0, 0, 10 );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "0,0", "0,2", "2,0" );
		// Selected shapes are required for this command
		when( tool.getSelectedShapes() ).thenReturn( List.of( line ) );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( SUCCESS );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		Point3DAssert.assertThat( line.getOrigin() ).isCloseTo( new Point3D( 0, 0, 0 ) );
		Point3DAssert.assertThat( line.getPoint() ).isCloseTo( new Point3D( 10, 0, 0 ) );
	}

}
