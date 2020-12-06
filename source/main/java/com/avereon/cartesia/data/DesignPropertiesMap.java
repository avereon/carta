package com.avereon.cartesia.data;

import com.avereon.cartesia.BundleKey;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.tool.settings.SettingsPageParser;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DesignPropertiesMap {

	private static final String propertiesPagePath = "/com/avereon/cartesia/design/props/";

	private static final String propertiesPageExt = ".xml";

	private final Map<Class<? extends DesignDrawable>, SettingsPage> propertiesPages;

	public DesignPropertiesMap( ProgramProduct product ) {
		Map<Class<? extends DesignDrawable>, SettingsPage> pages = new HashMap<>();
		pages.put( DesignLayer.class, loadPage( product, "layer" ) );
		pages.put( DesignShape.class, loadPage( product, "shape" ) );
		pages.put( DesignCircle.class, loadPage( product, "circle" ) );
		pages.put( DesignLine.class, loadPage( product, "line" ) );
		pages.put( DesignPoint.class, loadPage( product, "point" ) );
		propertiesPages = Collections.unmodifiableMap( pages );
	}

	public SettingsPage getSettingsPage( Class<? extends DesignDrawable> type ) {
		return propertiesPages.get( type );
	}

	private static SettingsPage loadPage( ProgramProduct product, String key ) {
		try {
			return loadSettingsPage( product, key );
		} catch( IOException exception ) {
			exception.printStackTrace();
		}
		return null;
	}

	private static SettingsPage loadSettingsPage( ProgramProduct product, String key ) throws IOException {
		String pagePath = propertiesPagePath + key + propertiesPageExt;
		return SettingsPageParser.parse( product, pagePath, BundleKey.PROPS ).get( key );
	}

}
