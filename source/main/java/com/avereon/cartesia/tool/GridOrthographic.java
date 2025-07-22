package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.CadShapes;
import com.avereon.curve.math.Arithmetic;
import com.avereon.marea.LineCap;
import com.avereon.marea.LineJoin;
import com.avereon.marea.Pen;
import com.avereon.marea.fx.FxRenderer2d;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import lombok.CustomLog;

import java.util.*;
import java.util.stream.Collectors;

@CustomLog
public class GridOrthographic implements Grid {

	private static final double GRID_THRESHOLD = 5;

	private static final double PIXEL_THRESHOLD = GRID_THRESHOLD;

	@Override
	public String name() {
		return "ORTHO";
	}

	@Override
	public Point3D getNearest( Workplane workplane, Point3D point ) {
		Point3D origin = workplane.calcOrigin();
		point = point.subtract( origin );
		double x = Arithmetic.nearest( point.getX(), workplane.calcSnapGridX() );
		double y = Arithmetic.nearest( point.getY(), workplane.calcSnapGridY() );
		point = new Point3D( x, y, 0 );
		point = point.add( origin );
		return point;
	}

	public Collection<Shape> createFxGeometryGrid( Workplane workplane, double scale ) {
		if( workplane == null ) return Collections.emptyList();
		return updateFxGeometryGrid( workplane, scale, FXCollections.observableArrayList() );
	}

