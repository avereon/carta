package com.avereon.cartesia;

import com.avereon.cartesia.command.Anchor;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.Select;
import com.avereon.cartesia.command.SelectByWindow;
import com.avereon.cartesia.command.camera.CameraMove;
import com.avereon.cartesia.command.camera.CameraZoom;
import com.avereon.cartesia.command.snap.SnapAuto;
import javafx.event.EventType;
import javafx.scene.input.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CommandMapUIT extends BaseCartesiaUiTest {

	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		CommandMap.load( getProgram() );
	}

	@ParameterizedTest
	@MethodSource( "provideCommandMetadataInputEventMatches" )
	void getCommandByEvent( CommandMetadata expected, InputEvent event ) {
		CommandMetadata actual = CommandMap.getCommandByEvent( event );
		assertThat( actual.getAction() ).isEqualTo( expected.getAction() );
		assertThat( actual.getName() ).isEqualTo( expected.getName() );
		assertThat( actual.getCommand() ).isEqualTo( expected.getCommand() );
		assertThat( actual.getShortcut() ).isEqualTo( expected.getShortcut() );
		assertThat( actual.getType() ).isEqualTo( expected.getType() );
	}

	private static Stream<Arguments> provideCommandMetadataInputEventMatches() {
		return Stream.of(
			// Anchor
			Arguments.of( createMetadata( "anchor", "anchor", Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, false, false, false ) ),
			Arguments.of( createMetadata( "anchor", "anchor", Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, true, false, false, false, false ) ),
			Arguments.of( createMetadata( "anchor", "anchor", Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, true, false, false, false ) ),
			Arguments.of( createMetadata( "anchor", "anchor", Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, true, false, false ) ),
			Arguments.of( createMetadata( "anchor", "anchor", Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, false, true, false ) ),
			Arguments.of( createMetadata( "anchor", "anchor", Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, false, false, true ) ),
			Arguments.of( createMetadata( "anchor", "anchor", Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, true, false, false, false, true ) ),
			Arguments.of( createMetadata( "anchor", "anchor", Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, true, false, false, true ) ),
			Arguments.of( createMetadata( "anchor", "anchor", Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, true, false, true ) ),
			Arguments.of( createMetadata( "anchor", "anchor", Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, false, true, true ) ),

			// Select
			Arguments.of( createMetadata( "select", "select", Select.class ), createMouseEvent( MouseEvent.MOUSE_RELEASED, MouseButton.PRIMARY, false, false, false, false, false ) ),
			Arguments.of( createMetadata( "select", "select", Select.class ), createMouseEvent( MouseEvent.MOUSE_RELEASED, MouseButton.PRIMARY, true, false, false, false, false ) ),
			Arguments.of( createMetadata( "select-window", "select-window", SelectByWindow.class ), createMouseEvent( MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY, false, false, false, false, true ) ),
			Arguments.of( createMetadata( "select-window", "select-window", SelectByWindow.class ), createMouseEvent( MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY, false, true, false, false, true ) ),

			// Auto Snap
			Arguments.of( createMetadata( "snap-auto-nearest", "snap-auto-nearest", SnapAuto.class ), createMouseEvent( MouseEvent.MOUSE_CLICKED, MouseButton.SECONDARY, false, false, false, false, false ) ),

			// Camera Move
			Arguments.of( createMetadata( "camera-move", "camera-move", CameraMove.class ), createMouseEvent( MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY, true, false, false, false, true ) ),

			// Camera Zoom
			Arguments.of( createMetadata( "camera-zoom", "camera-zoom", CameraZoom.class ), createScrollEvent( ScrollEvent.SCROLL, true, false, false, false, false, false ) ),
			Arguments.of( createMetadata( "camera-zoom", "camera-zoom", CameraZoom.class ), createZoomEvent( ZoomEvent.ZOOM, false, false, false, false, false, false ) )
		);
	}

	private static CommandMetadata createMetadata( String action, String name, Class<? extends Command> type ) {
		return new CommandMetadata( action, name, null, null, List.of(), type );
	}

	private static MouseEvent createMouseEvent( EventType<MouseEvent> type, MouseButton button, boolean control, boolean shift, boolean alt, boolean meta, boolean moved ) {
		boolean primary = button == MouseButton.PRIMARY;
		boolean secondary = button == MouseButton.SECONDARY;
		boolean middle = button == MouseButton.MIDDLE;
		return new MouseEvent( type, 0, 0, 0, 0, button, 0, shift, control, alt, meta, primary, middle, secondary, false, false, !moved, null );
	}

	private static ScrollEvent createScrollEvent( EventType<ScrollEvent> type, boolean control, boolean shift, boolean alt, boolean meta, boolean direct, boolean inertia ) {
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

	private static ZoomEvent createZoomEvent( EventType<ZoomEvent> type, boolean control, boolean shift, boolean alt, boolean meta, boolean direct, boolean inertia ) {
		return new ZoomEvent( type, 0, 0, 0, 0, shift, control, alt, meta, direct, inertia, 0, 0, null );
	}

}
