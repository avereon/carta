package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignLine;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SplitTest extends CommandBaseTest {

	private final Split command = new Split();

	// Script Tests --------------------------------------------------------------

	/**
	 * Join should ask the user for two shapes to join, by trimming the shapes to
	 * the nearest common intersection point. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParameters() throws Exception {
		DesignLayer layer = new DesignLayer();
		DesignLine line = new DesignLine( 1, 1, 4, 1 );
		layer.addShape( line );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "3,1", "2,1" );

		when( tool.worldPointSyncSelect( eq( new Point3D( 3, 1, 0 ) ) ) ).thenReturn( List.of( line ) );

		// The mouse clicks
		when( tool.worldToScreen( eq( new Point3D( 2, 1, 0 ) ) ) ).thenReturn( new Point3D( 20, 10, 0 ) );

		// when
		Object result = task.runTaskStep();

		assertSuccessfulSplit( result, layer, line );
	}

	// Interactive Tests ---------------------------------------------------------

	/**
	 * Trim with no parameters, should prompt the user to select a shape to trim.
	 * The result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testRunTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( Cursor.HAND );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	/**
	 * Trim with one parameter, should prompt the user to select a shape to use
	 * as the trim edge. The result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithOneParameter() throws Exception {
		// given
		DesignLine trim = new DesignLine( new Point3D( 2, 0, 0 ), new Point3D( 0, 2, 0 ) );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1,1" );
		when( tool.worldPointSyncSelect( eq( new Point3D( 1, 1, 0 ) ) ) ).thenReturn( List.of( trim ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( RETICLE );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	// Bad Parameter Tests -------------------------------------------------------

	@ParameterizedTest
	@MethodSource( "provideParametersForTestWithParameters" )
	void testRunTaskStepWithBadParameters( Object[] parameters, String rbKey ) {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, parameters );

		// when
		InvalidInputException exception = catchThrowableOfType( InvalidInputException.class, task::runTaskStep );

		// then
		verify( commandContext, times( 0 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( currentLayer, times( 0 ) ).addShape( any() );
		assertThat( exception.getInputRbKey() ).isEqualTo( rbKey );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	private static Stream<Arguments> provideParametersForTestWithParameters() {
		return Stream.of( Arguments.of( new String[]{ "bad parameter" }, "select-split-shape" ), Arguments.of( new String[]{ "1,1", "bad parameter" }, "select-split-point" ) );
	}

	@Test
	void testExecuteWithBadParameterThreeIsIgnored() throws Exception {
		DesignLayer layer = new DesignLayer();
		DesignLine line = new DesignLine( 1, 1, 4, 1 );
		layer.addShape( line );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "3,1", "2,1", BAD_PARAMETER );

		when( tool.worldPointSyncSelect( eq( new Point3D( 3, 1, 0 ) ) ) ).thenReturn( List.of( line ) );

		// The mouse clicks
		when( tool.worldToScreen( eq( new Point3D( 2, 1, 0 ) ) ) ).thenReturn( new Point3D( 20, 10, 0 ) );

		// when
		Object result = task.runTaskStep();

		assertSuccessfulSplit( result, layer, line );
	}

	private void assertSuccessfulSplit( Object result, DesignLayer layer, DesignLine line ) {
		// then
		assertThat( result ).isEqualTo( SUCCESS );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );

		// Ensure the old line has been removed from the layer
		assertThat( layer.getShapes() ).doesNotContain( line );
		assertThat( layer.getShapes() ).hasSize( 2 );

		// Ensure the new lines are in the layer
		assertThat( layer.getShapes() ).matches( shapes -> {
			for( var shape : shapes ) {
				if( shape instanceof DesignLine line1 ) {
					if( line1.getOrigin().equals( new Point3D( 1, 1, 0 ) ) && line1.getPoint().equals( new Point3D( 2, 1, 0 ) ) ) return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		});
		assertThat( layer.getShapes() ).matches( shapes -> {
			for( var shape : shapes ) {
				if( shape instanceof DesignLine line1 ) {
					if( line1.getOrigin().equals( new Point3D( 2, 1, 0 ) ) && line1.getPoint().equals( new Point3D( 4, 1, 0 ) ) ) return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		});
	}

}