	public Collection<Shape> updateFxGeometryGrid( Workplane workplane, double scale, ObservableList<Node> existing ) {
		if( workplane == null ) return Collections.emptyList();

		// Map the existing geometry from the node list to a collection
		Set<Line> prior = existing.stream().map( n -> (Line)n ).collect( Collectors.toCollection( HashSet::new ) );

		// This will become the collection of grid geometry after the update
		Set<Shape> grid = new HashSet<>();

		Point3D origin = CadShapes.parsePoint( workplane.getOrigin() );
		double originX = origin.getX() * scale;
		double originY = origin.getY() * scale;

		boolean axisVisible = workplane.isGridAxisVisible();
		Paint axisPaint = workplane.calcGridAxisPaint();
		double axisWidth = workplane.calcGridAxisWidth() * scale;
		axisWidth = 2.0;

		boolean majorVisible = workplane.isMajorGridShowing() && workplane.isMajorGridVisible();
		double majorIntervalX = workplane.calcMajorGridX() * scale;
		double majorIntervalY = workplane.calcMajorGridY() * scale;
		Paint majorPaint = workplane.calcMajorGridPaint();
		double majorWidth = workplane.calcMajorGridWidth() * scale;
		majorWidth = 1.0;

		boolean minorVisible = workplane.isMinorGridShowing() && workplane.isMinorGridVisible();
		double minorIntervalX = workplane.calcMinorGridX() * scale;
		double minorIntervalY = workplane.calcMinorGridY() * scale;
		Paint minorPaint = workplane.calcMinorGridPaint();
		double minorWidth = workplane.calcMinorGridWidth() * scale;
		minorWidth = 0.5;

		double snapIntervalX = workplane.calcSnapGridX() * scale;
		double snapIntervalY = workplane.calcSnapGridY() * scale;

		double boundaryX1 = Grid.getBoundaryX1( workplane.getBoundaryX1(), workplane.getBoundaryX2(), scale, majorIntervalX );
		double boundaryX2 = Grid.getBoundaryX2( workplane.getBoundaryX1(), workplane.getBoundaryX2(), scale, majorIntervalX );
		double boundaryY1 = Grid.getBoundaryY1( workplane.getBoundaryY1(), workplane.getBoundaryY2(), scale, majorIntervalY );
		double boundaryY2 = Grid.getBoundaryY2( workplane.getBoundaryY1(), workplane.getBoundaryY2(), scale, majorIntervalY );

		// Get all offsets
		List<Double> axisOffsetsX = new ArrayList<>();
		List<Double> axisOffsetsY = new ArrayList<>();
		if( originX >= boundaryX1 && originX <= boundaryX2 ) axisOffsetsX.add( originX );
		if( originY >= boundaryY1 && originY <= boundaryY2 ) axisOffsetsY.add( originY );
		List<Double> majorOffsetsX = Grid.getOffsets( originX, majorIntervalX, boundaryX1, boundaryX2 );
		List<Double> majorOffsetsY = Grid.getOffsets( originY, majorIntervalY, boundaryY1, boundaryY2 );
		List<Double> minorOffsetsX = Grid.getOffsets( originX, minorIntervalX, boundaryX1, boundaryX2 );
		List<Double> minorOffsetsY = Grid.getOffsets( originY, minorIntervalY, boundaryY1, boundaryY2 );

		double majorBoundaryX1 = majorOffsetsX.getFirst();
		double majorBoundaryX2 = majorOffsetsX.getLast();
		double majorBoundaryY1 = majorOffsetsY.getFirst();
		double majorBoundaryY2 = majorOffsetsY.getLast();

		// Check for conflicts
		if( workplane.getGridStyle() == GridStyle.LINE ) {
			if( majorVisible ) {
				minorOffsetsX.removeIf( value -> Grid.isNearAny( value, majorOffsetsX ) );
				minorOffsetsY.removeIf( value -> Grid.isNearAny( value, majorOffsetsY ) );
			}
			if( axisVisible ) {
				majorOffsetsX.removeIf( value -> Grid.isNearAny( value, axisOffsetsX ) );
				majorOffsetsY.removeIf( value -> Grid.isNearAny( value, axisOffsetsY ) );
			}
		}

		if( minorVisible ) {
			// Grid style
			double dashOffset = 0.0;
			Double[] dashSpacingX = new Double[]{};
			Double[] dashSpacingY = new Double[]{};
			if( workplane.getGridStyle() == GridStyle.CROSS ) {
				dashOffset = 0.25 * minorIntervalX;
				dashSpacingX = new Double[]{ 0.5 * minorIntervalX, 0.5 * minorIntervalX };
				dashSpacingY = new Double[]{ 0.5 * minorIntervalY, 0.5 * minorIntervalY };
			} else if( workplane.getGridStyle() == GridStyle.DOT ) {
				dashSpacingX = new Double[]{ 0.0, snapIntervalX };
				dashSpacingY = new Double[]{ 0.0, snapIntervalY };
			}

			// Lines
			for( double value : minorOffsetsX ) {
				Line shape = Grid.reuseOrNewLine( prior, value, majorBoundaryY1, value, majorBoundaryY2 );
				shape.setStroke( minorPaint );
				shape.setStrokeWidth( minorWidth );
				shape.setStrokeDashOffset( dashOffset );
				shape.getStrokeDashArray().setAll( dashSpacingY );
				grid.add( shape );
			}
			for( double value : minorOffsetsY ) {
				Line shape = Grid.reuseOrNewLine( prior, majorBoundaryX1, value, majorBoundaryX2, value );
				shape.setStroke( minorPaint );
				shape.setStrokeWidth( minorWidth );
				shape.setStrokeDashOffset( dashOffset );
				shape.getStrokeDashArray().setAll( dashSpacingX );
				grid.add( shape );
			}
		}

		if( majorVisible ) {
			// Grid style
			double dashOffset = 0.0;
			Double[] dashSpacingX = new Double[]{};
			Double[] dashSpacingY = new Double[]{};
			if( workplane.getGridStyle() == GridStyle.CROSS ) {
				dashOffset = 0.25 * majorIntervalX;
				dashSpacingX = new Double[]{ 0.5 * majorIntervalX, 0.5 * majorIntervalX };
				dashSpacingY = new Double[]{ 0.5 * majorIntervalY, 0.5 * majorIntervalY };
			} else if( workplane.getGridStyle() == GridStyle.DOT ) {
				dashSpacingX = new Double[]{ 0.0, minorIntervalX };
				dashSpacingY = new Double[]{ 0.0, minorIntervalY };
			}

			// Lines
			for( double value : majorOffsetsX ) {
				Line shape = Grid.reuseOrNewLine( prior, value, majorBoundaryY1, value, majorBoundaryY2 );
				shape.setStroke( majorPaint );
				shape.setStrokeWidth( majorWidth );
				shape.setStrokeDashOffset( dashOffset );
				shape.getStrokeDashArray().setAll( dashSpacingY );
				grid.add( shape );
			}
			for( double value : majorOffsetsY ) {
				Line shape = Grid.reuseOrNewLine( prior, majorBoundaryX1, value, majorBoundaryX2, value );
				shape.setStroke( majorPaint );
				shape.setStrokeWidth( majorWidth );
				shape.setStrokeDashOffset( dashOffset );
				shape.getStrokeDashArray().setAll( dashSpacingX );
				grid.add( shape );
			}
		}

		if( axisVisible ) {
			// Grid style
			double dashOffset = 0.0;
			Double[] dashSpacingX = new Double[]{};
			Double[] dashSpacingY = new Double[]{};
			// Match the style of the major grid
			if( workplane.getGridStyle() == GridStyle.CROSS ) {
				dashOffset = 0.25 * majorIntervalX;
				dashSpacingX = new Double[]{ 0.5 * majorIntervalX, 0.5 * majorIntervalX };
				dashSpacingY = new Double[]{ 0.5 * majorIntervalY, 0.5 * majorIntervalY };
			} else if( workplane.getGridStyle() == GridStyle.DOT ) {
				dashSpacingX = new Double[]{ 0.0, majorIntervalX };
				dashSpacingY = new Double[]{ 0.0, majorIntervalY };
			}

			// Lines
			for( double value : axisOffsetsX ) {
				Line shape = Grid.reuseOrNewLine( prior, value, majorBoundaryY1, value, majorBoundaryY2 );
				shape.setStroke( axisPaint );
				shape.setStrokeWidth( axisWidth );
				shape.setStrokeDashOffset( dashOffset );
				shape.getStrokeDashArray().setAll( dashSpacingY );
				grid.add( shape );
			}
			for( double value : axisOffsetsY ) {
				Line shape = Grid.reuseOrNewLine( prior, majorBoundaryX1, value, majorBoundaryX2, value );
				shape.setStroke( axisPaint );
				shape.setStrokeWidth( axisWidth );
				shape.setStrokeDashOffset( dashOffset );
				shape.getStrokeDashArray().setAll( dashSpacingX );
				grid.add( shape );
			}
		}

		existing.removeAll( prior );
		existing.setAll( grid );

		return grid;
	}

