package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import javafx.scene.text.Font;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignTextLineTest {

	@Test
	void testModify() {
		DesignTextLine textline = new DesignTextLine( new Point3D( 0, 0, 0 ), "Test", Font.getDefault(), 0.0 );
		assertThat( textline.isModified() ).isTrue();
		textline.setModified( false );
		assertThat( textline.isModified() ).isFalse();

		textline.setOrigin( new Point3D( 0, 0, 0 ) );
		textline.setText( "Test" );
		textline.setFont( Font.getDefault() );
		textline.setRotate( 0.0 );
		assertThat( textline.isModified() ).isFalse();

		textline.setOrigin( new Point3D( 1, 1, 0 ) );
		assertThat( textline.isModified() ).isTrue();
		textline.setModified( false );
		assertThat( textline.isModified() ).isFalse();

		textline.setText( "Modify" );
		assertThat( textline.isModified() ).isTrue();
		textline.setModified( false );
		assertThat( textline.isModified() ).isFalse();

		textline.setFont( Font.font( "Serif", 24 ) );
		assertThat( textline.isModified() ).isTrue();
		textline.setModified( false );
		assertThat( textline.isModified() ).isFalse();

		textline.setRotate( 25.0 );
		assertThat( textline.isModified() ).isTrue();
		textline.setModified( false );
		assertThat( textline.isModified() ).isFalse();
	}

	@Test
	void testOrigin() {
		DesignTextLine textline = new DesignTextLine( new Point3D( 0, 0, 0 ) );
		assertThat( textline.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		textline.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( textline.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testText() {
		DesignTextLine textline = new DesignTextLine( new Point3D( 0, 0, 0 ), "Empty" );
		assertThat( textline.getText() ).isEqualTo( "Empty" );

		textline.setText( "Test" );
		assertThat( textline.getText() ).isEqualTo( "Test" );
	}

	@Test
	void testFont() {
		DesignTextLine textline = new DesignTextLine( new Point3D( 0, 0, 0 ), "Empty", Font.font( "Sans-Serif", 24 ) );
		assertThat( textline.getFont() ).isEqualTo( Font.font( "Sans-Serif", 24 ) );

		textline.setFont( Font.font( "Serif", 24 ) );
		assertThat( textline.getFont() ).isEqualTo( Font.font( "Serif", 24 ) );
	}

	@Test
	void testRotate() {
		DesignTextLine textline = new DesignTextLine( new Point3D( 0, 0, 0 ), "Empty", Font.getDefault(), 40.0 );
		assertThat( textline.getRotate() ).isEqualTo( 40.0 );

		textline.setRotate( 73.0 );
		assertThat( textline.getRotate() ).isEqualTo( 73.0 );
	}

}
