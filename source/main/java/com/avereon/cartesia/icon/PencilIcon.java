package com.avereon.cartesia.icon;

import com.avereon.zerra.color.Colors;
import com.avereon.zerra.image.Proof;
import com.avereon.zerra.image.SvgIcon;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class PencilIcon extends SvgIcon {

	protected void define() {
		super.define();
		fill( "M3,29L5,25L7,27Z" );
		fill( "M24,4L28,8L26,10L22,6Z", Colors.mix( (Color)getStrokePaint(), Color.TRANSPARENT, 0.2 ) );
		fill( "M24,4L25,3A2,1,45,0,1,29,7L28,8Z", Colors.mix( Color.HOTPINK, Color.TRANSPARENT, 0.2 ) );
		draw( "M3,29L7,21L25,3A2,1,45,0,1,29,7L11,25Z M9,23L24,8 M24,4L28,8 M22,6L26,10 M7,21L7,22L9,23L10,25L11,25", null, 1, StrokeLineCap.BUTT, StrokeLineJoin.MITER );
	}

	public static void main( String[] commands ) {
		Proof.proof( new PencilIcon() );
	}

}
