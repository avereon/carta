package com.avereon.cartesia.icon;

public class SnapGridEnabledIcon extends SnapGridIcon {

	protected void define() {
		super.define( true );
	}

	public static void main( String[] parameters ) {
		proof( new SnapGridEnabledIcon() );
	}

}
