module com.avereon.cartesia2d {
	requires com.avereon.xenon;

	//opens com.avereon.cart2.bundles;
	exports com.avereon.cart2 to com.avereon.xenon, com.avereon.venza;
	provides com.avereon.xenon.Mod with com.avereon.cart2.Cartesia2dMod;
}