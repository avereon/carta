package com.avereon.cartesia;

import com.avereon.cartesia.command.*;
import com.avereon.cartesia.command.camera.CameraMove;
import com.avereon.cartesia.command.camera.CameraZoom;
import com.avereon.cartesia.command.snap.AutoSnap;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandMapTest extends BaseCommandMapTest {

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
	}

	@Test
	void getCommandByShortcut() {
		assertThat( getMod().getCommandMap().getCommandByShortcut( "ws" ).getType() ).isEqualTo( SelectByWindowContain.class );
	}

	@Test
	void getCommandByAction() {
		assertThat( getMod().getCommandMap().getCommandByAction( "anchor" ).getType() ).isEqualTo( Anchor.class );
	}

	@ParameterizedTest
	@MethodSource( "provideCommandMetadataInputEventMatches" )
	void getCommandByEvent( CommandMetadata expected, InputEvent event ) {
		CommandMetadata actual = getMod().getCommandMap().getCommandByEvent( event );
		assertThat( actual.getAction() ).isEqualTo( expected.getAction() );
		assertThat( actual.getName() ).isEqualTo( expected.getName() );
		assertThat( actual.getCommand() ).isEqualTo( expected.getCommand() );
		assertThat( actual.getShortcut() ).isEqualTo( expected.getShortcut() );
		assertThat( actual.getType() ).isEqualTo( expected.getType() );
	}

	private static Stream<Arguments> provideCommandMetadataInputEventMatches() {
		return Stream.of(
			// Anchor
			Arguments.of( createMetadata( "anchor", "Anchor", null, Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, false, false, false ) ),
			Arguments.of( createMetadata( "anchor", "Anchor", null, Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, true, false, false, false, false ) ),
			Arguments.of( createMetadata( "anchor", "Anchor", null, Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, true, false, false, false ) ),
			Arguments.of( createMetadata( "anchor", "Anchor", null, Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, true, false, false ) ),
			Arguments.of( createMetadata( "anchor", "Anchor", null, Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, false, true, false ) ),
			Arguments.of( createMetadata( "anchor", "Anchor", null, Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, false, false, true ) ),
			Arguments.of( createMetadata( "anchor", "Anchor", null, Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, true, false, false, false, true ) ),
			Arguments.of( createMetadata( "anchor", "Anchor", null, Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, true, false, false, true ) ),
			Arguments.of( createMetadata( "anchor", "Anchor", null, Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, true, false, true ) ),
			Arguments.of( createMetadata( "anchor", "Anchor", null, Anchor.class ), createMouseEvent( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, false, false, true, true ) ),

			// Select
			Arguments.of(
				createMetadata( "select-point", "Select By Point", null, SelectByPoint.class ),
				createMouseEvent( MouseEvent.MOUSE_RELEASED, MouseButton.PRIMARY, false, false, false, false, false )
			),
			Arguments.of(
				createMetadata( "select-toggle", "Select Toggle", null, SelectToggle.class ),
				createMouseEvent( MouseEvent.MOUSE_RELEASED, MouseButton.PRIMARY, true, false, false, false, false )
			),
			Arguments.of(
				createMetadata( "select-window-contain", "Select Window Contain", "ws", SelectByWindowContain.class ),
				createMouseEvent( MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY, false, false, false, false, true )
			),
			Arguments.of(
				createMetadata( "select-window-intersect", "Select Window Intersect", "cs", SelectByWindowIntersect.class ),
				createMouseEvent( MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY, false, true, false, false, true )
			),

			// Auto Snap
			Arguments.of( createMetadata( "snap-auto-nearest", "Snap Nearest", null, AutoSnap.class ),
				createMouseEvent( MouseEvent.MOUSE_CLICKED, MouseButton.SECONDARY, false, false, false, false, false )
			),

			// Camera Move
			Arguments.of( createMetadata( "camera-move", "Camera Pan", "pa", CameraMove.class ), createMouseEvent( MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY, true, false, false, false, true ) ),

			// Camera Zoom
			Arguments.of( createMetadata( "camera-zoom", "Zoom", "zm", CameraZoom.class ), createScrollEvent( ScrollEvent.SCROLL, true, false, false, false, false, false ) )
		);
	}

}
