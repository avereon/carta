package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.test.Point3DAssert;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class MirrorTest extends CommandBaseTest {

	private final Mirror command = new Mirror();

	// Script Tests --------------------------------------------------------------

	/**
	 * Mirror should mirror the selected shapes. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParameters() throws Exception {
		// given
		DesignLine line = new DesignLine( 0, 0, 0, 10 );
		DesignLayer layer = new DesignLayer();
		layer.addShape( line );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1,1", "2,2" );
		// Selected shapes are required for this command
		when( tool.getSelectedShapes() ).thenReturn( List.of( line ) );

		// when
		Object result = task.runTaskStep();

		// then
		assertSuccessfulMirror( result, layer, line );
	}

	private void assertSuccessfulMirror( Object result, DesignLayer layer, DesignLine line ) {
		// then
		assertThat( result ).isEqualTo( SUCCESS );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );

		// The original line should not move
		Point3DAssert.assertThat( line.getOrigin() ).isCloseTo( new Point3D( 0, 0, 0 ) );
		Point3DAssert.assertThat( line.getPoint() ).isCloseTo( new Point3D( 0, 10, 0 ) );

		// But there should be a second line that is in the new location
		DesignLine newLine = (DesignLine)layer.getShapes().stream().filter( s -> s != line ).findFirst().orElse( null );
		assertThat( newLine ).isNotNull();
		Point3DAssert.assertThat( newLine.getOrigin() ).isCloseTo( new Point3D( 0, 0, 0 ) );
		Point3DAssert.assertThat( newLine.getPoint() ).isCloseTo( new Point3D( 10, 0, 0 ) );
	}

}

