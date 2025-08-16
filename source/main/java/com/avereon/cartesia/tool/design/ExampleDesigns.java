package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.*;
import javafx.geometry.Point3D;
import javafx.scene.shape.StrokeLineCap;

import java.util.List;
import java.util.Set;

public class ExampleDesigns {

	public static Design singleHorizontalLine() {
		Design design = new Design2D();
		design.setName( "Test Design" );

		DesignLine line = new DesignLine( -1, 0, 1, 0 );
		line.setDrawPaint( "#ffffff" );
		line.setDrawWidth( "1.0" );
		line.setDrawCap( StrokeLineCap.ROUND.name() );
		line.setOrder( 0 );

		DesignLayer construction = new DesignLayer();
		construction.setName( "Construction" );
		construction.addShapes( Set.of( line ) );
		design.getLayers().addLayer( construction );

		return design;
	}

	public static Design singleVerticalLine() {
		Design design = new Design2D();
		design.setName( "Test Design" );

		DesignLine line = new DesignLine( 0, 1, 0, -1 );
		line.setDrawPaint( "#ffffff" );
		line.setDrawWidth( "1.0" );
		line.setDrawCap( StrokeLineCap.ROUND.name() );
		line.setOrder( 0 );

		DesignLayer construction = new DesignLayer();
		construction.setName( "Construction" );
		construction.addShapes( Set.of( line ) );
		design.getLayers().addLayer( construction );

		return design;
	}

	public static Design redBlueX() {
		Design design = new Design2D();
		design.setName( "Test Design" );

		DesignLine blueLine = new DesignLine( -5, -5, 5, 5 );
		blueLine.setDrawPaint( "#000080" );
		blueLine.setDrawWidth( "1.0" );
		blueLine.setDrawCap( StrokeLineCap.ROUND.name() );
		blueLine.setOrder( 0 );

		DesignLine redLine = new DesignLine( -5, 5, 5, -5 );
		redLine.setDrawPaint( "#800000" );
		redLine.setDrawWidth( "1.0" );
		redLine.setDrawCap( StrokeLineCap.ROUND.name() );
		redLine.setOrder( 1 );

		DesignLayer construction = new DesignLayer();
		construction.setName( "Construction" );
		construction.addShapes( Set.of( redLine, blueLine ) );
		design.getLayers().addLayer( construction );

		return design;
	}

