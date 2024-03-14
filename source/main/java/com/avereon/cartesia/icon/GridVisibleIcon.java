package com.avereon.cartesia.icon;

public class GridVisibleIcon extends GridIcon{

	protected void define() {
		super.define(true);
	}

	public static void main( String[] parameters ) {
		proof( new GridVisibleIcon() );
	}
}
