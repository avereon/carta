package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.CadShapes;
import com.avereon.curve.math.Arithmetic;
import com.avereon.marea.Pen;
import com.avereon.marea.fx.FxRenderer2d;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.List;

@CustomLog
public class GridOrthographic implements Grid {

	private static final double PIXEL_THRESHOLD = 5;

	@Override
	public String name() {
		return "ORTHO";
	}

	@Override
	public Point3D getNearest( DesignWorkplane workplane, Point3D point ) {
		Point3D origin = CadShapes.parsePoint( workplane.getOrigin() );
		point = point.subtract( origin );
		double x = Arithmetic.nearest( point.getX(), workplane.calcSnapGridX() );
		double y = Arithmetic.nearest( point.getY(), workplane.calcSnapGridY() );
		double z = Arithmetic.nearest( point.getZ(), workplane.calcSnapGridZ() );
		point = new Point3D( x, y, z );
		point = point.add( origin );
		return point;
	}

	@Override
	public void drawMareaGeometryGrid( FxRenderer2d renderer, DesignWorkplane workplane ) {
		switch( workplane.getGridStyle() ) {
			case DOT -> drawMareaGridDots1( renderer, workplane );
			case LINE -> drawMareaGridLines( renderer, workplane );
		}
	}

	private void drawMareaGridDots2( FxRenderer2d renderer, DesignWorkplane workplane ) {
		// TODO Try a new implementation with dashed lines
	}

