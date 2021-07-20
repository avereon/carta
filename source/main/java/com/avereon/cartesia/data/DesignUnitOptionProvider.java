package com.avereon.cartesia.data;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.DesignUnit;
import com.avereon.product.Rb;
import com.avereon.xenon.tool.settings.SettingOptionProvider;

import java.util.List;

public class DesignUnitOptionProvider implements SettingOptionProvider {

	private static List<String> keys;

	static {
		DesignUnitOptionProvider.keys = List.of(
			DesignUnit.MILLIMETER.name().toLowerCase(),
			DesignUnit.CENTIMETER.name().toLowerCase(),
			//DesignUnit.DECIMETER.name().toLowerCase(),
			DesignUnit.KILOMETER.name().toLowerCase(),
			DesignUnit.METER.name().toLowerCase(),
			DesignUnit.INCH.name().toLowerCase(),
			DesignUnit.FOOT.name().toLowerCase(),
			DesignUnit.YARD.name().toLowerCase(),
			DesignUnit.MILE.name().toLowerCase(),
			DesignUnit.NAUTICAL_MILE.name().toLowerCase().replace( "_", "-" )
		);

	}

	@Override
	public List<String> getKeys() {
		return keys;
	}

	@Override
	public String getName( String key ) {
		return Rb.text( BundleKey.PROPS, "design-unit-" + key );
	}

}
