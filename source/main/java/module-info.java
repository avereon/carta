import com.avereon.cartesia.CartesiaMod;

module com.avereon.cartesia {

	// Compile-time only
	requires static lombok;

	// Both compile-time and run-time
	requires com.avereon.curve;
	requires com.avereon.marea;
	requires com.avereon.xenon;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires java.logging;
	requires jep;

	exports com.avereon.cartesia.command;
	exports com.avereon.cartesia.data;
	exports com.avereon.cartesia.math;
	exports com.avereon.cartesia.snap;

	exports com.avereon.cartesia to com.avereon.xenon;
	exports com.avereon.cartesia.cursor to com.avereon.zarra;
	exports com.avereon.cartesia.icon to com.avereon.zarra;
	exports com.avereon.cartesia.tool to com.avereon.xenon;
	exports com.avereon.cartesia.rb to com.avereon.xenon;

	opens com.avereon.cartesia.bundles;
	opens com.avereon.cartesia.design.props;
	opens com.avereon.cartesia.settings;

	opens com.avereon.cartesia to org.testfx.junit5;
	opens com.avereon.cartesia.command to org.testfx.junit5;
	opens com.avereon.cartesia.math to org.testfx.junit5;
	opens com.avereon.cartesia.tool to org.testfx.junit5;
	opens com.avereon.cartesia.tool.view to org.testfx.junit5;

	provides com.avereon.xenon.Mod with CartesiaMod;

}