	private void drawMareaGridDots1( FxRenderer2d renderer, DesignWorkplane workplane ) {
		// TODO Can performance be improved by caching some things, the the pens?
		// TODO Can improve performance by multi-threading the calculations?
		// TODO Can improve performance by drawing the dots as dashed lines?

		Point2D parentZero = renderer.parentToLocal( Point2D.ZERO ).add( workplane.calcSnapGridX(), -workplane.calcSnapGridY() );

		Point3D origin = CadShapes.parsePoint( workplane.getOrigin() );
		double boundaryX1 = Math.min( workplane.getBoundaryX1(), workplane.getBoundaryX2() );
		double boundaryX2 = Math.max( workplane.getBoundaryX1(), workplane.getBoundaryX2() );
		double boundaryY1 = Math.min( workplane.getBoundaryY1(), workplane.getBoundaryY2() );
		double boundaryY2 = Math.max( workplane.getBoundaryY1(), workplane.getBoundaryY2() );

		boolean axisVisible = workplane.isGridAxisVisible();

		boolean majorVisible = workplane.isMajorGridShowing() && workplane.isMajorGridVisible();
		double majorIntervalX = workplane.calcMajorGridX();
		double majorIntervalY = workplane.calcMajorGridY();

		boolean minorVisible = workplane.isMinorGridShowing() && workplane.isMinorGridVisible();
		double minorIntervalX = workplane.calcMinorGridX();
		double minorIntervalY = workplane.calcMinorGridY();

		double snapIntervalX = workplane.calcSnapGridX();
		double snapIntervalY = workplane.calcSnapGridY();

		// Get all offsets
		List<Double> axisOffsetsX = new ArrayList<>();
		List<Double> axisOffsetsY = new ArrayList<>();
		if( origin.getX() >= boundaryX1 && origin.getX() <= boundaryX2 ) axisOffsetsX.add( origin.getX() );
		if( origin.getY() >= boundaryY1 && origin.getY() <= boundaryY2 ) axisOffsetsY.add( origin.getY() );
		List<Double> majorOffsetsX = Grid.getOffsets( origin.getX(), majorIntervalX, boundaryX1, boundaryX2 );
		List<Double> majorOffsetsY = Grid.getOffsets( origin.getY(), majorIntervalY, boundaryY1, boundaryY2 );
		List<Double> minorOffsetsX = Grid.getOffsets( origin.getX(), minorIntervalX, boundaryX1, boundaryX2 );
		List<Double> minorOffsetsY = Grid.getOffsets( origin.getY(), minorIntervalY, boundaryY1, boundaryY2 );

		List<Double> majorSpacingOffsetsX = null;
		List<Double> majorSpacingOffsetsY = null;
		List<Double> axisSpacingOffsetsX = null;
		List<Double> axisSpacingOffsetsY = null;

		// Draw the minor grid first so the major grid paints over it
		if( minorVisible ) {
			Point2D minorGridPixels = renderer.localToParent( parentZero.add( workplane.calcSnapGridX(), -workplane.calcSnapGridY() ) );
			boolean allowMinorGrid = minorGridPixels.getX() >= PIXEL_THRESHOLD && minorGridPixels.getY() >= PIXEL_THRESHOLD;
			List<Double> snapSpacingOffsetsX = Grid.getOffsets( origin.getX(), snapIntervalX, boundaryX1, boundaryX2 );
			List<Double> snapSpacingOffsetsY = Grid.getOffsets( origin.getY(), snapIntervalY, boundaryY1, boundaryY2 );
			if( allowMinorGrid ) {
				Pen pen = new Pen( workplane.calcMinorGridPaint(), workplane.calcMinorGridWidth() );
				for( double valueX : minorOffsetsX ) {
					for( double valueY : snapSpacingOffsetsY ) {
						renderer.setDrawPen( pen );
						renderer.drawLine( valueX, valueY, valueX, valueY );
					}
				}
				for( double valueX : snapSpacingOffsetsX ) {
					for( double valueY : minorOffsetsY ) {
						renderer.setDrawPen( pen );
						renderer.drawLine( valueX, valueY, valueX, valueY );
					}
				}
			}
			majorSpacingOffsetsX = snapSpacingOffsetsX;
			majorSpacingOffsetsY = snapSpacingOffsetsY;
			axisSpacingOffsetsX = snapSpacingOffsetsX;
			axisSpacingOffsetsY = snapSpacingOffsetsY;
		}

		// Draw the major grid next so the grid axes paint over it
		if( majorVisible ) {
			Pen pen = new Pen( workplane.calcMajorGridPaint(), workplane.calcMajorGridWidth() );
			Point2D majorGridPixels = renderer.localToParent( parentZero.add( workplane.calcMinorGridX(), -workplane.calcMinorGridY() ) );
			boolean allowMajorGrid = majorGridPixels.getX() >= PIXEL_THRESHOLD && majorGridPixels.getY() >= PIXEL_THRESHOLD;
			if( majorSpacingOffsetsX == null ) majorSpacingOffsetsX = majorOffsetsX;
			if( majorSpacingOffsetsY == null ) majorSpacingOffsetsY = minorOffsetsY;
			if( allowMajorGrid ) {
				for( double valueX : majorOffsetsX ) {
					for( double valueY : majorSpacingOffsetsY ) {
						renderer.setDrawPen( pen );
						renderer.drawLine( valueX, valueY, valueX, valueY );
					}
				}
				for( double valueX : majorSpacingOffsetsX ) {
					for( double valueY : majorOffsetsY ) {
						renderer.setDrawPen( pen );
						renderer.drawLine( valueX, valueY, valueX, valueY );
					}
				}
			}
			if( axisSpacingOffsetsX == null ) axisSpacingOffsetsX = minorOffsetsX;
			if( axisSpacingOffsetsY == null ) axisSpacingOffsetsY = minorOffsetsY;
		}

		// The grid axes are painted last
		if( axisVisible ) {
			Pen pen = new Pen( workplane.calcGridAxisPaint(), workplane.calcGridAxisWidth() );
			Point2D axisGridPixels = renderer.localToParent( parentZero.add( workplane.calcMajorGridX(), -workplane.calcMajorGridY() ) );
			boolean allowAxisGrid = axisGridPixels.getX() >= PIXEL_THRESHOLD && axisGridPixels.getY() >= PIXEL_THRESHOLD;
			if( axisSpacingOffsetsX == null ) axisSpacingOffsetsX = majorOffsetsX;
			if( axisSpacingOffsetsY == null ) axisSpacingOffsetsY = majorOffsetsY;
			if( allowAxisGrid ) {
				for( double valueX : axisOffsetsX ) {
					for( double valueY : axisSpacingOffsetsY ) {
						renderer.setDrawPen( pen );
						renderer.drawLine( valueX, valueY, valueX, valueY );
					}
				}
				for( double valueX : axisSpacingOffsetsX ) {
					for( double valueY : axisOffsetsY ) {
						renderer.setDrawPen( pen );
						renderer.drawLine( valueX, valueY, valueX, valueY );
					}
				}
			} else {
				for( double value : axisOffsetsX ) {
					renderer.setDrawPen( pen );
					renderer.drawLine( boundaryX1, value, boundaryX2, value );
				}
				for( double value : axisOffsetsY ) {
					renderer.setDrawPen( pen );
					renderer.drawLine( boundaryX1, value, boundaryX2, value );
				}
			}
		}
	}

