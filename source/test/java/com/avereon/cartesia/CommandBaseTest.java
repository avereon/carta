package com.avereon.cartesia;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.tool.CommandPrompt;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.cartesia.tool.DesignContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.asset.Asset;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith( MockitoExtension.class )
public class CommandBaseTest extends BaseCartesiaUnitTest {

	@Mock
	protected DesignTool tool;

	@Mock
	protected Asset asset;

	@Mock
	protected Design design;

	@Mock
	protected DesignContext designContext;

	@Mock
	protected DesignCommandContext commandContext;

	@Mock
	protected CommandPrompt commandPrompt;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();

		lenient().when( asset.getModel() ).thenReturn( design );
		lenient().when( tool.getAsset() ).thenReturn( asset );
		lenient().when( tool.getDesign() ).thenReturn( design );
		lenient().when( tool.getDesignContext() ).thenReturn( designContext );
		lenient().when( tool.getCommandContext() ).thenReturn( commandContext );
		lenient().when( designContext.getProduct() ).thenReturn( product );
		lenient().when( designContext.getDesignCommandContext() ).thenReturn( commandContext );
		lenient().when( commandContext.getCommandPrompt() ).thenReturn( commandPrompt );
		lenient().when( commandContext.getTool() ).thenReturn( tool );
		lenient().when( commandContext.getProduct() ).thenReturn( product );
		lenient().when( commandContext.getProgram() ).thenReturn( program );
	}

	protected static CommandMetadata createMetadata( String action, String name, String command, Class<? extends Command> type ) {
		return new CommandMetadata( action, name, command, null, List.of(), type );
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

	@SuppressWarnings( "unchecked" )
	protected static ZoomEvent createZoomEvent( CommandTrigger trigger, double x, double y, double factor ) {
		ZoomEvent event = createZoomEvent( (EventType<ZoomEvent>)trigger.getEventType(),
			trigger.hasModifier( CommandTrigger.Modifier.CONTROL ),
			trigger.hasModifier( CommandTrigger.Modifier.SHIFT ),
			trigger.hasModifier( CommandTrigger.Modifier.ALT ),
			trigger.hasModifier( CommandTrigger.Modifier.META ),
			trigger.hasModifier( CommandTrigger.Modifier.DIRECT ),
			trigger.hasModifier( CommandTrigger.Modifier.INERTIA ),
			x,
			y,
			factor
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

	protected static MouseEvent createMouseEvent( Object source, EventTarget target,EventType<MouseEvent> type, MouseButton button, boolean control, boolean shift, boolean alt, boolean meta, boolean moved, double x, double y ) {
		return createMouseEvent( source, target, type, button, control, shift, alt, meta, moved, x, y, 0 );
	}

	protected static MouseEvent createMouseEvent(
		EventType<MouseEvent> type, MouseButton button, boolean control, boolean shift, boolean alt, boolean meta, boolean moved, double x, double y, int clicks
	) {
		boolean primary = button == MouseButton.PRIMARY;
		boolean secondary = button == MouseButton.SECONDARY;
		boolean middle = button == MouseButton.MIDDLE;
		return new MouseEvent( type, x, y, 0, 0, button, clicks, shift, control, alt, meta, primary, middle, secondary, false, false, !moved, null );
	}

	protected static MouseEvent createMouseEvent(
		Object source, EventTarget target, EventType<MouseEvent> type, MouseButton button, boolean control, boolean shift, boolean alt, boolean meta, boolean moved, double x, double y, int clicks
	) {
		boolean primary = button == MouseButton.PRIMARY;
		boolean secondary = button == MouseButton.SECONDARY;
		boolean middle = button == MouseButton.MIDDLE;
		return new MouseEvent( source, target, type, x, y, 0, 0, button, clicks, shift, control, alt, meta, primary, middle, secondary, false, false, !moved, null );
	}

	@SuppressWarnings( "unchecked" )
	protected static ScrollEvent createScrollEvent( CommandTrigger trigger, double x, double y, double deltaX, double deltaY ) {
		ScrollEvent event = createScrollEvent( (EventType<ScrollEvent>)trigger.getEventType(),
			trigger.hasModifier( CommandTrigger.Modifier.CONTROL ),
			trigger.hasModifier( CommandTrigger.Modifier.SHIFT ),
			trigger.hasModifier( CommandTrigger.Modifier.ALT ),
			trigger.hasModifier( CommandTrigger.Modifier.META ),
			trigger.hasModifier( CommandTrigger.Modifier.DIRECT ),
			trigger.hasModifier( CommandTrigger.Modifier.INERTIA ),
			x,
			y,
			deltaX,
			deltaY
		);
		assertThat( CommandTrigger.from( event ) ).isEqualTo( trigger );
		return event;
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

	protected static ScrollEvent createScrollEvent(
		EventType<ScrollEvent> type, boolean control, boolean shift, boolean alt, boolean meta, boolean direct, boolean inertia, double x, double y, double deltaX, double deltaY
	) {
		return new ScrollEvent( type,
			x,
			y,
			x,
			y,
			shift,
			control,
			alt,
			meta,
			direct,
			inertia,
			deltaX,
			deltaY,
			deltaX,
			deltaY,
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

	protected static ZoomEvent createZoomEvent(
		EventType<ZoomEvent> type, boolean control, boolean shift, boolean alt, boolean meta, boolean direct, boolean inertia, double x, double y, double factor
	) {
		return new ZoomEvent( type, x, y, x, y, shift, control, alt, meta, direct, inertia, factor, factor, null );
	}

}
