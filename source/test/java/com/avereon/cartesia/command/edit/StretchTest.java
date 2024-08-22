package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.test.Point3DAssert;
import javafx.geometry.Point3D;
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

public class StretchTest extends CommandBaseTest {

	private final Stretch command = new Stretch();

	// Script Tests --------------------------------------------------------------

	/**
	 * Stretch should stretch the selected shapes. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParameters() throws Exception {
		// given
		DesignLine line1 = new DesignLine( 1, 0, 2, 2 );
		DesignLine line2 = new DesignLine( 3, 0, 2, 2 );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "2,0", "2,2", "2,1" );
		// Selected shapes are required for this command
		when( tool.getSelectedShapes() ).thenReturn( List.of( line1, line2 ) );

		// when
		Object result = task.runTaskStep();

		// then
		Point3DAssert.assertThat( line1.getOrigin() ).isCloseTo( new Point3D( 1, 0, 0 ) );
		Point3DAssert.assertThat( line1.getPoint() ).isCloseTo( new Point3D( 2, 1, 0 ) );
		Point3DAssert.assertThat( line2.getOrigin() ).isCloseTo( new Point3D( 3, 0, 0 ) );
		Point3DAssert.assertThat( line2.getPoint() ).isCloseTo( new Point3D( 2, 1, 0 ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	// Interactive Tests ---------------------------------------------------------

	@Test
	void testExecuteWithNoSelectedShapes() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void testExecuteWithSelectedShapesAndNoParameters() throws Exception {
		// given
		DesignLine line1 = new DesignLine( 1, 0, 2, 2 );
		DesignLine line2 = new DesignLine( 3, 0, 2, 2 );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Selected shapes are required for this command
		when( tool.getSelectedShapes() ).thenReturn( List.of( line1, line2 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( RETICLE );
		assertThat( command.getReference().stream().findFirst().orElse( null ) ).isInstanceOf( DesignLine.class );
		assertThat( command.getReference() ).hasSize( 1 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testExecuteWithSelectedShapesAndOneParameter() throws Exception {
		// given
		DesignLine line1 = new DesignLine( 1, 0, 2, 2 );
		DesignLine line2 = new DesignLine( 3, 0, 2, 2 );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "2,0" );
		// Selected shapes are required for this command
		when( tool.getSelectedShapes() ).thenReturn( List.of( line1, line2 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( RETICLE );
		assertThat( command.getReference().getFirst() ).isInstanceOf( DesignLine.class );
		assertThat( command.getReference() ).hasSize( 1 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testExecuteWithSelectedShapesAndTwoParameter() throws Exception {
		// given
		DesignLine line1 = new DesignLine( 1, 0, 2, 2 );
		DesignLine line2 = new DesignLine( 3, 0, 2, 2 );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "2,0", "2,2" );
		// Selected shapes are required for this command
		when( tool.getSelectedShapes() ).thenReturn( List.of( line1, line2 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( RETICLE );
		assertThat( command.getReference().getFirst() ).isInstanceOf( DesignLine.class );
		assertThat( command.getReference() ).hasSize( 1 );
		assertThat( command.getPreview() ).allSatisfy( shape -> assertThat( shape ).isInstanceOf( DesignLine.class ) );
		assertThat( command.getPreview() ).hasSize( 2 );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	// Bad Parameter Tests -------------------------------------------------------

	@ParameterizedTest
	@MethodSource( "provideParametersForTestWithParameters" )
	void testRunTaskStepWithBadParameters( Object[] parameters, String rbKey ) {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, parameters );
		when( tool.getSelectedShapes() ).thenReturn( List.of( new DesignLine() ) );

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
			Arguments.of( new Object[]{ BAD_POINT_PARAMETER }, "anchor" ),
			Arguments.of( new Object[]{ "2,0", BAD_POINT_PARAMETER }, "reference" ),
			Arguments.of( new Object[]{ "2,0", "2,2", BAD_POINT_PARAMETER }, "target" )
		);
	}

	@Test
	void testExecuteWithBadParameterThreeIsIgnored() throws Exception {
		// given
		DesignLine line1 = new DesignLine( 1, 0, 2, 2 );
		DesignLine line2 = new DesignLine( 3, 0, 2, 2 );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "2,0", "2,2", "2,1", BAD_PARAMETER );
		// Selected shapes are required for this command
		when( tool.getSelectedShapes() ).thenReturn( List.of( line1, line2 ) );

		// when
		Object result = task.runTaskStep();

		// then
		Point3DAssert.assertThat( line1.getOrigin() ).isCloseTo( new Point3D( 1, 0, 0 ) );
		Point3DAssert.assertThat( line1.getPoint() ).isCloseTo( new Point3D( 2, 1, 0 ) );
		Point3DAssert.assertThat( line2.getOrigin() ).isCloseTo( new Point3D( 3, 0, 0 ) );
		Point3DAssert.assertThat( line2.getPoint() ).isCloseTo( new Point3D( 2, 1, 0 ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
