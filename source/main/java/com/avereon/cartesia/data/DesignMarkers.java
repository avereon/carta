package com.avereon.cartesia.data;

import com.avereon.curve.math.Constants;
import javafx.scene.shape.*;

public class DesignMarkers {

	private static final DesignMarker.Type DEFAULT_TYPE = DesignMarker.Type.CROSS;

	public static Path createPoint( DesignMarker.Type type, double x, double y, double r ) {
		return switch( type ) {
			case CROSS -> createCrossPoint( x, y, r );
			case REFERENCE -> createReference( x, y, r );
			case CIRCLE -> createCircle( x, y, r );
			case DIAMOND -> createDiamond( x, y, r );
			case SQUARE -> createSquare( x, y, r );
			default -> createXPoint( x, y, r );
		};
	}

	public static DesignMarker.Type parseType( String type ) {
		try {
			return DesignMarker.Type.valueOf( type.toUpperCase() );
		} catch( NullPointerException | IllegalArgumentException exception ) {
			return DEFAULT_TYPE;
		}
	}

	private static Path createCrossPoint( double x, double y, double r ) {
		Path path = new Path( new MoveTo( x, y ) );
		path.getElements().add( new MoveTo( x, y - r ) );
		path.getElements().add( new LineTo( x, y + r ) );
		path.getElements().add( new MoveTo( x - r, y ) );
		path.getElements().add( new LineTo( x + r, y ) );
		return path;
	}

	private static Path createXPoint( double x, double y, double r ) {
		double z = Constants.SQRT_ONE_HALF * r;
		Path path = new Path( new MoveTo( x, y ) );
		path.getElements().add( new MoveTo( x - z, y - z ) );
		path.getElements().add( new LineTo( x + z, y + z ) );
		path.getElements().add( new MoveTo( x - z, y + z ) );
		path.getElements().add( new LineTo( x + z, y - z ) );
		return path;
	}

	private static Path createReference( double x, double y, double r ) {
		Path path = createCrossPoint( x, y, r );
		Path xPath = createXPoint( x, y, r );
		path.getElements().addAll( xPath.getElements().subList( 1, xPath.getElements().size() ) );
		return path;
	}

	private static Path createCircle( double x, double y, double r ) {
		Path path = new Path( new MoveTo( x, y ) );

		path.getElements().add( new MoveTo( x, y - r ) );
		path.getElements().add( new ArcTo( r, r, 0, x, y + r, false, false ) );
		path.getElements().add( new ArcTo( r, r, 0, x, y - r, false, false ) );
		path.getElements().add( new ClosePath() );

		return path;
	}

	private static Path createDiamond( double x, double y, double r ) {
		Path path = new Path( new MoveTo( x, y ) );
		path.getElements().add( new MoveTo( x - r, y ) );
		path.getElements().add( new LineTo( x, y + r ) );
		path.getElements().add( new LineTo( x + r, y ) );
		path.getElements().add( new LineTo( x, y - r ) );
		path.getElements().add( new ClosePath() );
		return path;
	}

	private static Path createSquare( double x, double y, double r ) {
		double z = Constants.SQRT_ONE_HALF * r;
		Path path = new Path( new MoveTo( x, y ) );
		path.getElements().add( new MoveTo( x - z, y - z ) );
		path.getElements().add( new LineTo( x - z, y + z ) );
		path.getElements().add( new LineTo( x + z, y + z ) );
		path.getElements().add( new LineTo( x + z, y - z ) );
		path.getElements().add( new ClosePath() );
		return path;
	}

}
