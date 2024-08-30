package com.avereon.cartesia.command;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.data.DesignLine;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.util.List;

import static com.avereon.cartesia.command.Command.Result.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ShapeInformationTest extends BaseCommandTest {

	@Spy
	private final ShapeInformation command = new ShapeInformation();

	@Test
	void executeWithNothing() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( eq( Cursor.HAND ) );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testExecuteWithSelectedShapes() throws Exception {
		// Select by point with selected shapes should cause information to be displayed

		// given
		DesignLine line = new DesignLine( 0, 0, 2, 2 );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Pretend there are shapes selected
		when( tool.getSelectedShapes() ).thenReturn( List.of( line ) );
		// Pretend the context is interactive
		when( commandContext.isInteractive() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( command, times( 1 ) ).clearReferenceAndPreviewWhenComplete();
		verify( noticeManager, times( 1 ) ).addNotice( any() );
		assertThat( result ).isEqualTo(  line );
	}

	@Test
	void testExecuteWithPointParameter() throws Exception {
		// Select by point with one parameter should cause information to be displayed

		// given
		DesignLine line = new DesignLine( 0, 0, 2, 2 );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1,1" );
		// Pretend there are shapes selected
		when( tool.worldPointSyncSelect( eq( new Point3D( 1, 1, 0 ) ) ) ).thenReturn( List.of( line ) );
		// Pretend the context is interactive
		when( commandContext.isInteractive() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( command, times( 1 ) ).clearReferenceAndPreviewWhenComplete();
		verify( noticeManager, times( 1 ) ).addNotice( any() );
		assertThat( result ).isEqualTo( line );
	}

	@Test
	void testExecuteWithBadParameter() throws Exception {
		// Select by point with one parameter should cause information to be displayed

		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "bad parameter" );

		// when
		InvalidInputException exception = catchThrowableOfType( InvalidInputException.class, task::runTaskStep );

		// then
		verify( commandContext, times( 0 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( currentLayer, times( 0 ) ).addShape( any() );
		assertThat( exception.getInputRbKey() ).isEqualTo( "select-shape" );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

}
