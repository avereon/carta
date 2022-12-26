package com.avereon.cartesia.data;

import com.avereon.zarra.font.FontUtil;
import javafx.geometry.Point3D;
import javafx.scene.text.Font;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignTextTest {

	@Test
	void testModify() {
		DesignText textline = new DesignText( new Point3D( 0, 0, 0 ), "Test", Font.getDefault(), 0.0 );
		assertThat( textline.isModified() ).isTrue();
		textline.setModified( false );
		assertThat( textline.isModified() ).isFalse();

		textline.setOrigin( new Point3D( 0, 0, 0 ) );
		textline.setText( "Test" );
		textline.setTextFont( FontUtil.encode( Font.getDefault() ) );
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

		textline.setTextFont( FontUtil.encode( Font.font( "Serif", 24 ) ) );
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
		DesignText textline = new DesignText( new Point3D( 0, 0, 0 ) );
		assertThat( textline.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		textline.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( textline.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testText() {
		DesignText textline = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		assertThat( textline.getText() ).isEqualTo( "Empty" );

		textline.setText( "Test" );
		assertThat( textline.getText() ).isEqualTo( "Test" );
	}

	@Test
	void testFont() {
		DesignText textline = new DesignText( new Point3D( 0, 0, 0 ), "Empty", Font.font( "Sans-Serif", 24 ) );
		assertThat( textline.getTextFont() ).isEqualTo( FontUtil.encode(Font.font( "Sans-Serif", 24 ) ) );

		textline.setTextFont( FontUtil.encode( Font.font( "Serif", 24 ) ) );
		assertThat( textline.getTextFont() ).isEqualTo( FontUtil.encode(Font.font( "Serif", 24 ) ) );
	}

	@Test
	void testRotate() {
		DesignText textline = new DesignText( new Point3D( 0, 0, 0 ), "Empty", Font.getDefault(), 40.0 );
		assertThat( textline.getRotate() ).isEqualTo( 40.0 );

		textline.setRotate( 73.0 );
		assertThat( textline.getRotate() ).isEqualTo( 73.0 );
	}

}
