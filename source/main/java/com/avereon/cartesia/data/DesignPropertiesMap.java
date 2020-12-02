package com.avereon.cartesia.data;

import com.avereon.xenon.ProgramProduct;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DesignPropertiesMap {

	private static final String propertiesPagePath = "/com/avereon/cartesia/design/props/";

	private static final String propertiesPageExt = ".xml";

	private static final Map<Class<? extends DesignDrawable>, String> propertiesPages;

	static {
		Map<Class<? extends DesignDrawable>, String> pages = new HashMap<>();
		pages.put( DesignLayer.class, "layer" );
		pages.put( DesignShape.class, "shape" );
		pages.put( DesignCircle.class, "circle" );
		pages.put( DesignLine.class, "line" );
		pages.put( DesignPoint.class, "point" );
		propertiesPages = Collections.unmodifiableMap( pages );
	}

	public static String getDesignPropertiesKey( ProgramProduct product, Class<? extends DesignDrawable> type ) {
		return propertiesPages.get( type );
	}

//	private static SettingsPage loadPage( ProgramProduct product, String key ) {
//		try {
//			return loadSettingsPage( product, key );
//		} catch( IOException exception ) {
//			exception.printStackTrace();
//		}
//		return null;
//	}
//
//	private static SettingsPage loadSettingsPage( ProgramProduct product, String key ) throws IOException {
//		String pagePath = propertiesPagePath + key + propertiesPageExt;
//		return SettingsPageParser.parse( product, pagePath, BundleKey.PROPS );
//	}

}
