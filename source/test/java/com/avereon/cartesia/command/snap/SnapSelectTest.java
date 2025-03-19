package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.base.Prompt;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.snap.SnapMidpoint;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SnapSelectTest extends BaseCommandTest {

	private final SnapSelect command = new SnapSelect();

	private static final Snap snap = new SnapMidpoint();

	// Script Tests --------------------------------------------------------------

	/**
	 * Draw line with two parameters should set both the origin
	 * and the point, and then add the line to the current layer. The
	 * result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParameters() throws Exception {
		// given
		DesignLine line = new DesignLine( -1, -1, 1, -1 );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, snap, "0.5,-1" );
		when( tool.worldPointSyncFindOne( eq( new Point3D( 0.5, -1, 0 ) ) ) ).thenReturn( List.of( line ) );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( new Point3D( 0, -1, 0 ) );
	}

	// Interactive Tests ---------------------------------------------------------

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
	void testRunTaskStepWithSnapOnly() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, snap );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
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
			Arguments.of( new Object[]{ snap, BAD_POINT_PARAMETER }, "select-snap-shape" )
		);
	}

	@Test
	void testExecuteWithBadParameterThreeIsIgnored() throws Exception {
		// given
		DesignLine line = new DesignLine( -1, -1, 1, -1 );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, snap, "0.5,-1", BAD_PARAMETER );
		when( tool.worldPointSyncFindOne( eq( new Point3D( 0.5, -1, 0 ) ) ) ).thenReturn( List.of( line ) );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( new Point3D( 0, -1, 0 ) );
	}

}