	private void drawMareaGridLines( FxRenderer2d renderer, DesignWorkplane workplane ) {
		// TODO Can performance be improved by caching some things, the the pens

		Point2D parentZero = renderer.parentToLocal( Point2D.ZERO ).add( workplane.calcSnapGridX(), -workplane.calcSnapGridY() );

		Point3D origin = CadShapes.parsePoint( workplane.getOrigin() );
		double boundaryX1 = Math.min( workplane.getBoundaryX1(), workplane.getBoundaryX2() );
		double boundaryX2 = Math.max( workplane.getBoundaryX1(), workplane.getBoundaryX2() );
		double boundaryY1 = Math.min( workplane.getBoundaryY1(), workplane.getBoundaryY2() );
		double boundaryY2 = Math.max( workplane.getBoundaryY1(), workplane.getBoundaryY2() );

		boolean axisVisible = workplane.isGridAxisVisible();

		boolean majorVisible = workplane.isMajorGridShowing() && workplane.isMajorGridVisible();
		double majorIntervalX = workplane.calcMajorGridX();
		double majorIntervalY = workplane.calcMajorGridY();

		boolean minorVisible = workplane.isMinorGridShowing() && workplane.isMinorGridVisible();
		double minorIntervalX = workplane.calcMinorGridX();
		double minorIntervalY = workplane.calcMinorGridY();

		// Get all offsets
		List<Double> axisOffsetsX = new ArrayList<>();
		List<Double> axisOffsetsY = new ArrayList<>();
		if( origin.getX() >= boundaryX1 && origin.getX() <= boundaryX2 ) axisOffsetsX.add( origin.getX() );
		if( origin.getY() >= boundaryY1 && origin.getY() <= boundaryY2 ) axisOffsetsY.add( origin.getY() );
		List<Double> majorOffsetsX = Grid.getOffsets( origin.getX(), majorIntervalX, boundaryX1, boundaryX2 );
		List<Double> majorOffsetsY = Grid.getOffsets( origin.getY(), majorIntervalY, boundaryY1, boundaryY2 );
		List<Double> minorOffsetsX = Grid.getOffsets( origin.getX(), minorIntervalX, boundaryX1, boundaryX2 );
		List<Double> minorOffsetsY = Grid.getOffsets( origin.getY(), minorIntervalY, boundaryY1, boundaryY2 );

		// Check for offset conflicts
		if( majorVisible ) {
			minorOffsetsX.removeIf( value -> Grid.isNearAny( value, majorOffsetsX ) );
			minorOffsetsY.removeIf( value -> Grid.isNearAny( value, majorOffsetsY ) );
		}
		if( axisVisible ) {
			majorOffsetsX.removeIf( value -> Grid.isNearAny( value, axisOffsetsX ) );
			majorOffsetsY.removeIf( value -> Grid.isNearAny( value, axisOffsetsY ) );
		}

		// Draw the minor grid first so the major grid paints over it
		if( minorVisible ) {
			Pen pen = new Pen( workplane.calcMinorGridPaint(), workplane.calcMinorGridWidth() );
			Point2D minorGridPixels = renderer.localToParent( parentZero.add( workplane.calcMinorGridX(), -workplane.calcMinorGridY() ) );
			boolean allowMinorGrid = minorGridPixels.getX() >= PIXEL_THRESHOLD && minorGridPixels.getY() >= PIXEL_THRESHOLD;
			if( allowMinorGrid ) {
				for( double value : minorOffsetsX ) {
					renderer.draw( new com.avereon.marea.geom.Line( value, boundaryY1, value, boundaryY2 ), pen );
				}
				for( double value : minorOffsetsY ) {
					renderer.draw( new com.avereon.marea.geom.Line( boundaryX1, value, boundaryX2, value ), pen );
				}
			}
		}

		// Draw the major grid next so the grid axes paint over it
		if( majorVisible ) {
			Pen pen = new Pen( workplane.calcMajorGridPaint(), workplane.calcMajorGridWidth() );
			Point2D majorGridPixels = renderer.localToParent( parentZero.add( workplane.calcMajorGridX(), -workplane.calcMajorGridY() ) );
			boolean allowMajorGrid = majorGridPixels.getX() >= PIXEL_THRESHOLD && majorGridPixels.getY() >= PIXEL_THRESHOLD;
			if( allowMajorGrid ) {
				for( double value : majorOffsetsX ) {
					renderer.draw( new com.avereon.marea.geom.Line( value, boundaryY1, value, boundaryY2 ), pen );
				}
				for( double value : majorOffsetsY ) {
					renderer.draw( new com.avereon.marea.geom.Line( boundaryX1, value, boundaryX2, value ), pen );
				}
			}
		}

		// The grid axes are painted last
		if( axisVisible ) {
			Pen pen = new Pen( workplane.calcGridAxisPaint(), workplane.calcGridAxisWidth() );
			for( double value : axisOffsetsX ) {
				renderer.draw( new com.avereon.marea.geom.Line( value, boundaryY1, value, boundaryY2 ), pen );
			}
			for( double value : axisOffsetsY ) {
				renderer.draw( new com.avereon.marea.geom.Line( boundaryX1, value, boundaryX2, value ), pen );
			}
		}
	}

	@Override
	@Deprecated
	public List<Shape> createFxGeometryGrid( DesignWorkplane workplane ) {
		return switch( workplane.getGridStyle() ) {
			case DOT -> createFxGridDots( workplane );
			case LINE -> createFxGridLines( workplane );
		};
	}

