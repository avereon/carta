package com.avereon.cartesia;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.tool.CommandPrompt;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.cartesia.tool.DesignContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.event.EventType;
import javafx.scene.input.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.lenient;

@ExtendWith( MockitoExtension.class )
public class CommandBaseTest extends BaseCartesiaUnitTest {

	@Mock
	protected DesignTool tool;

	@Mock
	protected DesignContext designContext;

	@Mock
	protected DesignCommandContext commandContext;

	@Mock
	protected CommandPrompt commandPrompt;

	@BeforeEach
	protected void setup() {
		lenient().when( tool.getDesignContext() ).thenReturn( designContext );
		lenient().when( tool.getCommandContext() ).thenReturn( commandContext );
		lenient().when( designContext.getDesignCommandContext() ).thenReturn( commandContext );
		lenient().when( commandContext.getCommandPrompt() ).thenReturn( commandPrompt );
		lenient().when( commandContext.getTool() ).thenReturn( tool );
	}

	protected static CommandMetadata createMetadata( String action, String name, Class<? extends Command> type ) {
		return new CommandMetadata( action, name, null, null, List.of(), type );
	}

	protected static MouseEvent createMouseEvent( EventType<MouseEvent> type, MouseButton button, boolean control, boolean shift, boolean alt, boolean meta, boolean moved ) {
		return createMouseEvent( type, button, control, shift, alt, meta, moved, 0, 0, 0 );
	}

	protected static MouseEvent createMouseEvent( EventType<MouseEvent> type, MouseButton button, boolean control, boolean shift, boolean alt, boolean meta, boolean moved, double x, double y ) {
		return createMouseEvent( type, button, control, shift, alt, meta, moved, x, y, 0 );
	}

	protected static MouseEvent createMouseEvent(
		EventType<MouseEvent> type, MouseButton button, boolean control, boolean shift, boolean alt, boolean meta, boolean moved, double x, double y, int clicks
	) {
		boolean primary = button == MouseButton.PRIMARY;
		boolean secondary = button == MouseButton.SECONDARY;
		boolean middle = button == MouseButton.MIDDLE;
		return new MouseEvent( type, x, y, 0, 0, button, clicks, shift, control, alt, meta, primary, middle, secondary, false, false, !moved, null );
	}

	protected static ScrollEvent createScrollEvent( EventType<ScrollEvent> type, boolean control, boolean shift, boolean alt, boolean meta, boolean direct, boolean inertia ) {
		return new ScrollEvent( type,
			0,
			0,
			0,
			0,
			shift,
			control,
			alt,
			meta,
			direct,
			inertia,
			0,
			0,
			0,
			0,
			ScrollEvent.HorizontalTextScrollUnits.CHARACTERS,
			0,
			ScrollEvent.VerticalTextScrollUnits.LINES,
			0,
			2,
			null
		);
	}

	protected static ZoomEvent createZoomEvent( EventType<ZoomEvent> type, boolean control, boolean shift, boolean alt, boolean meta, boolean direct, boolean inertia ) {
		return new ZoomEvent( type, 0, 0, 0, 0, shift, control, alt, meta, direct, inertia, 0, 0, null );
	}

}
