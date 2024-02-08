package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.curve.math.Arithmetic;
import com.avereon.marea.Shape2d;
import javafx.geometry.Point3D;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridOrthographic implements Grid {

	@Override
	public String name() {
		return "ORTHO";
	}

	@Override
	public Point3D getNearest( DesignWorkplane workplane, Point3D point ) {
		Point3D origin = CadShapes.parsePoint( workplane.getOrigin() );
		point = point.subtract( origin );
		point = new Point3D( Arithmetic.nearest( point.getX(), workplane.calcSnapGridX() ),
			Arithmetic.nearest( point.getY(), workplane.calcSnapGridY() ),
			Arithmetic.nearest( point.getZ(), workplane.calcSnapGridZ() )
		);
		point = point.add( origin );
		return point;
	}

	@Override
	public List<DesignShape> generateGrid( DesignWorkplane workplane, GridStyle style ) {
		return switch( style ) {
			case DOT -> generateGridDots( workplane );
			case LINE -> generateGridLines( workplane );
		};
	}

	private List<DesignShape> generateGridDots( DesignWorkplane workplane ) {
		return List.of();
	}

	private List<DesignShape> generateGridLines( DesignWorkplane workplane ) {
		List<DesignShape> grid = new ArrayList<>();

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

		//		if( minorVisible ) {
		//			for( double value : minorOffsetsX ) {
		//				DesignLine shape = new DesignLine( new Point3D( value, boundaryY1, 0 ), new Point3D( value, boundaryY2, 0 ) );
		//				shape.setDrawPaint( Paints.toString( minorPaint ) );
		//				shape.setDrawWidth( Double.toString( minorWidth ) );
		//				grid.add( shape );
		//			}
		//			for( double value : minorOffsetsY ) {
		//				DesignLine shape = new DesignLine( new Point3D( boundaryX1, value, 0 ), new Point3D( boundaryX2, value, 0 ) );
		//				shape.setDrawPaint( Paints.toString( minorPaint ) );
		//				shape.setDrawWidth( Double.toString( minorWidth ) );
		//				grid.add( shape );
		//			}
		//		}

		//		if( majorVisible ) {
		//			for( double value : majorOffsetsX ) {
		//				Line shape = new Line( value, boundaryY1, value, boundaryY2 );
		//				shape.setStroke( majorPaint );
		//				shape.setStrokeWidth( majorWidth );
		//				grid.add( shape );
		//			}
		//			for( double value : majorOffsetsY ) {
		//				Line shape = new Line( boundaryX1, value, boundaryX2, value );
		//				shape.setStroke( majorPaint );
		//				shape.setStrokeWidth( majorWidth );
		//				grid.add( shape );
		//			}
		//		}
		//
				if( axisVisible ) {
					for( double value : axisOffsetsX ) {
						DesignLine shape = new DesignLine(value, boundaryY1, value, boundaryY2);
		//				Line shape = new Line( value, boundaryY1, value, boundaryY2 );
		//				shape.setStroke( axisPaint );
		//				shape.setStrokeWidth( axisWidth );
						grid.add( shape );
					}
					for( double value : axisOffsetsY ) {
						DesignLine shape = new DesignLine(boundaryX1, value, boundaryX2, value);
		//				Line shape = new Line( boundaryX1, value, boundaryX2, value );
		//				shape.setStroke( axisPaint );
		//				shape.setStrokeWidth( axisWidth );
						grid.add( shape );
					}
				}

		return grid;
	}

	@Override
	public List<Shape> getGridDots( DesignWorkplane workplane ) {
		return List.of();
	}

	@Override
	public List<Shape> getGridLines( DesignWorkplane workplane ) {
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

	@Override
	public Set<Shape2d> createAxisLines( DesignWorkplane workplane ) {
		Point3D origin = CadShapes.parsePoint( workplane.getOrigin() );
		double boundaryX1 = Math.min( workplane.getBoundaryX1(), workplane.getBoundaryX2() );
		double boundaryX2 = Math.max( workplane.getBoundaryX1(), workplane.getBoundaryX2() );
		double boundaryY1 = Math.min( workplane.getBoundaryY1(), workplane.getBoundaryY2() );
		double boundaryY2 = Math.max( workplane.getBoundaryY1(), workplane.getBoundaryY2() );

		boolean axisVisible = workplane.isGridAxisVisible();

		// Get all offsets
		List<Double> axisOffsetsX = new ArrayList<>();
		List<Double> axisOffsetsY = new ArrayList<>();
		if( origin.getX() >= boundaryX1 && origin.getX() <= boundaryX2 ) axisOffsetsX.add( origin.getX() );
		if( origin.getY() >= boundaryY1 && origin.getY() <= boundaryY2 ) axisOffsetsY.add( origin.getY() );

		Set<Shape2d> shapes = new HashSet<>();

		if( axisVisible ) {
			for( double value : axisOffsetsX ) {
				com.avereon.marea.geom.Line shape = new com.avereon.marea.geom.Line( value, boundaryY1, value, boundaryY2 );
				shapes.add( shape );
			}
			for( double value : axisOffsetsY ) {
				com.avereon.marea.geom.Line shape = new com.avereon.marea.geom.Line( boundaryX1, value, boundaryX2, value );
				shapes.add( shape );
			}
		}

		return shapes;
	}

}
