package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadShapes;
import com.avereon.zarra.color.Paints;
import com.avereon.zarra.font.FontUtil;
import javafx.geometry.Point3D;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignTextTest {

	private DesignText text;

	@BeforeEach
	void setup() {
		text = new DesignText();
	}

	@Test
	void testInitialValues() {
		assertThat( text.getOrigin() ).isNull();
		assertThat( text.getText() ).isNull();
		assertThat( text.getRotate() ).isNull();

		assertThat( text.getTextSize() ).isNull();
		assertThat( text.getFillPaint() ).isNull();
		assertThat( text.getDrawPaint() ).isNull();
		assertThat( text.getDrawWidth() ).isNull();
		assertThat( text.getDrawPattern() ).isNull();
		assertThat( text.getDrawCap() ).isNull();

		assertThat( text.getFontName() ).isNull();
		assertThat( text.getFontWeight() ).isNull();
		assertThat( text.getFontPosture() ).isNull();
		assertThat( text.getFontUnderline() ).isNull();
		assertThat( text.getFontStrikethrough() ).isNull();

		// Backward compatibility
		assertThat( text.getTextFont() ).isNull();
	}

	@Test
	void testCalcWithInitialValues() throws Exception {
		// Calculated values should not be null, except for strings, and use the default values for initial values
		assertThat( text.calcRotate() ).isEqualTo( Double.parseDouble( DesignText.DEFAULT_ROTATE ) );

		assertThat( text.calcTextSize() ).isEqualTo( Double.parseDouble( DesignLayer.DEFAULT_TEXT_SIZE ) );
		assertThat( text.calcDrawPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_TEXT_DRAW_PAINT ) );
		assertThat( text.calcDrawWidth() ).isEqualTo( Double.parseDouble( DesignLayer.DEFAULT_TEXT_DRAW_WIDTH ) );
		assertThat( text.calcDrawPattern() ).containsExactlyElementsOf( CadShapes.parseDashPattern( DesignLayer.DEFAULT_TEXT_DRAW_PATTERN ) );
		assertThat( text.calcDrawCap() ).isEqualTo( StrokeLineCap.valueOf( DesignLayer.DEFAULT_TEXT_DRAW_CAP.toUpperCase() ) );
		assertThat( text.calcFillPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_TEXT_FILL_PAINT ) );

		assertThat( text.calcFontName() ).isEqualTo( DesignLayer.DEFAULT_FONT_NAME );
		assertThat( text.calcFontWeight() ).isEqualTo( FontWeight.valueOf( DesignLayer.DEFAULT_FONT_WEIGHT.toUpperCase() ) );
		assertThat( text.calcFontPosture() ).isEqualTo( FontPosture.valueOf( DesignLayer.DEFAULT_FONT_POSTURE.toUpperCase() ) );
		assertThat( text.calcFontUnderline() ).isEqualTo( Boolean.parseBoolean( DesignLayer.DEFAULT_FONT_UNDERLINE ) );
		assertThat( text.calcFontStrikethrough() ).isEqualTo( Boolean.parseBoolean( DesignLayer.DEFAULT_FONT_STRIKETHROUGH ) );

		assertThat( text.calcTextFont() ).isEqualTo( Font.font( DesignLayer.DEFAULT_FONT_NAME, Double.parseDouble( DesignLayer.DEFAULT_TEXT_SIZE ) ) );
	}

	@Test
	void testModify() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Test" );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();

		text.setOrigin( new Point3D( 0, 0, 0 ) );
		text.setText( "Test" );
		text.setTextFont( null );
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

		text.setRotate( 25.0 );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();

		text.setTextSize( "2" );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();

		text.setFontName( "Andromeda" );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();

		text.setFontWeight( "light" );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();

		text.setFontPosture( "italic" );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();

		text.setFontUnderline( "true" );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();

		text.setFontStrikethrough( "true" );
		assertThat( text.isModified() ).isTrue();
		text.setModified( false );
		assertThat( text.isModified() ).isFalse();

		// Backward compatibility
		text.setTextFont( FontUtil.encode( Font.font( "Serif", 24 ) ) );
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
	void testRotate() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty", 40.0 );
		assertThat( text.getRotate() ).isEqualTo( 40.0 );

		text.setRotate( 73.0 );
		assertThat( text.getRotate() ).isEqualTo( 73.0 );
	}

	@Test
	void testTextSize() {
		DesignText text = new DesignText();
		assertThat( text.getTextSize() ).isNull();

		text.setTextSize( "73.0" );
		assertThat( text.getTextSize() ).isEqualTo( "73.0" );
		assertThat( text.calcTextSize() ).isEqualTo( 73.0 );
	}

	@Test
	void testFontName() {
		DesignText text = new DesignText();
		assertThat( text.getFontName() ).isNull();

		text.setFontName( "Andromeda" );
		assertThat( text.getFontName() ).isEqualTo( "Andromeda" );
		assertThat( text.calcFontName() ).isEqualTo( "Andromeda" );
	}

	@Test
	void testFontWeight() {
		DesignText text = new DesignText();
		assertThat( text.getFontWeight() ).isNull();

		text.setFontWeight( FontWeight.LIGHT.name().toLowerCase() );
		assertThat( text.getFontWeight() ).isEqualTo( FontWeight.LIGHT.name().toLowerCase() );
		assertThat( text.calcFontWeight() ).isEqualTo( FontWeight.LIGHT );
	}

	@Test
	void testFontPosture() {
		DesignText text = new DesignText();
		assertThat( text.getFontPosture() ).isNull();

		text.setFontPosture( FontPosture.ITALIC.name().toLowerCase() );
		assertThat( text.getFontPosture() ).isEqualTo( FontPosture.ITALIC.name().toLowerCase() );
		assertThat( text.calcFontPosture() ).isEqualTo( FontPosture.ITALIC );
	}

	@Test
	void testFontUnderline() {
		DesignText text = new DesignText();
		assertThat( text.getFontUnderline() ).isNull();

		text.setFontUnderline( "true" );
		assertThat( text.getFontUnderline() ).isEqualTo( "true" );
		assertThat( text.calcFontUnderline() ).isEqualTo( true );

		text.setFontUnderline( "underline" );
		assertThat( text.getFontUnderline() ).isEqualTo( "underline" );
		assertThat( text.calcFontUnderline() ).isEqualTo( false );
	}

	@Test
	void testFontStrikethrough() {
		DesignText text = new DesignText();
		assertThat( text.getFontStrikethrough() ).isNull();

		text.setFontStrikethrough( "true" );
		assertThat( text.getFontStrikethrough() ).isEqualTo( "true" );
		assertThat( text.calcFontStrikethrough() ).isEqualTo( true );

		text.setFontStrikethrough( "strikethrough" );
		assertThat( text.getFontStrikethrough() ).isEqualTo( "strikethrough" );
		assertThat( text.calcFontStrikethrough() ).isEqualTo( false );
	}

	@Test
	@Deprecated
	void testFont() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		assertThat( text.getTextFont() ).isNull();

		text.setTextFont( FontUtil.encode( Font.font( "Serif", 24 ) ) );
		assertThat( text.getTextFont() ).isEqualTo( FontUtil.encode( Font.font( "Serif", 24 ) ) );
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
		assertThat( text.getTextFont() ).isNull();
		assertThat( text.calcTextFont() ).isEqualTo( FontUtil.decode( DesignLayer.DEFAULT_TEXT_FONT ) );

		layer.setTextFont( "Serif|Regular|12.0" );
		assertThat( text.getValueMode( text.getTextFont() ) ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( text.getTextFont() ).isNull();
		assertThat( text.calcTextFont() ).isEqualTo( FontUtil.decode( "Serif|Regular|12.0" ) );

		text.setTextFont( "SansSerif|Regular|16.0" );
		assertThat( text.getValueMode( text.getTextFont() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		assertThat( text.getTextFont() ).isEqualTo( "SansSerif|Regular|16.0" );
		assertThat( text.calcTextFont() ).isEqualTo( FontUtil.decode( "SansSerif|Regular|16.0" ) );
	}

	// ### Text size

	@Test
	void testChangeTextSizeModeFromDefaultToCustom() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		DesignLayer layer = new DesignLayer();
		layer.addShape( text );

		// Change mode to custom to copy current getTextSizeWithInheritance value
		text.changeTextSizeMode( DesignDrawable.MODE_CUSTOM );
		assertThat( text.getValueMode( text.getTextSize() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the size is a copy of the layer size value
		assertThat( text.getTextSize() ).isEqualTo( DesignLayer.DEFAULT_TEXT_SIZE );
		assertThat( text.calcTextSize() ).isEqualTo( Double.parseDouble( DesignLayer.DEFAULT_TEXT_SIZE ) );

		// Change the layer size to ensure that size value is still the custom value
		layer.setTextSize( "8.0" );
		assertThat( text.getTextSize() ).isEqualTo( DesignLayer.DEFAULT_TEXT_SIZE );
		assertThat( text.calcTextSize() ).isEqualTo( Double.parseDouble( DesignLayer.DEFAULT_TEXT_SIZE ) );
	}

	@Test
	void testChangeTextSizeModeFromLayerToCustom() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		DesignLayer layer = new DesignLayer();
		layer.addShape( text );

		// Set a custom layer text size
		layer.setTextSize( "1/8" );

		// Change mode to custom to copy current getTextSizeWithInheritance value
		text.changeTextSizeMode( DesignDrawable.MODE_CUSTOM );
		assertThat( text.getValueMode( text.getTextSize() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the size value is a copy of the layer size value
		assertThat( text.getTextSize() ).isEqualTo( "1/8" );
		assertThat( text.calcTextSize() ).isEqualTo( 0.125 );

		// Change the layer size to ensure that size value is still the custom value
		layer.setTextSize( "5" );
		assertThat( text.getTextSize() ).isEqualTo( "1/8" );
		assertThat( text.calcTextSize() ).isEqualTo( 1.0 / 8.0 );
	}

	@Test
	void testSetTextSizeWhenTextSizeModeIsLayer() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		DesignLayer layer = new DesignLayer();
		layer.addShape( text );

		assertThat( text.getValueMode( text.getTextSize() ) ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( text.getTextSize() ).isNull();
		assertThat( text.calcTextSize() ).isEqualTo( Double.parseDouble( DesignLayer.DEFAULT_TEXT_SIZE ) );

		layer.setTextSize( "1/6" );
		assertThat( text.getValueMode( text.getTextSize() ) ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( text.getTextSize() ).isNull();
		assertThat( text.calcTextSize() ).isEqualTo( 1.0 / 6.0 );

		text.setTextSize( "16.0" );
		assertThat( text.getValueMode( text.getTextSize() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		assertThat( text.getTextSize() ).isEqualTo( "16.0" );
		assertThat( text.calcTextSize() ).isEqualTo( 16.0 );
	}

	// ### Font name

	@Test
	void testChangeFontNameModeFromDefaultToCustom() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		DesignLayer layer = new DesignLayer();
		layer.addShape( text );

		// Change mode to custom to copy current getFontNameWithInheritance value
		text.changeFontNameMode( DesignDrawable.MODE_CUSTOM );
		// Because the default value is null the value mode is the layer mode
		assertThat( text.getValueMode( text.getFontName() ) ).isEqualTo( DesignDrawable.MODE_LAYER );

		// Check that the name is a copy of the layer name value
		assertThat( text.getFontName() ).isEqualTo( DesignLayer.DEFAULT_FONT_NAME );
		assertThat( text.calcFontName() ).isEqualTo( DesignLayer.DEFAULT_FONT_NAME );

		// Change the layer name to ensure that size value is still the custom value
		layer.setFontName( "Pinwheel" );
		assertThat( text.getFontName() ).isEqualTo( DesignLayer.DEFAULT_FONT_NAME );
		// Because the default value is null the value mode is the layer mode
		assertThat( text.calcFontName() ).isEqualTo( "Pinwheel" );
	}

	@Test
	void testChangeFontNameModeFromLayerToCustom() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		DesignLayer layer = new DesignLayer();
		layer.addShape( text );

		// Set a custom layer text size
		layer.setFontName( "Whirlpool" );

		// Change mode to custom to copy current getFontNameWithInheritance value
		text.changeFontNameMode( DesignDrawable.MODE_CUSTOM );
		assertThat( text.getValueMode( text.getFontName() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the size value is a copy of the layer size value
		assertThat( text.getFontName() ).isEqualTo( "Whirlpool" );
		assertThat( text.calcFontName() ).isEqualTo( "Whirlpool" );

		// Change the layer size to ensure that size value is still the custom value
		layer.setFontName( "Andromeda" );
		assertThat( text.getFontName() ).isEqualTo( "Whirlpool" );
		assertThat( text.calcFontName() ).isEqualTo( "Whirlpool" );
	}

	@Test
	void testSetFontNameWhenFontSizeModeIsLayer() {
		DesignText text = new DesignText( new Point3D( 0, 0, 0 ), "Empty" );
		DesignLayer layer = new DesignLayer();
		layer.addShape( text );

		assertThat( text.getValueMode( text.getFontName() ) ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( text.getFontName() ).isNull();
		assertThat( text.calcFontName() ).isEqualTo(  DesignLayer.DEFAULT_FONT_NAME );

		layer.setFontName( "Cartwheel" );
		assertThat( text.getValueMode( text.getFontName() ) ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( text.getFontName() ).isNull();
		assertThat( text.calcFontName() ).isEqualTo( "Cartwheel" );

		text.setFontName( "Andromeda" );
		assertThat( text.getValueMode( text.getFontName() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		assertThat( text.getFontName() ).isEqualTo( "Andromeda" );
		assertThat( text.calcFontName() ).isEqualTo( "Andromeda" );
	}

	// NEXT Continue writing tests for remaining fields
}
