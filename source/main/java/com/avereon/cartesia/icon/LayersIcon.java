package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;

public class LayersIcon extends LayerIcon {

	public LayersIcon() {
		super( -6 );
		add( lowerLayer(0) );
		add( lowerLayer( 6 ) );
	}

	public static void main( String[] commands ) {
		Proof.proof( new LayersIcon() );
	}

}
