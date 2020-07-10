import com.avereon.cartesia.CartesiaMod;

module com.avereon.cartesia {
	requires com.avereon.xenon;

	//opens com.avereon.cartesia.bundles;
	exports com.avereon.cartesia to com.avereon.xenon, com.avereon.venza;
	provides com.avereon.xenon.Mod with CartesiaMod;
}