	public static Design design1() {
		Design design = new Design2D();
		design.setName( "Test Design" );

		DesignLine greenLineA = new DesignLine( -5, 5, -3, 3 );
		greenLineA.setDrawPaint( "#008000" );
		greenLineA.setDrawWidth( "1.0" );
		greenLineA.setDrawCap( StrokeLineCap.ROUND.name() );
		greenLineA.setOrder( 0 );
		DesignLine greenLineB = new DesignLine( -1, 4, 1, 4 );
		greenLineB.setDrawPaint( "#008000" );
		greenLineB.setDrawWidth( "1.0" );
		greenLineB.setDrawCap( StrokeLineCap.ROUND.name() );
		greenLineB.setOrder( 0 );
		DesignLine greenLineC = new DesignLine( 3, 3, 5, 5 );
		greenLineC.setDrawPaint( "#008000" );
		greenLineC.setDrawWidth( "1.0" );
		greenLineC.setDrawCap( StrokeLineCap.ROUND.name() );
		greenLineC.setOrder( 0 );

		DesignLine blueLineA = new DesignLine( -5, 3, -3, 1 );
		blueLineA.setDrawPaint( "#000080" );
		blueLineA.setDrawWidth( "1.0" );
		blueLineA.setDrawCap( StrokeLineCap.SQUARE.name() );
		blueLineA.setOrder( 0 );
		DesignLine blueLineB = new DesignLine( -1, 2, 1, 2 );
		blueLineB.setDrawPaint( "#000080" );
		blueLineB.setDrawWidth( "1.0" );
		blueLineB.setDrawCap( StrokeLineCap.SQUARE.name() );
		blueLineB.setOrder( 0 );
		DesignLine blueLineC = new DesignLine( 3, 1, 5, 3 );
		blueLineC.setDrawPaint( "#000080" );
		blueLineC.setDrawWidth( "1.0" );
		blueLineC.setDrawCap( StrokeLineCap.SQUARE.name() );
		blueLineC.setOrder( 0 );

		DesignLine redLineA = new DesignLine( -5, 1, -3, -1 );
		redLineA.setDrawPaint( "#800000" );
		redLineA.setDrawWidth( "1.0" );
		redLineA.setDrawCap( StrokeLineCap.BUTT.name() );
		redLineA.setOrder( 1 );
		DesignLine redLineB = new DesignLine( -1, 0, 1, 0 );
		redLineB.setDrawPaint( "#800000" );
		redLineB.setDrawWidth( "1.0" );
		redLineB.setDrawCap( StrokeLineCap.BUTT.name() );
		redLineB.setOrder( 1 );
		DesignLine redLineC = new DesignLine( 3, -1, 5, 1 );
		redLineC.setDrawPaint( "#800000" );
		redLineC.setDrawWidth( "1.0" );
		redLineC.setDrawCap( StrokeLineCap.BUTT.name() );
		redLineC.setOrder( 1 );

		DesignArc arc1 = new DesignArc( new Point3D( 0, 0, 0 ), 10.0, 10.0, -135.0, 90.0, DesignArc.Type.OPEN );
		DesignArc arc2 = new DesignArc( new Point3D( 0, 0, 0 ), 10.0, 10.0, 135.0, -90.0, DesignArc.Type.OPEN );

		DesignEllipse ellipse1 = new DesignEllipse( new Point3D( 7, -2, 0), 1.0 );
		ellipse1.setFillPaint( "#C0808080" );
		DesignEllipse ellipse2 = new DesignEllipse( new Point3D( 7, -5.5, 0), 1.0,1.5, -45.0 );

		DesignBox box1 = new DesignBox( -8, -2, 2, 1 );
		box1.setRotate( 15.0 );
		DesignBox box2 = new DesignBox( -8, -6, 2, 3 );
		box2.setFillPaint( "#80C08080" );

		DesignQuad quad1 = new DesignQuad( new Point3D( -8, 4, 0 ), new Point3D( -7, 2, 0 ), new Point3D( -6, 4, 0 ) );
		DesignQuad quad2 = new DesignQuad( new Point3D( -8, 0, 0 ), new Point3D( -7, 2, 0 ), new Point3D( -6, 0, 0 ) );
		DesignQuad quad3 = new DesignQuad( new Point3D( -8, 3, 0 ), new Point3D( -7, 2, 0 ), new Point3D( -8, 1, 0 ) );
		DesignQuad quad4 = new DesignQuad( new Point3D( -6, 3, 0 ), new Point3D( -7, 2, 0 ), new Point3D( -6, 1, 0 ) );

		DesignCubic curve1 = new DesignCubic( new Point3D( 6, 4, 0 ), new Point3D( 7, 5, 0 ), new Point3D( 7, 3, 0 ), new Point3D( 8, 4, 0 ) );
		DesignCubic curve2 = new DesignCubic( new Point3D( 6, 2, 0 ), new Point3D( 6, 4, 0 ), new Point3D( 8, 4, 0 ), new Point3D( 8, 2, 0 ) );
		DesignCubic curve3 = new DesignCubic( new Point3D( 7, 1, 0 ), new Point3D( 6, 0, 0 ), new Point3D( 8, 0, 0 ), new Point3D( 7, 1, 0 ) );

		DesignText hello = new DesignText( new Point3D( -5, -5, 0 ), "Hello" );
		hello.setFillPaint( "#80C0FF" );
		hello.setRotate( -45 );
		DesignText sweet = new DesignText( new Point3D( -1.5, -6, 0 ), "Sweet" );
		sweet.setFillPaint( "#80C0FF" );
		DesignText world = new DesignText( new Point3D( 3, -7, 0 ), "World" );
		world.setFillPaint( "#80C0FF" );
		world.setRotate( 45 );

		DesignMarker cross = new DesignMarker( new Point3D( 0, -2, 0 ), "2" );
		cross.setDrawPaint( "#80C0FF" );
		DesignMarker star = new DesignMarker( new Point3D( -4, -2, 0 ), "1", DesignMarker.Type.STAR );
		star.setDrawPaint( "#C080FF" );
		DesignMarker centerOfMass = new DesignMarker( new Point3D( 4, -2, 0 ), "1", DesignMarker.Type.CG );
		centerOfMass.setDrawPaint( "#80FFC0" );

		DesignPath path = new DesignPath();
		path.move( -3, -4 );
		path.cubic( -3, -3, -1, -3, 0, -4 );
		path.cubic( 1, -5, 3, -5, 3, -4 );
		path.cubic( 3, -3, 1, -3, 0, -4 );
		path.cubic( -1, -5, -3, -5, -3, -4 );
		path.close();
		path.setFillPaint( "#20808040" );

		DesignLayer construction = new DesignLayer();
		construction.setName( "Construction" );
		// Lines
		construction.addShapes( List.of( greenLineA, greenLineB, greenLineC ) );
		construction.addShapes( List.of( blueLineA, blueLineB, blueLineC ) );
		construction.addShapes( List.of( redLineA, redLineB, redLineC ) );
		// Boxes
		construction.addShapes( List.of( box1, box2 ) );
		// Arcs
		construction.addShapes( List.of( arc1, arc2 ) );
		// Ellipses
		construction.addShapes( List.of( ellipse1, ellipse2 ) );
		// Quad
		construction.addShapes( List.of( quad1, quad2, quad3, quad4 ) );
		// Cubic
		construction.addShapes( List.of( curve1, curve2, curve3 ) );
		// Text
		construction.addShapes( List.of( hello, sweet, world ) );
		// Markers
		construction.addShapes( List.of( star, cross, centerOfMass ) );
		// Paths
		construction.addShapes( List.of( path ) );

		design.getLayers().addLayer( construction );

		return design;
	}

}
