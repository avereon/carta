package com.avereon.cartesia.data.util;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.DesignUnit;
import com.avereon.product.Rb;
import com.avereon.xenon.tool.settings.SettingOptionProvider;

import java.util.List;

public class DesignUnitOptionProvider implements SettingOptionProvider {

	private static List<String> keys;

	static {
		DesignUnitOptionProvider.keys = List.of(
			DesignUnit.MM.name().toLowerCase(),
			DesignUnit.CM.name().toLowerCase(),
			//DesignUnit.DECIMETER.name().toLowerCase(),
			DesignUnit.KM.name().toLowerCase(),
			DesignUnit.M.name().toLowerCase(),
			DesignUnit.IN.name().toLowerCase(),
			DesignUnit.FT.name().toLowerCase(),
			DesignUnit.YD.name().toLowerCase(),
			DesignUnit.MI.name().toLowerCase(),
			DesignUnit.NM.name().toLowerCase().replace( "_", "-" )
		);

	}

	@Override
	public List<String> getKeys() {
		return keys;
	}

	@Override
	public String getName( String key ) {
		return Rb.text( RbKey.PROPS, "design-unit-" + key );
	}

}
