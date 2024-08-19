package com.avereon.cartesia.command.draw.layer;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.command.layer.LayerCreate;
import com.avereon.cartesia.data.DesignLayer;
import javafx.scene.Cursor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import java.util.stream.Stream;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class LayerCreateTest extends CommandBaseTest {

	private final LayerCreate command = new LayerCreate();

	@Mock
	private DesignLayer parent;

	// Script Tests --------------------------------------------------------------

	/**
	 * Layer create with all parameters should create and add a new layer to the
	 * design. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "Layer Name" );
		when( selectedLayer.getLayer() ).thenReturn( parent );
		when( parent.addLayerBeforeOrAfter( any( DesignLayer.class ), eq( selectedLayer ), eq( true ) ) ).thenAnswer( invocation -> invocation.getArgument( 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( parent, times( 1 ) ).addLayerBeforeOrAfter( any( DesignLayer.class ), eq( selectedLayer ), eq( true ) );
		verify( commandContext, times( 0 ) ).submit( eq( tool ), any( Prompt.class ) );
		assertThat( result ).isInstanceOf( DesignLayer.class );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	// Interactive Tests ---------------------------------------------------------

	/**
	 * Measure point with no parameters or event, should prompt the
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
		verify( tool, times( 1 ) ).setCursor( Cursor.TEXT );
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
		return Stream.of( Arguments.of( new String[]{ null }, "layer-name" ) );
	}

	@Test
	void testExecuteWithBadParameterTwoIsIgnored() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "Layer Name", "bad parameter" );
		when( selectedLayer.getLayer() ).thenReturn( parent );
		when( parent.addLayerBeforeOrAfter( any( DesignLayer.class ), eq( selectedLayer ), eq( true ) ) ).thenAnswer( invocation -> invocation.getArgument( 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( parent, times( 1 ) ).addLayerBeforeOrAfter( any( DesignLayer.class ), eq( selectedLayer ), eq( true ) );
		verify( commandContext, times( 0 ) ).submit( eq( tool ), any( Prompt.class ) );
		assertThat( result ).isInstanceOf( DesignLayer.class );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

}
