package com.avereon.cartesia.icon;

public class SnapGridDisabledIcon extends SnapGridIcon {

	protected void define() {
		super.define( false );
	}

	public static void main( String[] parameters ) {
		proof( new SnapGridDisabledIcon() );
	}

}
