import com.avereon.cartesia.CartesiaMod;
import com.avereon.xenon.Module;

module com.avereon.cartesia {

	// Compile-time only
	requires static lombok;

	// Both compile-time and run-time
	requires com.avereon.curve;
	requires com.avereon.marea;
	requires com.avereon.xenon;
	requires com.avereon.zarra;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires java.logging;
	requires jep;

	// Public APIs
	exports com.avereon.cartesia.command;
	exports com.avereon.cartesia.data;
	exports com.avereon.cartesia.math;
	exports com.avereon.cartesia.snap;

	// Private APIs
	exports com.avereon.cartesia to com.avereon.xenon;
	exports com.avereon.cartesia.cursor to com.avereon.zarra;
	exports com.avereon.cartesia.icon to com.avereon.zarra;
	exports com.avereon.cartesia.settings to com.avereon.xenon;
	exports com.avereon.cartesia.tool to com.avereon.xenon;
	exports com.avereon.cartesia.tool.design to com.avereon.xenon;
	exports com.avereon.cartesia.rb to com.avereon.xenon;

	// Public resources
	opens com.avereon.cartesia.bundles;
	opens com.avereon.cartesia.design.props;
	opens com.avereon.cartesia.settings;

	// Private APIs for testing
	opens com.avereon.cartesia to org.testfx.junit5;
	opens com.avereon.cartesia.command to org.testfx.junit5;
	opens com.avereon.cartesia.math to org.testfx.junit5;
	opens com.avereon.cartesia.tool to org.testfx.junit5;
	opens com.avereon.cartesia.tool.view to org.testfx.junit5;
	opens com.avereon.cartesia.tool.design to org.testfx.junit5;
	exports com.avereon.cartesia.command.camera;
	opens com.avereon.cartesia.command.camera to org.testfx.junit5;
	exports com.avereon.cartesia.command.draw;
	opens com.avereon.cartesia.command.draw to org.testfx.junit5;
	exports com.avereon.cartesia.command.layer;
	opens com.avereon.cartesia.command.layer to org.testfx.junit5;
	exports com.avereon.cartesia.command.measure;
	opens com.avereon.cartesia.command.measure to org.testfx.junit5;
	exports com.avereon.cartesia.command.view;
	opens com.avereon.cartesia.command.view to org.testfx.junit5;
	exports com.avereon.cartesia.command.print;
	opens com.avereon.cartesia.command.print to org.testfx.junit5;
	exports com.avereon.cartesia.command.snap;
	opens com.avereon.cartesia.command.snap to org.testfx.junit5;
	exports com.avereon.cartesia.command.edit;
	opens com.avereon.cartesia.command.edit to org.testfx.junit5;

	provides Module with CartesiaMod;

}