	@Deprecated
	private List<Shape> createFxGridDots( DesignWorkplane workplane ) {
		// It is not practical to create a dotted grid with FX shapes
		return List.of();
	}

	@Deprecated
	private List<Shape> createFxGridLines( DesignWorkplane workplane ) {
		List<Shape> grid = new ArrayList<>();

		Point3D origin = CadShapes.parsePoint( workplane.getOrigin() );
		double boundaryX1 = Math.min( workplane.getBoundaryX1(), workplane.getBoundaryX2() );
		double boundaryX2 = Math.max( workplane.getBoundaryX1(), workplane.getBoundaryX2() );
		double boundaryY1 = Math.min( workplane.getBoundaryY1(), workplane.getBoundaryY2() );
		double boundaryY2 = Math.max( workplane.getBoundaryY1(), workplane.getBoundaryY2() );

		boolean axisVisible = workplane.isGridAxisVisible();
		Paint axisPaint = workplane.calcGridAxisPaint();
		double axisWidth = workplane.calcGridAxisWidth();

		boolean majorVisible = workplane.isMajorGridShowing() && workplane.isMajorGridVisible();
		double majorIntervalX = workplane.calcMajorGridX();
		double majorIntervalY = workplane.calcMajorGridY();
		Paint majorPaint = workplane.calcMajorGridPaint();
		double majorWidth = workplane.calcMajorGridWidth();

		boolean minorVisible = workplane.isMinorGridShowing() && workplane.isMinorGridVisible();
		double minorIntervalX = workplane.calcMinorGridX();
		double minorIntervalY = workplane.calcMinorGridY();
		Paint minorPaint = workplane.calcMinorGridPaint();
		double minorWidth = workplane.calcMinorGridWidth();

		// Get all offsets
		List<Double> axisOffsetsX = new ArrayList<>();
		List<Double> axisOffsetsY = new ArrayList<>();
		if( origin.getX() >= boundaryX1 && origin.getX() <= boundaryX2 ) axisOffsetsX.add( origin.getX() );
		if( origin.getY() >= boundaryY1 && origin.getY() <= boundaryY2 ) axisOffsetsY.add( origin.getY() );
		List<Double> majorOffsetsX = Grid.getOffsets( origin.getX(), majorIntervalX, boundaryX1, boundaryX2 );
		List<Double> majorOffsetsY = Grid.getOffsets( origin.getY(), majorIntervalY, boundaryY1, boundaryY2 );
		List<Double> minorOffsetsX = Grid.getOffsets( origin.getX(), minorIntervalX, boundaryX1, boundaryX2 );
		List<Double> minorOffsetsY = Grid.getOffsets( origin.getY(), minorIntervalY, boundaryY1, boundaryY2 );

		// Check for conflicts
		if( majorVisible ) {
			minorOffsetsX.removeIf( value -> Grid.isNearAny( value, majorOffsetsX ) );
			minorOffsetsY.removeIf( value -> Grid.isNearAny( value, majorOffsetsY ) );
		}
		if( axisVisible ) {
			majorOffsetsX.removeIf( value -> Grid.isNearAny( value, axisOffsetsX ) );
			majorOffsetsY.removeIf( value -> Grid.isNearAny( value, axisOffsetsY ) );
		}

		if( minorVisible ) {
			for( double value : minorOffsetsX ) {
				Line shape = new Line( value, boundaryY1, value, boundaryY2 );
				shape.setStroke( minorPaint );
				shape.setStrokeWidth( minorWidth );
				grid.add( shape );
			}
			for( double value : minorOffsetsY ) {
				Line shape = new Line( boundaryX1, value, boundaryX2, value );
				shape.setStroke( minorPaint );
				shape.setStrokeWidth( minorWidth );
				grid.add( shape );
			}
		}

		if( majorVisible ) {
			for( double value : majorOffsetsX ) {
				Line shape = new Line( value, boundaryY1, value, boundaryY2 );
				shape.setStroke( majorPaint );
				shape.setStrokeWidth( majorWidth );
				grid.add( shape );
			}
			for( double value : majorOffsetsY ) {
				Line shape = new Line( boundaryX1, value, boundaryX2, value );
				shape.setStroke( majorPaint );
				shape.setStrokeWidth( majorWidth );
				grid.add( shape );
			}
		}

		if( axisVisible ) {
			for( double value : axisOffsetsX ) {
				Line shape = new Line( value, boundaryY1, value, boundaryY2 );
				shape.setStroke( axisPaint );
				shape.setStrokeWidth( axisWidth );
				grid.add( shape );
			}
			for( double value : axisOffsetsY ) {
				Line shape = new Line( boundaryX1, value, boundaryX2, value );
				shape.setStroke( axisPaint );
				shape.setStrokeWidth( axisWidth );
				grid.add( shape );
			}
		}

		return grid;
	}

}
