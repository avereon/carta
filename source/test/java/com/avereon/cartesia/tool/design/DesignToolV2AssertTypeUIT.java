package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.Design2dAssetType;
import lombok.CustomLog;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Getter
@CustomLog
public class DesignToolV2AssertTypeUIT extends DesignToolV2TestUIT {

	@Test
	void assetTypeResolvesCorrectly() {
		assertThat( getAsset().getType() ).isInstanceOf( Design2dAssetType.class );
	}

}
