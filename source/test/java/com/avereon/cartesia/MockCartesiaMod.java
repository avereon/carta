package com.avereon.cartesia;

import com.avereon.product.ProductCard;
import com.avereon.settings.MapSettings;
import com.avereon.settings.Settings;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public class MockCartesiaMod implements XenonProgramProduct {

	@Override
	public ProductCard getCard() {
		return new ProductCard();
	}

	@Override
	public Settings getSettings() {
		return new MapSettings();
	}

	@Override
	public Path getDataFolder() {
		return Paths.get( "." );
	}

	@Override
	public Xenon getProgram() {
		return null;
	}

	@Override
	public <T> void task( String name, Callable<T> callable ) {
		// This implementation is not suitable for production
		// but is very useful for unit testing
		try {
			callable.call();
		} catch( Exception exception ) {
			throw new RuntimeException( exception );
		}
	}

}
