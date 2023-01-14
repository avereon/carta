package com.avereon.cartesia.data;

import com.avereon.zarra.font.FontUtil;
import javafx.geometry.Point3D;
import javafx.scene.text.Font;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignTextTest {

	@Test
	void testDefaults() {
		DesignText text = new DesignText();
		assertThat( text.getOrigin() ).isNull();
		assertThat( text.getText() ).isNull();
		assertThat( text.getTextFont() ).isEqualTo( DesignLayer.MODE_LAYER );
		assertThat( text.getRotate() ).isNull();
	}

	@Test
	void testModify() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Test" );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();

		text.setOrigin( new Point3D( 0, 0, 0 ) );
		text.setText( "Test" );
		text.setTextFont( DesignLayer.MODE_LAYER );
		text.setRotate( null );
		assertThat( text.isModified() ).isFalse();

		text.setOrigin( new Point3D( 1, 1, 0 ) );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();

		text.setText( "Modify" );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();

		text.setTextFont( FontUtil.encode( Font.font( "Serif", 24 ) ) );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();

		text.setRotate( 25.0 );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();
	}

	@Test
	void testOrigin() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ) );
		assertThat( text.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		text.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( text.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testText() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		assertThat( text.getText() ).isEqualTo( "Empty" );

		text.setText( "Test" );
		assertThat( text.getText() ).isEqualTo( "Test" );
	}

	@Test
	void testFont() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		assertThat( text.getTextFont() ).isEqualTo( DesignDrawable.MODE_LAYER );

		text.setTextFont( FontUtil.encode( Font.font( "Serif", 24 ) ) );
		assertThat( text.getTextFont() ).isEqualTo( FontUtil.encode( Font.font( "Serif", 24 ) ) );
	}

	@Test
	void testRotate() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty", 40.0 );
		assertThat( text.getRotate() ).isEqualTo( 40.0 );

		text.setRotate( 73.0 );
		assertThat( text.getRotate() ).isEqualTo( 73.0 );
	}

	@Test
	void testChangeTextFontModeFromDefaultToCustom() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		DesignLayer layer = new DesignLayer();
		layer.addShape( text );

		// Change mode to custom to copy current getTextFontWithInheritance value
		text.changeTextFontMode( DesignDrawable.MODE_CUSTOM );
		assertThat( text.getValueMode( text.getTextFont() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the pattern is a copy of the layer pattern value
		assertThat( text.getTextFont() ).isEqualTo( DesignLayer.DEFAULT_TEXT_FONT );
		assertThat( text.calcTextFont() ).isEqualTo( FontUtil.decode( DesignLayer.DEFAULT_TEXT_FONT ) );

		// Change the layer pattern to ensure that pattern value is still the custom value
		layer.setTextFont( "Serif|Regular|12.0" );
		assertThat( text.getTextFont() ).isEqualTo( DesignLayer.DEFAULT_TEXT_FONT );
		assertThat( text.calcTextFont() ).isEqualTo( FontUtil.decode( DesignLayer.DEFAULT_TEXT_FONT ) );
	}

	@Test
	void testChangeTextFontModeFromLayerToCustom() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		DesignLayer layer = new DesignLayer();
		layer.addShape( text );

		String font = FontUtil.encode( Font.getDefault() );
		layer.setTextFont( font );

		// Change mode to custom to copy current getTextFontWithInheritance value
		text.changeTextFontMode( DesignDrawable.MODE_CUSTOM );
		assertThat( text.getValueMode( text.getTextFont() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the pattern value is a copy of the layer pattern value
		assertThat( text.getTextFont() ).isEqualTo( font );
		assertThat( text.calcTextFont() ).isEqualTo( FontUtil.decode( font ) );

		// Change the layer pattern to ensure that pattern value is still the custom value
		layer.setTextFont( "Serif|Regular|12.0" );
		assertThat( text.getTextFont() ).isEqualTo( font );
		assertThat( text.calcTextFont() ).isEqualTo( FontUtil.decode( font ) );
	}

	@Test
	void testSetTextFontWhenTextFontModeIsLayer() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		DesignLayer layer = new DesignLayer();
		layer.addShape( text );

		assertThat( text.getValueMode( text.getTextFont() ) ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( text.getTextFont() ).isEqualTo( DesignLayer.MODE_LAYER );
		assertThat( text.calcTextFont() ).isEqualTo( FontUtil.decode( DesignLayer.DEFAULT_TEXT_FONT ) );

		layer.setTextFont( "Serif|Regular|12.0" );
		assertThat( text.getValueMode( text.getTextFont() ) ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( text.getTextFont() ).isEqualTo( DesignLayer.MODE_LAYER );
		assertThat( text.calcTextFont() ).isEqualTo( FontUtil.decode( "Serif|Regular|12.0" ) );

		text.setTextFont( "SansSerif|Regular|16.0" );
		assertThat( text.getValueMode( text.getTextFont() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		assertThat( text.getTextFont() ).isEqualTo( "SansSerif|Regular|16.0" );
		assertThat( text.calcTextFont() ).isEqualTo( FontUtil.decode( "SansSerif|Regular|16.0" ) );
	}

}
