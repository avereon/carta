package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import javafx.scene.text.Font;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignTextAreaTest {

	@Test
	void testModify() {
		DesignTextArea textarea = new DesignTextArea( new Point3D( 0, 0, 0 ), "Test", Font.getDefault(), 0.0 );
		assertThat( textarea.isModified() ).isTrue();
		textarea.setModified( false );
		assertThat( textarea.isModified() ).isFalse();

		textarea.setOrigin( new Point3D( 0, 0, 0 ) );
		textarea.setText( "Test" );
		textarea.setFont( Font.getDefault() );
		textarea.setRotate( 0.0 );
		assertThat( textarea.isModified() ).isFalse();

		textarea.setOrigin( new Point3D( 1, 1, 0 ) );
		assertThat( textarea.isModified() ).isTrue();
		textarea.setModified( false );
		assertThat( textarea.isModified() ).isFalse();

		textarea.setText( "Modify" );
		assertThat( textarea.isModified() ).isTrue();
		textarea.setModified( false );
		assertThat( textarea.isModified() ).isFalse();

		textarea.setFont( Font.font( "Serif", 24 ) );
		assertThat( textarea.isModified() ).isTrue();
		textarea.setModified( false );
		assertThat( textarea.isModified() ).isFalse();

		textarea.setRotate( 25.0 );
		assertThat( textarea.isModified() ).isTrue();
		textarea.setModified( false );
		assertThat( textarea.isModified() ).isFalse();
	}

	@Test
	void testOrigin() {
		DesignTextArea textarea = new DesignTextArea( new Point3D( 0, 0, 0 ) );
		assertThat( textarea.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		textarea.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( textarea.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
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
