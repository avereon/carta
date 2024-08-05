package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandTask;
import javafx.scene.Cursor;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.util.List;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ShapeInformationTest extends CommandBaseTest {

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
	void testExecuteWithOneParameter() throws Exception {
		// Select by point with one parameter should cause information to be displayed

		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1,1" );
		// Pretend there are shapes selected
		when( tool.getSelectedShapes() ).thenReturn( List.of( new DesignLine( -1, -1, 1, 1 ) ) );
		// Pretend the context is interactive
		when( commandContext.isInteractive() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( command, times( 1 ) ).clearReferenceAndPreviewWhenComplete();
		verify( noticeManager, times( 1 ) ).addNotice( any() );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
