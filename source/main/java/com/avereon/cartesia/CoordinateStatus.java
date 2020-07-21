package com.avereon.cartesia;

import com.avereon.util.TextUtil;
import com.avereon.xenon.ProgramProduct;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CoordinateStatus extends HBox {

	private final Label xCoord;

	private final Label yCoord;

	private final Label zCoord;

	private final Label zoomValue;

	private final BorderPane zPane;

	private boolean isFraction;

	private NumberFormat format;

	@SuppressWarnings( "SuspiciousNameCombination" )
	public CoordinateStatus( ProgramProduct product ) {
		setPrecision( 4 );

		String xPrompt = product.rb().textOr( "prompt", "x", "X:" );
		String yPrompt = product.rb().textOr( "prompt", "y", "Y:" );
		String zPrompt = product.rb().textOr( "prompt", "z", "Z:" );
		String zoomPrompt = product.rb().textOr( "prompt", "zoom", "ZM:" );

		xCoord = new Label( "0.0" );
		yCoord = new Label( "0.0" );
		zCoord = new Label( "0.0" );
		zoomValue = new Label( "0.0" );

		BorderPane xPane = new BorderPane( null, null, xCoord, null, new Label( xPrompt ) );
		BorderPane yPane = new BorderPane( null, null, yCoord, null, new Label( yPrompt ) );
		zPane = new BorderPane( null, null, zCoord, null, new Label( zPrompt ) );
		BorderPane mPane = new BorderPane( null, null, zoomValue, null, new Label( zoomPrompt ) );

		setShowZCoordinate( false );

		getChildren().addAll( xPane, yPane, zPane, mPane );
	}

	public void updatePosition( DesignTool tool, double x, double y, double z ) {
		Point3D point = tool.mouseToWorld( x,y,z );
		if( isFraction ) {
			xCoord.setText( "0 0/0" );
			yCoord.setText( "0 0/0" );
			zCoord.setText( "0 0/0" );
		} else {
			xCoord.setText( format.format( point.getX() ) );
			yCoord.setText( format.format( point.getY() ) );
			zCoord.setText( format.format( point.getZ() ) );
		}
	}

		public void updateZoom( double x, double y, double z ) {
		zoomValue.setText( format.format( x ) );
	}

	public void setShowZCoordinate( boolean showZ ) {
		zPane.setManaged( showZ );
		zPane.setVisible( showZ );
	}

	public void setPrecision( int count ) {
		format = new DecimalFormat( "0." + TextUtil.pad( count, '0' ) );
		isFraction = false;
	}

	public void setFractionPrecision( int unit ) {
		isFraction = true;
	}

}
