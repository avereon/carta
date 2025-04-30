package com.avereon.cartesia.tool;

import com.avereon.product.Rb;
import com.avereon.util.TextUtil;
import com.avereon.zerra.javafx.Fx;
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
	public CoordinateStatus() {
		setPrecision( 4 );

		String xPrompt = Rb.textOr(  "prompt", "x", "X:" );
		String yPrompt = Rb.textOr(  "prompt", "y", "Y:" );
		String zPrompt = Rb.textOr(  "prompt", "z", "Z:" );
		String zoomPrompt = Rb.textOr(  "prompt", "zoom", "ZM:" );

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

		updatePosition( Point3D.ZERO );
		updateZoom( BaseDesignTool.DEFAULT_ZOOM );
	}

	public void updateZoom( double zoom ) {
		Fx.run( () -> zoomValue.setText( format.format( zoom ) ) );
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

	public void updatePosition( Point3D position ) {
		if( position == null ) return;
		Fx.run( () -> {
			if( isFraction ) {
				xCoord.setText( "0 0/0" );
				yCoord.setText( "0 0/0" );
				zCoord.setText( "0 0/0" );
			} else {
				xCoord.setText( format.format( position.getX() ) );
				yCoord.setText( format.format( position.getY() ) );
				zCoord.setText( format.format( position.getZ() ) );
			}
		} );
	}

}
