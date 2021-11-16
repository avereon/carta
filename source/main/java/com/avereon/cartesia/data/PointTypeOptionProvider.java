package com.avereon.cartesia.data;

import com.avereon.product.Rb;
import com.avereon.xenon.RbKey;
import com.avereon.xenon.tool.settings.SettingOptionProvider;

import java.util.List;

public class PointTypeOptionProvider implements SettingOptionProvider {

	private static List<String> keys;

	static {
		PointTypeOptionProvider.keys = List.of(
			NULL_VALUE_OPTION_KEY,
			DesignMarker.Type.CROSS.name().toLowerCase(),
			DesignMarker.Type.X.name().toLowerCase(),
			DesignMarker.Type.REFERENCE.name().toLowerCase(),
			DesignMarker.Type.CIRCLE.name().toLowerCase(),
			DesignMarker.Type.DIAMOND.name().toLowerCase(),
			DesignMarker.Type.SQUARE.name().toLowerCase()
		);
	}

	@Override
	public List<String> getKeys() {
		return keys;
	}

	@Override
	public String getName( String key ) {
		if( key.equals( NULL_VALUE_OPTION_KEY ) ) key = "default";
		return Rb.text( RbKey.PROPS, "point-type-" + key );
	}

}
