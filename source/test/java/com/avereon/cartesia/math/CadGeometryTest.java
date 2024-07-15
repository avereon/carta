package com.avereon.cartesia.math;

import com.avereon.cartesia.test.PointAssert;
import com.avereon.cartesia.data.*;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import org.assertj.core.util.DoubleComparator;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.TestConstants.LOOSE_TOLERANCE;
import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class CadGeometryTest {

	// Most of the methods in CadGeometry are just forwarded to the Geometry
	// class. However, there are a few that are specific to CadGeometry. Those
	// are tested here.

	@Test
	void testArcFromThreePoints() {
		DesignArc arc;
		arc = CadGeometry.arcFromThreePoints( new Point3D( -1, 0, 0 ), new Point3D( 0, 1, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 1.0 );
		assertThat( arc.getStart() ).isEqualTo( 180.0 );
		assertThat( arc.getExtent() ).isEqualTo( -180.0 );

		arc = CadGeometry.arcFromThreePoints( new Point3D( -1, 0, 0 ), new Point3D( 0, -1, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 1.0 );
		assertThat( arc.getStart() ).isEqualTo( 180.0 );
		assertThat( arc.getExtent() ).isEqualTo( 180.0 );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 0, -1, 0 ), new Point3D( 1, 0, 0 ), new Point3D( 0, 1, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 1.0 );
		assertThat( arc.getStart() ).isEqualTo( -90.0 );
		assertThat( arc.getExtent() ).isEqualTo( 180.0 );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 0, 1, 0 ), new Point3D( 1, 0, 0 ), new Point3D( 0, -1, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 1.0 );
		assertThat( arc.getStart() ).isEqualTo( 90.0 );
		assertThat( arc.getExtent() ).isEqualTo( -180.0 );
	}

	@Test
	void testArcFromThreePointsScenarioB() {
		DesignArc arc;

		// Class A
		double a = 126.86989764584402;
		double b = 540 - 2 * a;
		double c = 2 * a - 180;
		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, -4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 8, -4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( -a );
		assertThat( arc.getExtent() ).isEqualTo( -b );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, -4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 2, -4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( a - 180 );
		assertThat( arc.getExtent() ).isEqualTo( b );

		// Class B
		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, 4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 8, 4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( a );
		assertThat( arc.getExtent() ).isEqualTo( b );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, 4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 2, 4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( 180 - a );
		assertThat( arc.getExtent() ).isEqualTo( -b );

		// Class C
		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, -4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 8, -4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( -a );
		assertThat( arc.getExtent() ).isCloseTo( c, TOLERANCE );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, 4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 8, 4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( a );
		assertThat( arc.getExtent() ).isCloseTo( -c, TOLERANCE );

		// Class D
		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, -4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 2, -4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( a - 180 );
		assertThat( arc.getExtent() ).isCloseTo( -c, TOLERANCE );
		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, 4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 2, 4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( 180 - a );
		assertThat( arc.getExtent() ).isCloseTo( c, TOLERANCE );
	}

	@Test
	void testArcFromThreePointsScenarioC() {
		DesignArc arc = CadGeometry.arcFromThreePoints( new Point3D( 1, 1, 0 ), new Point3D( 2, 2 + Math.sqrt( 2 ), 0 ), new Point3D( 3, 1, 0 ) );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 2, 2, 0 ) );
		assertThat( arc.getRadius() ).isCloseTo( Math.sqrt( 2 ), TOLERANCE );
		assertThat( arc.getStart() ).isCloseTo( -135, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( -270, TOLERANCE );

		arc = CadGeometry.arcFromThreePoints( new Point3D( -3, 1, 0 ), new Point3D( -2, 2 + Math.sqrt( 2 ), 0 ), new Point3D( -1, 1, 0 ) );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( -2, 2, 0 ) );
		assertThat( arc.getRadius() ).isCloseTo( Math.sqrt( 2 ), TOLERANCE );
		assertThat( arc.getStart() ).isCloseTo( -135, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( -270, TOLERANCE );
	}

	@Test
	void testArcFromThreePointsScenarioD() {
		double start = CadGeometry.cartesianToPolar360( new Point3D( -1, -0.75, 0 ) ).getY();
		double extent = -(360 + 2 * (start + 90));
		DesignArc arc = CadGeometry.arcFromThreePoints( new Point3D( 1, 1, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 3, 1, 0 ) );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 2, 1.75, 0 ) );
		assertThat( arc.getRadius() ).isCloseTo( 1.25, TOLERANCE );
		assertThat( arc.getStart() ).isCloseTo( CadGeometry.cartesianToPolar360( new Point3D( -1, -0.75, 0 ) ).getY(), TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( extent, TOLERANCE );
	}

	@Test
	void testArcFromThreePointsWithSameMidAndEndPoints() {
		DesignArc arc;
		arc = CadGeometry.arcFromThreePoints( new Point3D( -1, 0, 0 ), new Point3D( 0, 1, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 1.0 );
		assertThat( arc.getStart() ).isEqualTo( 180.0 );
		assertThat( arc.getExtent() ).isEqualTo( -180.0 );
	}

	@Test
	void testClampAngle() {
		assertThat( CadGeometry.clampAngle360( 0 ) ).isEqualTo( 0.0 );
		assertThat( CadGeometry.clampAngle360( 180 ) ).isEqualTo( 180.0 );
		assertThat( CadGeometry.clampAngle360( -180 ) ).isEqualTo( -180.0 );
		assertThat( CadGeometry.clampAngle360( 315 ) ).isEqualTo( -45.0 );
		assertThat( CadGeometry.clampAngle360( -315 ) ).isEqualTo( 45.0 );
		assertThat( CadGeometry.clampAngle360( 675 ) ).isEqualTo( -45.0 );
		assertThat( CadGeometry.clampAngle360( -675 ) ).isEqualTo( 45.0 );
	}

	@Test
	void testNormalizeAngle360() {
		assertThat( CadGeometry.normalizeAngle360( 0 ) ).isEqualTo( 0.0 );
		assertThat( CadGeometry.normalizeAngle360( 180 ) ).isEqualTo( 180.0 );
		assertThat( CadGeometry.normalizeAngle360( -180 ) ).isEqualTo( -180.0 );
		assertThat( CadGeometry.normalizeAngle360( 315 ) ).isEqualTo( 315.0 );
		assertThat( CadGeometry.normalizeAngle360( -315 ) ).isEqualTo( -315.0 );
		assertThat( CadGeometry.normalizeAngle360( 675 ) ).isEqualTo( 315.0 );
		assertThat( CadGeometry.normalizeAngle360( -675 ) ).isEqualTo( -315.0 );
	}

	@Test
	void toFxShapeWithBox() {
		Rectangle box = (Rectangle)CadGeometry.toFxShape( new DesignBox( new Point3D( 0, 0, 0 ), new Point3D( 1, 1, 0 ) ) );
		assertThat( box.getX() ).isEqualTo( 0 );
		assertThat( box.getY() ).isEqualTo( 0 );
		assertThat( box.getWidth() ).isEqualTo( 1 );
		assertThat( box.getHeight() ).isEqualTo( 1 );

		assertThat( box.getStroke()).isEqualTo( Color.YELLOW );
		assertThat( box.getStrokeWidth() ).isEqualTo( 0.05, TOLERANCE );
	}

	@Test
	void toFxShapeWithBoxAndScale() {
		DesignBox designBox = new DesignBox( new Point3D( 0, 0, 0 ), new Point3D( 1, 1, 0 ) );
		designBox.setDrawPattern( "0.2" );
		Rectangle box = (Rectangle)CadGeometry.toFxShape( designBox, 2.5 );
		assertThat( box.getX() ).isEqualTo( 0 );
		assertThat( box.getY() ).isEqualTo( 0 );
		assertThat( box.getWidth() ).isEqualTo( 2.5 );
		assertThat( box.getHeight() ).isEqualTo( 2.5 );

		assertThat( box.getStrokeWidth() ).isEqualTo( 0.125, TOLERANCE );
		assertThat( box.getStrokeDashArray() ).usingComparatorForType( new DoubleComparator( TOLERANCE.value ), Double.class ).containsExactly( 0.5 );
	}

	@Test
	void toFxShapeWithLine() {
		Line line = (Line)CadGeometry.toFxShape( new DesignLine( 0, 0, 1, 1 ) );
		assertThat( line.getStartX() ).isEqualTo( 0 );
		assertThat( line.getStartY() ).isEqualTo( 0 );
		assertThat( line.getEndX() ).isEqualTo( 1 );
		assertThat( line.getEndY() ).isEqualTo( 1 );

		assertThat( line.getStroke()).isEqualTo( Color.YELLOW );
		assertThat( line.getStrokeWidth() ).isEqualTo( 0.05, TOLERANCE );
	}

	@Test
	void toFxShapeWithLineAndScale() {
		DesignLine designLine = new DesignLine( 0, 0, 1, 1 );
		designLine.setDrawPattern( "0.1" );

		Line line = (Line)CadGeometry.toFxShape( designLine, 3.5 );
		assertThat( line.getStartX() ).isEqualTo( 0 );
		assertThat( line.getStartY() ).isEqualTo( 0 );
		assertThat( line.getEndX() ).isEqualTo( 3.5 );
		assertThat( line.getEndY() ).isEqualTo( 3.5 );

		assertThat( line.getStrokeWidth() ).isEqualTo( 0.175, TOLERANCE );
		assertThat( line.getStrokeDashArray() ).usingComparatorForType( new DoubleComparator( TOLERANCE.value ), Double.class ).containsExactly( 0.35 );
	}

	@Test
	void toFxShapeWithArc() {
		Arc arc = (Arc)CadGeometry.toFxShape( new DesignArc( new Point3D( 3, 5, 0 ), new Point3D( 1, 1, 0 ), 0.0, 45.0, 90.0, DesignArc.Type.OPEN ) );
		assertThat( arc.getCenterX() ).isEqualTo( 3 );
		assertThat( arc.getCenterY() ).isEqualTo( 5 );
		assertThat( arc.getRadiusX() ).isEqualTo( 1 );
		assertThat( arc.getRadiusY() ).isEqualTo( 1 );
		assertThat( arc.getStartAngle() ).isEqualTo( -45 );
		assertThat( arc.getLength() ).isEqualTo( -90 );
		assertThat( arc.getType() ).isEqualTo( ArcType.OPEN );

		// Check rotate
		assertThat( arc.getTransforms() ).isEmpty();

		assertThat( arc.getStrokeWidth() ).isEqualTo( 0.05, TOLERANCE );
	}

	@Test
	void toFxShapeWithArcAndScale() {
		DesignArc designArc = new DesignArc( new Point3D( 3, 5, 0 ), new Point3D( 1, 1, 0 ), 0.0, 0.0, 90.0, DesignArc.Type.OPEN );
		designArc.setDrawPattern( "0.1" );

		Arc arc = (Arc)CadGeometry.toFxShape( designArc, 1.5 );
		assertThat( arc.getCenterX() ).isEqualTo( 4.5 );
		assertThat( arc.getCenterY() ).isEqualTo( 7.5 );
		assertThat( arc.getRadiusX() ).isEqualTo( 1.5 );
		assertThat( arc.getRadiusY() ).isEqualTo( 1.5 );
		assertThat( arc.getStartAngle() ).isEqualTo( 0 );
		assertThat( arc.getLength() ).isEqualTo( -90 );
		assertThat( arc.getType() ).isEqualTo( ArcType.OPEN );

		// Check rotate
		assertThat( arc.getTransforms() ).isEmpty();

		assertThat( arc.getStrokeWidth() ).isEqualTo( 0.075, TOLERANCE );
		assertThat( arc.getStrokeDashArray() ).usingComparatorForType( new DoubleComparator( TOLERANCE.value ), Double.class ).containsExactly( 0.15 );
	}

	@Test
	void toFxShapeWithArcAndRotate() {
		double rotate = 1.0;
		Arc arc = (Arc)CadGeometry.toFxShape( new DesignArc( new Point3D( 3, 5, 0 ), new Point3D( 1, 1, 0 ), rotate, 0.0, 90.0, DesignArc.Type.OPEN ) );
		assertThat( arc.getCenterX() ).isEqualTo( 3 );
		assertThat( arc.getCenterY() ).isEqualTo( 5 );
		assertThat( arc.getRadiusX() ).isEqualTo( 1 );
		assertThat( arc.getRadiusY() ).isEqualTo( 1 );
		assertThat( arc.getStartAngle() ).isEqualTo( 0 );
		assertThat( arc.getLength() ).isEqualTo( -90 );
		assertThat( arc.getType() ).isEqualTo( ArcType.OPEN );

		javafx.scene.transform.Rotate fxRotate = (javafx.scene.transform.Rotate)arc.getTransforms().getFirst();
		assertThat( fxRotate.getAngle() ).isEqualTo( rotate );
		assertThat( fxRotate.getPivotX() ).isEqualTo( arc.getCenterX() );
		assertThat( fxRotate.getPivotY() ).isEqualTo( arc.getCenterY() );

		assertThat( arc.getStrokeWidth() ).isEqualTo( 0.05, TOLERANCE );
		assertThat( arc.getStrokeDashArray() ).isEmpty();
	}

	@Test
	void toFxShapeWithArcAndRotateAndScale() {
		double rotate = 2.0;
		DesignArc designArc = new DesignArc( new Point3D( 3, 5, 0 ), new Point3D( 1, 1, 0 ), rotate, 0.0, 90.0, DesignArc.Type.OPEN );
		designArc.setDrawPattern( "0.3" );

		Arc arc = (Arc)CadGeometry.toFxShape( designArc, 1.1 );
		assertThat( arc.getCenterX() ).isEqualTo( 3.3, TOLERANCE );
		assertThat( arc.getCenterY() ).isEqualTo( 5.5, TOLERANCE );
		assertThat( arc.getRadiusX() ).isEqualTo( 1.1, TOLERANCE );
		assertThat( arc.getRadiusY() ).isEqualTo( 1.1, TOLERANCE );
		assertThat( arc.getStartAngle() ).isEqualTo( 0 );
		assertThat( arc.getLength() ).isEqualTo( -90 );
		assertThat( arc.getType() ).isEqualTo( ArcType.OPEN );

		javafx.scene.transform.Rotate fxRotate = (javafx.scene.transform.Rotate)arc.getTransforms().getFirst();
		assertThat( fxRotate.getAngle() ).isEqualTo( rotate );
		assertThat( fxRotate.getPivotX() ).isEqualTo( arc.getCenterX() );
		assertThat( fxRotate.getPivotY() ).isEqualTo( arc.getCenterY() );

		assertThat( arc.getStrokeWidth() ).isEqualTo( 0.055, TOLERANCE );
		assertThat( arc.getStrokeDashArray() ).usingComparatorForType( new DoubleComparator( TOLERANCE.value ), Double.class ).containsExactly( 0.33 );
	}

	@Test
	void toFxShapeWithEllipse() {
		double rotate = 0.0;

		Ellipse ellipse = (Ellipse)CadGeometry.toFxShape( new DesignEllipse( new Point3D( 3, 5, 0 ), new Point3D( 1, 1, 0 ), rotate ) );
		assertThat( ellipse.getCenterX() ).isEqualTo( 3 );
		assertThat( ellipse.getCenterY() ).isEqualTo( 5 );
		assertThat( ellipse.getRadiusX() ).isEqualTo( 1 );
		assertThat( ellipse.getRadiusY() ).isEqualTo( 1 );

		assertThat( ellipse.getTransforms() ).isEmpty();

		assertThat( ellipse.getStrokeWidth() ).isEqualTo( 0.05, TOLERANCE );
	}

	@Test
	void toFxShapeWithEllipseAndScale() {
		double rotate = 0.0;
		DesignEllipse designEllipse = new DesignEllipse( new Point3D( 3, 5, 0 ), new Point3D( 1, 1, 0 ), rotate );
		designEllipse.setDrawPattern( "0.11" );

		Ellipse ellipse = (Ellipse)CadGeometry.toFxShape( designEllipse, 1.3 );
		assertThat( ellipse.getCenterX() ).isEqualTo( 3.9, TOLERANCE );
		assertThat( ellipse.getCenterY() ).isEqualTo( 6.5, TOLERANCE );
		assertThat( ellipse.getRadiusX() ).isEqualTo( 1.3, TOLERANCE );
		assertThat( ellipse.getRadiusY() ).isEqualTo( 1.3, TOLERANCE );

		assertThat( ellipse.getTransforms() ).isEmpty();

		assertThat( ellipse.getStrokeWidth() ).isEqualTo( 0.065, TOLERANCE );
		assertThat( ellipse.getStrokeDashArray() ).usingComparatorForType( new DoubleComparator( TOLERANCE.value ), Double.class ).containsExactly( 0.143 );
	}

	@Test
	void toFxShapeWithEllipseAndRotate() {
		double rotate = 1.0;

		Ellipse ellipse = (Ellipse)CadGeometry.toFxShape( new DesignEllipse( new Point3D( 3, 5, 0 ), new Point3D( 1, 1, 0 ), rotate ) );
		assertThat( ellipse.getCenterX() ).isEqualTo( 3 );
		assertThat( ellipse.getCenterY() ).isEqualTo( 5 );
		assertThat( ellipse.getRadiusX() ).isEqualTo( 1 );
		assertThat( ellipse.getRadiusY() ).isEqualTo( 1 );

		javafx.scene.transform.Rotate fxRotate = (javafx.scene.transform.Rotate)ellipse.getTransforms().getFirst();
		assertThat( fxRotate.getAngle() ).isEqualTo( rotate );
		assertThat( fxRotate.getPivotX() ).isEqualTo( 3 );
		assertThat( fxRotate.getPivotY() ).isEqualTo( 5 );

		assertThat( ellipse.getStrokeWidth() ).isEqualTo( 0.05, TOLERANCE );
	}

	@Test
	void toFxShapeWithEllipseAndRotateAndScale() {
		double rotate = 1.0;
		DesignEllipse designEllipse = new DesignEllipse( new Point3D( 3, 5, 0 ), new Point3D( 1, 1, 0 ), rotate );

		Ellipse ellipse = (Ellipse)CadGeometry.toFxShape( designEllipse, 1.7 );
		assertThat( ellipse.getCenterX() ).isEqualTo( 5.1 );
		assertThat( ellipse.getCenterY() ).isEqualTo( 8.5 );
		assertThat( ellipse.getRadiusX() ).isEqualTo( 1.7 );
		assertThat( ellipse.getRadiusY() ).isEqualTo( 1.7 );

		javafx.scene.transform.Rotate fxRotate = (javafx.scene.transform.Rotate)ellipse.getTransforms().getFirst();
		assertThat( fxRotate.getAngle() ).isEqualTo( rotate );
		assertThat( fxRotate.getPivotX() ).isEqualTo( ellipse.getCenterX() );
		assertThat( fxRotate.getPivotY() ).isEqualTo( ellipse.getCenterY() );

		assertThat( ellipse.getStrokeWidth() ).isEqualTo( 0.085, TOLERANCE );
	}

	// Quad
	@Test
	void toFxShapeWithQuad() {}

	@Test
	void toFxShapeWithQuadAndScale() {}

	// Cubic
	@Test
	void toFxShapeWithCubic() {}

	@Test
	void toFxShapeWithCubicAndScale() {}

	// Marker
	@Test
	void toFxShapeWithMarker() {
		DesignMarker designMarker = new DesignMarker( new Point3D( 3, 5, 0 ) );

		Path path = (Path)CadGeometry.toFxShape( designMarker );
		assertThat( path.getElements() ).hasSize( 13 );

		// NOTE Markers are weird! They are not a single shape, but a collection of shapes.
		//assertThat( path.getElements().getFirst() ).isEqualTo( new MoveTo( 3, 5 ) );
	}

	@Test
	void toFxShapeWithMarkerAndScale() {
		DesignMarker designMarker = new DesignMarker( new Point3D( 3, 5, 0 ) );

		Path path = (Path)CadGeometry.toFxShape( designMarker, 2.9 );
		assertThat( path.getElements() ).hasSize( 13 );

		// NOTE Markers are weird! They are not a single shape, but a collection of shapes.
		//assertThat( path.getElements().getFirst() ).isEqualTo( new MoveTo( 8.7, 14.5 ) );
	}

	// Path
	@Test
	void toFxShapeWithPath() {
		DesignPath designPath = new DesignPath( new Point3D( 3, 5, 0 ) ).line( 4, 5 ).line( 4, 6 ).line( 3, 6 ).close();

		Path path = (Path)CadGeometry.toFxShape( designPath );

		FxPathElementAssert.assertThat( path.getElements().getFirst() ).isEqualTo( new MoveTo( 3, 5 ) );
		FxPathElementAssert.assertThat( path.getElements().get( 1 ) ).isEqualTo( new LineTo( 4, 5 ) );
		FxPathElementAssert.assertThat( path.getElements().get( 2 ) ).isEqualTo( new LineTo( 4, 6 ) );
		FxPathElementAssert.assertThat( path.getElements().get( 3 ) ).isEqualTo( new LineTo( 3, 6 ) );
		FxPathElementAssert.assertThat( path.getElements().get( 4 ) ).isEqualTo( new ClosePath() );
		assertThat( path.getElements() ).hasSize( 5 );
	}

	@Test
	void toFxShapeWithPathAndScale() {
		DesignPath designPath = new DesignPath( new Point3D( 3, 5, 0 ) ).line( 4, 5 ).line( 4, 6 ).line( 3, 6 ).close();

		Path path = (Path)CadGeometry.toFxShape( designPath, 4.7 );
		FxPathElementAssert.assertThat( path.getElements().getFirst() ).isEqualTo( new MoveTo( 14.1, 23.5 ), TOLERANCE );
		FxPathElementAssert.assertThat( path.getElements().get( 1 ) ).isEqualTo( new LineTo( 18.8, 23.5 ), TOLERANCE );
		FxPathElementAssert.assertThat( path.getElements().get( 2 ) ).isEqualTo( new LineTo( 18.8, 28.2 ), TOLERANCE );
		FxPathElementAssert.assertThat( path.getElements().get( 3 ) ).isEqualTo( new LineTo( 14.1, 28.2 ), TOLERANCE );
		FxPathElementAssert.assertThat( path.getElements().get( 4 ) ).isEqualTo( new ClosePath() );
		assertThat( path.getElements() ).hasSize( 5 );
	}

	// Text
	@Test
	void toFxShapeWithText() {
		Text text = (Text)CadGeometry.toFxShape( new DesignText( new Point3D( 3, 5, 0 ), "Hello, World!" ) );
		assertThat( text.getX() ).isEqualTo( 3 );
		assertThat( text.getY() ).isEqualTo( 5 );
		assertThat( text.getText() ).isEqualTo( "Hello, World!" );

		assertThat( text.getTransforms() ).isEmpty();

		assertThat( text.getStrokeWidth() ).isEqualTo( 0.05, TOLERANCE );
	}

	@Test
	void toFxShapeWithTextAndRotate() {
		double rotate = 48.0;

		Text text = (Text)CadGeometry.toFxShape( new DesignText( new Point3D( 3, 5, 0 ), "Hello, World!", String.valueOf( rotate ) ) );
		assertThat( text.getX() ).isEqualTo( 3, TOLERANCE );
		assertThat( text.getY() ).isEqualTo( 5, TOLERANCE );
		assertThat( text.getText() ).isEqualTo( "Hello, World!" );

		javafx.scene.transform.Rotate fxRotate = (javafx.scene.transform.Rotate)text.getTransforms().getFirst();
		assertThat( fxRotate.getAngle() ).isEqualTo( rotate );
		assertThat( fxRotate.getPivotX() ).isEqualTo( text.getX() );
		assertThat( fxRotate.getPivotY() ).isEqualTo( text.getY() );

		assertThat( text.getStrokeWidth() ).isEqualTo( 0.05, TOLERANCE );
	}

	@Test
	void toFxShapeWithTextAndScale() {
		Text text = (Text)CadGeometry.toFxShape( new DesignText( new Point3D( 3, 5, 0 ), "Hello, World!" ), 2.7 );
		assertThat( text.getX() ).isEqualTo( 8.1, TOLERANCE );
		assertThat( text.getY() ).isEqualTo( 13.5, TOLERANCE );
		assertThat( text.getText() ).isEqualTo( "Hello, World!" );

		assertThat( text.getTransforms() ).isEmpty();

		assertThat( text.getStrokeWidth() ).isEqualTo( 0.135, TOLERANCE );
		assertThat( text.getFont().getSize() ).isEqualTo( 2.7, LOOSE_TOLERANCE );
	}

	@Test
	void toFxShapeWithTextAndRotateAndScale() {
		double rotate = 48.0;

		Text text = (Text)CadGeometry.toFxShape( new DesignText( new Point3D( 3, 5, 0 ), "Hello, World!", String.valueOf( rotate ) ), 2.7 );
		assertThat( text.getX() ).isEqualTo( 8.1, TOLERANCE );
		assertThat( text.getY() ).isEqualTo( 13.5, TOLERANCE );
		assertThat( text.getText() ).isEqualTo( "Hello, World!" );

		javafx.scene.transform.Rotate fxRotate = (javafx.scene.transform.Rotate)text.getTransforms().getFirst();
		assertThat( fxRotate.getAngle() ).isEqualTo( rotate );
		assertThat( fxRotate.getPivotX() ).isEqualTo( text.getX() );
		assertThat( fxRotate.getPivotY() ).isEqualTo( text.getY() );

		assertThat( text.getStrokeWidth() ).isEqualTo( 0.135, TOLERANCE );
		assertThat( text.getFont().getSize() ).isEqualTo( 2.7, LOOSE_TOLERANCE );
	}

}
