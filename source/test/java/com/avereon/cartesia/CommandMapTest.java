package com.avereon.cartesia;

import com.avereon.cartesia.command.*;
import com.avereon.cartesia.command.camera.CameraMove;
import com.avereon.cartesia.command.camera.CameraZoom;
import com.avereon.cartesia.command.snap.SnapAuto;
import javafx.scene.input.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandMapTest extends CommandMapBaseTest {

	@Test
	void getCommandByShortcut() {
		// FIXME Why does this return Noop?
		//assertThat( CommandMap.getCommandByShortcut( "zw" ).getType()).isEqualTo( CameraZoomWindow.class );
	}

	@Test
	void getCommandByAction() {
		assertThat( CommandMap.getCommandByAction( "anchor" ).getType()).isEqualTo( Anchor.class );
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
			Arguments.of( createMetadata( "select-point", "select-point", SelectByPoint.class ), createMouseEvent( MouseEvent.MOUSE_RELEASED, MouseButton.PRIMARY, false, false, false, false, false ) ),
			Arguments.of( createMetadata( "select-toggle", "select-toggle", SelectToggle.class ), createMouseEvent( MouseEvent.MOUSE_RELEASED, MouseButton.PRIMARY, true, false, false, false, false ) ),
			Arguments.of( createMetadata( "select-window-contain", "select-window-contain", SelectByWindowContain.class ), createMouseEvent( MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY, false, false, false, false, true ) ),
			Arguments.of( createMetadata( "select-window-intersect", "select-window-intersect", SelectByWindowIntersect.class ), createMouseEvent( MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY, false, true, false, false, true ) ),

			// Auto Snap
			Arguments.of( createMetadata( "snap-auto-nearest", "snap-auto-nearest", SnapAuto.class ),
				createMouseEvent( MouseEvent.MOUSE_CLICKED, MouseButton.SECONDARY, false, false, false, false, false )
			),

			// Camera Move
			Arguments.of( createMetadata( "camera-move", "camera-move", CameraMove.class ), createMouseEvent( MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY, true, false, false, false, true ) ),

			// Camera Zoom
			Arguments.of( createMetadata( "camera-zoom", "camera-zoom", CameraZoom.class ), createScrollEvent( ScrollEvent.SCROLL, true, false, false, false, false, false ) )
			//Arguments.of( createMetadata( "camera-zoom", "camera-zoom", CameraZoom.class ), createZoomEvent( ZoomEvent.ZOOM, false, false, false, false, false, false ) )
		);
	}

}
