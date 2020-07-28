import com.avereon.cartesia.CartesiaMod;

module com.avereon.cartesia {
	requires com.avereon.xenon;
	requires jep;

	opens com.avereon.cartesia.bundles;
	opens com.avereon.cartesia.settings;

	exports com.avereon.cartesia to com.avereon.xenon, com.avereon.zerra;
	exports com.avereon.cartesia.cursor to com.avereon.zerra;

	provides com.avereon.xenon.Mod with CartesiaMod;
}