	@Override
	public void drawMareaGeometryGrid( FxRenderer2d renderer, Workplane workplane ) {
		switch( workplane.getGridStyle() ) {
			case DOT -> drawMareaGridDots( renderer, workplane );
			case LINE -> drawMareaGridLines( renderer, workplane );
		}
	}

	private void drawMareaGridDots( FxRenderer2d renderer, Workplane workplane ) {
		Point2D originInParent = renderer.parentToLocal( Point2D.ZERO ).add( workplane.calcSnapGridX(), -workplane.calcSnapGridY() );

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

		// Draw the minor grid first so the major grid paints over it
		if( minorVisible ) {
			double[] dashSpacingX = new double[]{ 0, snapIntervalX };
			double[] dashSpacingY = new double[]{ 0, snapIntervalY };

			Point2D minorGridPixels = renderer.localToParent( originInParent.add( workplane.calcSnapGridX(), -workplane.calcSnapGridY() ) );
			boolean allowMinorGrid = Math.abs( minorGridPixels.getX() ) >= PIXEL_THRESHOLD && Math.abs( minorGridPixels.getY() ) >= PIXEL_THRESHOLD;
			if( allowMinorGrid ) {
				renderer.setDrawPen( workplane.calcMinorGridPaint(), workplane.calcMinorGridWidth(), LineCap.ROUND, LineJoin.ROUND, dashSpacingY, 0 );
				for( double valueX : minorOffsetsX ) {
					renderer.drawLine( valueX, 0, valueX, boundaryY2 );
					renderer.drawLine( valueX, 0, valueX, boundaryY1 );
				}
				renderer.setDrawPen( workplane.calcMinorGridPaint(), workplane.calcMinorGridWidth(), LineCap.ROUND, LineJoin.ROUND, dashSpacingX, 0 );
				for( double valueY : minorOffsetsY ) {
					renderer.drawLine( 0, valueY, boundaryX2, valueY );
					renderer.drawLine( 0, valueY, boundaryX1, valueY );
				}
			}
		}

		// Draw the major grid next so the grid axes paint over it
		if( majorVisible ) {
			double[] dashSpacingX = new double[]{ 0, minorIntervalX };
			double[] dashSpacingY = new double[]{ 0, minorIntervalY };
			Point2D majorGridPixels = renderer.localToParent( originInParent.add( workplane.calcMinorGridX(), -workplane.calcMinorGridY() ) );
			boolean allowMajorGrid = Math.abs( majorGridPixels.getX() ) >= PIXEL_THRESHOLD && Math.abs( majorGridPixels.getY() ) >= PIXEL_THRESHOLD;
			if( allowMajorGrid ) {
				renderer.setDrawPen( workplane.calcMajorGridPaint(), workplane.calcMajorGridWidth(), LineCap.ROUND, LineJoin.ROUND, dashSpacingY, 0 );
				for( double valueX : majorOffsetsX ) {
					renderer.drawLine( valueX, 0, valueX, boundaryY2 );
					renderer.drawLine( valueX, 0, valueX, boundaryY1 );
				}
				renderer.setDrawPen( workplane.calcMajorGridPaint(), workplane.calcMajorGridWidth(), LineCap.ROUND, LineJoin.ROUND, dashSpacingX, 0 );
				for( double valueY : majorOffsetsY ) {
					renderer.drawLine( 0, valueY, boundaryX2, valueY );
					renderer.drawLine( 0, valueY, boundaryX1, valueY );
				}
			}
		}

		// The grid axes are painted last
		if( axisVisible ) {
			double[] dashSpacingX = new double[]{ 0, majorIntervalX };
			double[] dashSpacingY = new double[]{ 0, majorIntervalY };
			Point2D axisGridPixels = renderer.localToParent( originInParent.add( workplane.calcMajorGridX(), -workplane.calcMajorGridY() ) );
			boolean allowAxisGrid = Math.abs( axisGridPixels.getX() ) >= PIXEL_THRESHOLD && Math.abs( axisGridPixels.getY() ) >= PIXEL_THRESHOLD;
			if( allowAxisGrid ) {
				renderer.setDrawPen( workplane.calcGridAxisPaint(), workplane.calcMajorGridWidth(), LineCap.ROUND, LineJoin.ROUND, dashSpacingY, 0 );
				for( double valueX : axisOffsetsX ) {
					renderer.drawLine( valueX, 0, valueX, boundaryY2 );
					renderer.drawLine( valueX, 0, valueX, boundaryY1 );
				}
				renderer.setDrawPen( workplane.calcGridAxisPaint(), workplane.calcMajorGridWidth(), LineCap.ROUND, LineJoin.ROUND, dashSpacingX, 0 );
				for( double valueY : axisOffsetsY ) {
					renderer.drawLine( 0, valueY, boundaryX2, valueY );
					renderer.drawLine( 0, valueY, boundaryX1, valueY );
				}
			}
		}
	}

