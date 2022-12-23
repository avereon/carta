package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import javafx.scene.text.Font;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignTextLineTest {

	@Test
	void testOrigin() {
		DesignTextLine textline = new DesignTextLine( new Point3D( 0, 0, 0 ) );
		assertThat( textline.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		textline.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( textline.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testText() {
		DesignTextArea textarea = new DesignTextArea( new Point3D( 0, 0, 0 ), "Empty" );
		assertThat( textarea.getText() ).isEqualTo( "Empty" );

		textarea.setText( "Test" );
		assertThat( textarea.getText() ).isEqualTo( "Test" );
	}

	@Test
	void testFont() {
		DesignTextArea textarea = new DesignTextArea( new Point3D( 0, 0, 0 ), "Empty", Font.font( "Sans-Serif", 24 ) );
		assertThat( textarea.getFont() ).isEqualTo( Font.font( "Sans-Serif", 24 ) );

		textarea.setFont( Font.font( "Serif", 24 ) );
		assertThat( textarea.getFont() ).isEqualTo( Font.font( "Serif", 24 ) );
	}

	@Test
	void testRotate() {
		DesignTextArea textarea = new DesignTextArea( new Point3D( 0, 0, 0 ), "Empty", Font.getDefault(), 40.0 );
		assertThat( textarea.getRotate() ).isEqualTo( 40.0 );

		textarea.setRotate( 73.0 );
		assertThat( textarea.getRotate() ).isEqualTo( 73.0 );
	}

}
