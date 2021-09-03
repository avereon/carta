package com.avereon.cartesia;

import com.avereon.cartesia.tool.Design2dEditor;
import com.avereon.product.ProductCard;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.asset.AssetType;
import com.avereon.xenon.test.BaseModUIT;
import lombok.CustomLog;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@CustomLog
public class BaseCartesiaUIT extends BaseModUIT {

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		initMod( ProductCard.card( CartesiaMod.class ) );

		AssetType assetType = getProgram().getAssetManager().getAssetType( Design2dAssetType.class.getName() );
		assertNotNull( assetType, "Asset type not registered: " + Design2dAssetType.class.getName() );

		List<Class<? extends ProgramTool>> tools = getProgram().getToolManager().getRegisteredTools( assetType );
		assertThat( Design2dEditor.class.getSimpleName() + " not registered for " + Design2dAssetType.class.getName(), tools, contains( Design2dEditor.class ) );
		assertThat( tools.size(), is( 1 ) );
	}

}
