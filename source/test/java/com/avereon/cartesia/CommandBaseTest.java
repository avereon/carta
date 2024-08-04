package com.avereon.cartesia;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.tool.CommandPrompt;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.cartesia.tool.DesignContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.ActionLibrary;
import com.avereon.xenon.ActionProxy;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class )
public class CommandBaseTest extends BaseCartesiaUnitTest {

	static XenonProgramProduct product = Mockito.mock( XenonProgramProduct.class );

	static Xenon program = Mockito.mock( Xenon.class );

	static ActionLibrary actionLibrary = Mockito.mock( ActionLibrary.class );

	static ActionProxy other = Mockito.mock( ActionProxy.class );

	static Map<String, ActionProxy> mockActionMap = new HashMap<>();

	@Mock
	protected DesignTool tool;

	@Mock
	protected DesignContext designContext;

	@Mock
	protected DesignCommandContext commandContext;

	@Mock
	protected CommandPrompt commandPrompt;

	@BeforeAll
	public static void load() {
		List<String> actions = List.of( "anchor", "select-point", "select-toggle", "select-window-contain", "select-window-intersect", "snap-auto-nearest", "camera-move", "camera-zoom" );

		for( String command : actions ) {
			ActionProxy action = Mockito.mock( ActionProxy.class );
			when( action.getName() ).thenReturn( command );
			mockActionMap.put( command, action );
		}
		when( other.getName() ).thenReturn( "other" );

		when( product.getProgram() ).thenReturn( program );
		when( program.getActionLibrary() ).thenReturn( actionLibrary );
		when( actionLibrary.getAction( anyString() ) ).thenAnswer( i -> {
			String name = String.valueOf( i.getArguments()[ 0 ] );
			return mockActionMap.getOrDefault( name, other );
		} );

		// With this setup, the CommandMap contain mocks for all the tested actions

		CommandMap.load( product );
	}

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

	@SuppressWarnings( "unchecked" )
	protected static MouseEvent createMouseEvent( CommandTrigger trigger, double x, double y ) {
		MouseEvent event = createMouseEvent( (EventType<MouseEvent>)trigger.getEventType(),
			trigger.getButton(),
			trigger.hasModifier( CommandTrigger.Modifier.CONTROL ),
			trigger.hasModifier( CommandTrigger.Modifier.SHIFT ),
			trigger.hasModifier( CommandTrigger.Modifier.ALT ),
			trigger.hasModifier( CommandTrigger.Modifier.META ),
			trigger.hasModifier( CommandTrigger.Modifier.MOVED ),
			x,
			y
		);
		assertThat( CommandTrigger.from( event ) ).isEqualTo( trigger );
		return event;
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
