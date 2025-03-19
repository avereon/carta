package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.base.Prompt;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.test.Point3DAssert;
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

public class JoinTest extends BaseCommandTest {

	private final Join command = new Join();

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
		DesignLine trim = new DesignLine( -2, 0, -1, 1 );
		DesignLine edge = new DesignLine( 2, 0, 1, 1 );
		layer.addShape( trim );
		layer.addShape( edge );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-1.25,0.75", "1.25,0.75" );

		when( tool.worldPointSyncSelect( eq( new Point3D( -1.25, 0.75, 0 ) ) ) ).thenReturn( List.of( trim ) );
		when( tool.worldPointSyncFindOne( eq( new Point3D( 1.25, 0.75, 0 ) ) ) ).thenReturn( List.of( edge ) );

		// The mouse clicks
		when( tool.worldToScreen( eq( new Point3D( -1.25, 0.75, 0 ) ) ) ).thenReturn( new Point3D( -12.5, 7.5, 0 ) );
		when( tool.worldToScreen( eq( new Point3D( 1.25, 0.75, 0 ) ) ) ).thenReturn( new Point3D( 12.5, 7.5, 0 ) );

		// The shape endpoints
		when( tool.worldToScreen( eq( new Point3D( -2, 0, 0 ) ) ) ).thenReturn( new Point3D( -20, 0, 0 ) );
		when( tool.worldToScreen( eq( new Point3D( -1, 1, 0 ) ) ) ).thenReturn( new Point3D( -10, 10, 0 ) );
		when( tool.worldToScreen( eq( new Point3D( 2, 0, 0 ) ) ) ).thenReturn( new Point3D( 20, 0, 0 ) );
		when( tool.worldToScreen( eq( new Point3D( 1, 1, 0 ) ) ) ).thenReturn( new Point3D( 10, 10, 0 ) );

		// The intersection point
		when( tool.worldToScreen( eq( new Point3D( 0,2, 0 ) ) ) ).thenReturn( new Point3D( 0, 20, 0 ) );

		// when
		Object result = task.runTaskStep();

		Point3DAssert.assertThat( trim.getOrigin() ).isEqualTo( new Point3D( -2, 0, 0 ) );
		Point3DAssert.assertThat( trim.getPoint() ).isEqualTo( new Point3D( 0, 2, 0 ) );
		Point3DAssert.assertThat( edge.getOrigin() ).isEqualTo( new Point3D( 2, 0, 0 ) );
		Point3DAssert.assertThat( edge.getPoint() ).isEqualTo( new Point3D( 0, 2, 0 ) );
		assertThat( result ).isEqualTo( SUCCESS );
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
		verify( tool, times( 1 ) ).setCursor( Cursor.HAND );
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
		return Stream.of(
			Arguments.of( new String[]{ "bad parameter" }, "select-meet-shape" ),
			Arguments.of( new String[]{ "1,1", "bad parameter" }, "select-meet-shape" )
		);
	}

	@Test
	void testExecuteWithBadParameterThreeIsIgnored() throws Exception {
		DesignLayer layer = new DesignLayer();
		DesignLine trim = new DesignLine( -2, 0, -1, 1 );
		DesignLine edge = new DesignLine( 2, 0, 1, 1 );
		layer.addShape( trim );
		layer.addShape( edge );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "-1.25,0.75", "1.25,0.75", BAD_PARAMETER );

		when( tool.worldPointSyncSelect( eq( new Point3D( -1.25, 0.75, 0 ) ) ) ).thenReturn( List.of( trim ) );
		when( tool.worldPointSyncFindOne( eq( new Point3D( 1.25, 0.75, 0 ) ) ) ).thenReturn( List.of( edge ) );

		// The mouse clicks
		when( tool.worldToScreen( eq( new Point3D( -1.25, 0.75, 0 ) ) ) ).thenReturn( new Point3D( -12.5, 7.5, 0 ) );
		when( tool.worldToScreen( eq( new Point3D( 1.25, 0.75, 0 ) ) ) ).thenReturn( new Point3D( 12.5, 7.5, 0 ) );

		// The shape endpoints
		when( tool.worldToScreen( eq( new Point3D( -2, 0, 0 ) ) ) ).thenReturn( new Point3D( -20, 0, 0 ) );
		when( tool.worldToScreen( eq( new Point3D( -1, 1, 0 ) ) ) ).thenReturn( new Point3D( -10, 10, 0 ) );
		when( tool.worldToScreen( eq( new Point3D( 2, 0, 0 ) ) ) ).thenReturn( new Point3D( 20, 0, 0 ) );
		when( tool.worldToScreen( eq( new Point3D( 1, 1, 0 ) ) ) ).thenReturn( new Point3D( 10, 10, 0 ) );

		// The intersection point
		when( tool.worldToScreen( eq( new Point3D( 0,2, 0 ) ) ) ).thenReturn( new Point3D( 0, 20, 0 ) );

		// when
		Object result = task.runTaskStep();

		Point3DAssert.assertThat( trim.getOrigin() ).isEqualTo( new Point3D( -2, 0, 0 ) );
		Point3DAssert.assertThat( trim.getPoint() ).isEqualTo( new Point3D( 0, 2, 0 ) );
		Point3DAssert.assertThat( edge.getOrigin() ).isEqualTo( new Point3D( 2, 0, 0 ) );
		Point3DAssert.assertThat( edge.getPoint() ).isEqualTo( new Point3D( 0, 2, 0 ) );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
