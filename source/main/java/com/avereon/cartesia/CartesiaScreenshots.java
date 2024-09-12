package com.avereon.cartesia;

import com.avereon.product.ProgramFlag;
import com.avereon.xenon.ProgramScreenshots;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class CartesiaScreenshots extends ProgramScreenshots {

	public static void main( String[] args ) {
		new CartesiaScreenshots().generate( args );
	}

//	@Override
//	protected String getLogLevel() {
//		return ProgramFlag.DEBUG;
//	}

	@Override
	protected List<String> getProgramParameters() {
		List<String> parameters = new ArrayList<>( super.getProgramParameters() );

		parameters.add( ProgramFlag.ENABLE_MOD );
		parameters.add( CartesiaMod.class.getModule().getName() );

		return parameters;
	}

	@Override
	protected void generateScreenshots() throws InterruptedException, TimeoutException {
		screenshot( "default-workarea" );
		screenshot( Path.of( "sample/design/screenshot.cartesia2d" ).toUri(), "design-tool" );
	}

}
