package com.avereon.cartesia;

import com.avereon.product.ProductBundle;
import com.avereon.product.ProductCard;
import com.avereon.settings.MapSettings;
import com.avereon.settings.Settings;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MockProgramProduct implements ProgramProduct {

	@Override
	public ProductCard getCard() {
		return new ProductCard();
	}

	@Override
	public ClassLoader getClassLoader() {
		return getClass().getClassLoader();
	}

	@Override
	public ProductBundle rb() {
		return new ProductBundle( this );
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
	public Program getProgram() {
		return null;
	}

}
