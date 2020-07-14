package com.avereon.cartesia;

import com.avereon.util.TextUtil;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CoordinateStatus extends HBox {

	private final DesignTool tool;

	private final Label xCoord;

	private final Label yCoord;

	private final Label zCoord;

	private final BorderPane zPane;

	private boolean isFraction;

	private NumberFormat format;

	public CoordinateStatus( DesignTool tool ) {
		this.tool = tool;
		setPrecision( 4 );

		String xPrompt = tool.getProduct().rb().textOr( "prompt", "x", "X:" );
		String yPrompt = tool.getProduct().rb().textOr( "prompt", "y", "Y:" );
		String zPrompt = tool.getProduct().rb().textOr( "prompt", "z", "Z:" );

		xCoord = new Label( "0.000000" );
		yCoord = new Label( "0.000000" );
		zCoord = new Label( "0.000000" );

		BorderPane xPane = new BorderPane( null, null, xCoord, null, new Label( xPrompt ) );
		BorderPane yPane = new BorderPane( null, null, yCoord, null, new Label( yPrompt ) );
		zPane = new BorderPane( null, null, zCoord, null, new Label( zPrompt ) );

		setShowZCoordinate( false );

		getChildren().addAll( xPane, yPane, zPane );
	}

	public void update( MouseEvent event ) {
		Point3D point = tool.mouseToWorld( event );
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