	private void drawMareaGridLines( FxRenderer2d renderer, Workplane workplane ) {
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
			renderer.setDrawPen( new Pen( workplane.calcMinorGridPaint(), workplane.calcMinorGridWidth() ) );
			Point2D minorGridPixels = renderer.localToParent( parentZero.add( workplane.calcMinorGridX(), -workplane.calcMinorGridY() ) );
			boolean allowMinorGrid = minorGridPixels.getX() >= PIXEL_THRESHOLD && minorGridPixels.getY() >= PIXEL_THRESHOLD;
			if( allowMinorGrid ) {
				for( double value : minorOffsetsX ) {
					renderer.drawLine( value, boundaryY1, value, boundaryY2 );
				}
				for( double value : minorOffsetsY ) {
					renderer.drawLine( boundaryX1, value, boundaryX2, value );
				}
			}
		}

		// Draw the major grid next so the grid axes paint over it
		if( majorVisible ) {
			renderer.setDrawPen( new Pen( workplane.calcMajorGridPaint(), workplane.calcMajorGridWidth() ) );
			Point2D majorGridPixels = renderer.localToParent( parentZero.add( workplane.calcMajorGridX(), -workplane.calcMajorGridY() ) );
			boolean allowMajorGrid = majorGridPixels.getX() >= PIXEL_THRESHOLD && majorGridPixels.getY() >= PIXEL_THRESHOLD;
			if( allowMajorGrid ) {
				for( double value : majorOffsetsX ) {
					renderer.drawLine( value, boundaryY1, value, boundaryY2 );
				}
				for( double value : majorOffsetsY ) {
					renderer.drawLine( boundaryX1, value, boundaryX2, value );
				}
			}
		}

		// The grid axes are painted last
		if( axisVisible ) {
			renderer.setDrawPen( new Pen( workplane.calcGridAxisPaint(), workplane.calcGridAxisWidth() ) );
			for( double value : axisOffsetsX ) {
				renderer.drawLine( value, boundaryY1, value, boundaryY2 );
			}
			for( double value : axisOffsetsY ) {
				renderer.drawLine( boundaryX1, value, boundaryX2, value );
			}
		}
	}

}
