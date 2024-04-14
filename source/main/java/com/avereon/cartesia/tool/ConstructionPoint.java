package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignMarker;
import com.avereon.zarra.color.Colors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import lombok.CustomLog;

@CustomLog
public class ConstructionPoint extends Region {

	public static final DesignMarker.Type DEFAULT_TYPE = DesignMarker.Type.CIRCLE;

	public static final Paint DEFAULT_PAINT = Colors.parse( "#80ff00ff" );

	private ObjectProperty<DesignMarker.Type> type;

	private ObjectProperty<Paint> paint;

	private Path path;

	public ConstructionPoint() {
		this( DEFAULT_TYPE );
	}

	public ConstructionPoint( DesignMarker.Type type ) {
		getStyleClass().addAll( "construction-point" );
		setManaged( false );
		setType( type );
		doUpdateGeometry();
	}

	public DesignMarker.Type getType() {
		return type == null ? DEFAULT_TYPE : type.get();
	}

	public ConstructionPoint setType( DesignMarker.Type type ) {
		typeProperty().set( type );
		return this;
	}

	public ObjectProperty<DesignMarker.Type> typeProperty() {
		if( type == null ) {
			type = new SimpleObjectProperty<>( DEFAULT_TYPE );
			type.addListener( ( p, o, n ) -> doUpdateGeometry() );
		}
		return type;
	}

	public Paint getPaint() {
		return paint == null ? DEFAULT_PAINT : paint.get();
	}

	public ConstructionPoint setPaint( Paint paint ) {
		paintProperty().set( paint );
		return this;
	}

	public ObjectProperty<Paint> paintProperty() {
		if( paint == null ) {
			paint = new SimpleObjectProperty<>( DEFAULT_PAINT );
			paint.addListener( ( p, o, n ) -> path.setFill( n ) );
		}
		return paint;
	}

	public Point3D getLocation() {
		return new Point3D( getLayoutX(), getLayoutY(), 0 );
	}

	private void doUpdateGeometry() {
		path = getType().getFxPath();
		path.setFill( getPaint() );
		getChildren().setAll( path );
	}

}
