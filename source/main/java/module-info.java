import com.avereon.cartesia.CartesiaMod;
import com.avereon.xenon.Module;

// This should match the group and artifact from the product card
// or there will be a lot of confusion.
module com.avereon.carta {

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
	exports com.avereon.cartesia.command.camera;
	exports com.avereon.cartesia.command.draw;
	exports com.avereon.cartesia.command.layer;
	exports com.avereon.cartesia.command.measure;
	exports com.avereon.cartesia.command.view;
	exports com.avereon.cartesia.command.print;
	exports com.avereon.cartesia.command.snap;
	exports com.avereon.cartesia.command.edit;
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

	provides Module with CartesiaMod;

}
