package com.avereon.cartesia.data;

import com.avereon.xenon.BundleKey;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.settings.SettingOptionProvider;

import java.util.List;

public class PointTypeOptionProvider implements SettingOptionProvider {

	private static List<String> keys;

	private final ProgramProduct product;

	public PointTypeOptionProvider( ProgramProduct product ) {
		this.product = product;
	}

	static {
		PointTypeOptionProvider.keys = List.of(
			NULL_VALUE_OPTION_KEY,
			DesignPoints.Type.CROSS.name().toLowerCase(),
			DesignPoints.Type.X.name().toLowerCase(),
			DesignPoints.Type.REFERENCE.name().toLowerCase(),
			DesignPoints.Type.CIRCLE.name().toLowerCase(),
			DesignPoints.Type.DIAMOND.name().toLowerCase(),
			DesignPoints.Type.SQUARE.name().toLowerCase()
		);
	}

	@Override
	public List<String> getKeys() {
		System.out.println( "getKeys=" + keys );
		return keys;
	}

	@Override
	public String getName( String key ) {
		if( key.equals( NULL_VALUE_OPTION_KEY ) ) key = "default";
		return product.rb().text( BundleKey.PROPS, "point-type-" + key );
	}

}
