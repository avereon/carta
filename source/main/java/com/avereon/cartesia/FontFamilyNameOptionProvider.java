package com.avereon.cartesia;

import com.avereon.xenon.tool.settings.SettingOptionProvider;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class FontFamilyNameOptionProvider implements SettingOptionProvider {

	private final List<String> names;

	public FontFamilyNameOptionProvider() {
		names = new ArrayList<>();
		names.addAll( Font.getFamilies() );
	}

	@Override
	public List<String> getKeys() {
		return names;
	}

	@Override
	public String getName( String key ) {
		return key;
	}

	@Override
	public String getValue( String key ) {
		return SettingOptionProvider.super.getValue( key );
	}

}
