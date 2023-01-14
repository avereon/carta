package com.avereon.cartesia.data.util;

import com.avereon.cartesia.data.DesignMarker;
import com.avereon.product.Rb;
import com.avereon.xenon.RbKey;
import com.avereon.xenon.tool.settings.SettingOptionProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class MarkerTypeOptionProvider implements SettingOptionProvider {

	private static List<String> keys;

	static {
		Stream<String> types = Arrays.stream( DesignMarker.Type.values() ).map( t -> t.name().toLowerCase() );
		MarkerTypeOptionProvider.keys = Collections.unmodifiableList( types.toList() );
	}

	@Override
	public List<String> getKeys() {
		return keys;
	}

	@Override
	public String getName( String key ) {
		//if( key.equals( NULL_VALUE_OPTION_KEY ) ) key = "default";
		return Rb.text( RbKey.PROPS, "point-type-" + key );
	}

}
