package com.avereon.cartesia.command.measure;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.base.Prompt;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MeasureLengthTest extends BaseCommandTest {

	private final MeasureLength command = new MeasureLength();

	// Script Tests --------------------------------------------------------------

	/**
	 * Measure length with all parameters should calculate the length of the
	 * selected shape and display it as a notice. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParameters() throws Exception {
		// given
		DesignLine line = new DesignLine( new Point3D( -2, -2, 0 ), new Point3D( 2, 2, 0 ) );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1,1" );
		when( commandContext.isInteractive() ).thenReturn( true );
		when( tool.worldPointSyncFindOne( new Point3D( 1, 1, 0 ) ) ).thenReturn( List.of( line ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( noticeManager, times( 1 ) ).addNotice( any() );
		assertThat( result ).isEqualTo( 5.656854249492381 );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	// Interactive Tests ---------------------------------------------------------

	/**
	 * Measure length with no parameters or event, should prompt the
	 * user to select a shape. The result should be incomplete.
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
		return Stream.of( Arguments.of( new String[]{ "bad parameter" }, "select-shape" ) );
	}

	@Test
	void testExecuteWithBadParameterThreeIsIgnored() throws Exception {
		// given
		DesignLine line = new DesignLine( new Point3D( -2, -2, 0 ), new Point3D( 2, 2, 0 ) );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1,1", "bad parameter" );
		when( commandContext.isInteractive() ).thenReturn( true );
		when( tool.worldPointSyncFindOne( new Point3D( 1, 1, 0 ) ) ).thenReturn( List.of( line ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( noticeManager, times( 1 ) ).addNotice( any() );
		assertThat( result ).isEqualTo( 5.656854249492381 );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

}
