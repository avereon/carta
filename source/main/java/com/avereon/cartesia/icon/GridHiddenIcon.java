package com.avereon.cartesia.icon;

public class GridHiddenIcon extends GridIcon {

	protected void define() {
		super.define(false);
	}

	public static void main( String[] parameters ) {
		proof( new GridHiddenIcon() );
	}
}
