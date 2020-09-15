import com.avereon.cartesia.CartesiaMod;

module com.avereon.cartesia {
	requires com.avereon.xenon;
	//requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.core;
	requires jep;

	opens com.avereon.cartesia.bundles;
	opens com.avereon.cartesia.design.props;
	opens com.avereon.cartesia.settings;

	exports com.avereon.cartesia to com.avereon.xenon;
	exports com.avereon.cartesia.cursor to com.avereon.zerra;
	exports com.avereon.cartesia.icon to com.avereon.zerra;
	exports com.avereon.cartesia.tool to com.avereon.xenon;

	provides com.avereon.xenon.Mod with CartesiaMod;
}