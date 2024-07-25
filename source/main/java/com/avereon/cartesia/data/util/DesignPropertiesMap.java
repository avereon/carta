package com.avereon.cartesia.data.util;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.*;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.tool.settings.SettingsPageParser;
import lombok.CustomLog;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@CustomLog
public class DesignPropertiesMap {

	private static final String propertiesPagePath = "/com/avereon/cartesia/design/props/";

	private static final String propertiesPageExt = ".xml";

	private static final Map<Class<? extends DesignDrawable>, SettingsPage> propertiesPageCache;

	static {
		propertiesPageCache = Collections.synchronizedMap( new WeakHashMap<>() );
	}

	public DesignPropertiesMap( XenonProgramProduct product ) {
		propertiesPageCache.putIfAbsent( DesignLayer.class, loadPage( product, "layer" ) );
		propertiesPageCache.putIfAbsent( DesignShape.class, loadPage( product, "shape" ) );
		propertiesPageCache.putIfAbsent( DesignEllipse.class, loadPage( product, "arc" ) );
		propertiesPageCache.putIfAbsent( DesignArc.class, loadPage( product, "arc" ) );
		propertiesPageCache.putIfAbsent( DesignLine.class, loadPage( product, "line" ) );
		propertiesPageCache.putIfAbsent( DesignMarker.class, loadPage( product, "point" ) );
		propertiesPageCache.putIfAbsent( DesignCubic.class, loadPage( product, "curve" ) );
		propertiesPageCache.putIfAbsent( DesignText.class, loadPage( product, "text" ) );
	}

	public SettingsPage getSettingsPage( Class<? extends DesignDrawable> type ) {
		return propertiesPageCache.get( type );
	}

	private SettingsPage loadPage( XenonProgramProduct product, String key ) {
		try {
			return loadSettingsPage( product, key );
		} catch( IOException exception ) {
			log.atError().withCause( exception).log( "Unable to load settings page for %s", key );
		}
		return null;
	}

	private SettingsPage loadSettingsPage( XenonProgramProduct product, String key ) throws IOException {
		String pagePath = propertiesPagePath + key + propertiesPageExt;
		return SettingsPageParser.parse( product, pagePath, RbKey.PROPS ).get( key );
	}

}
