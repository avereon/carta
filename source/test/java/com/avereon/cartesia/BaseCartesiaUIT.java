package com.avereon.cartesia;

import com.avereon.cartesia.tool.Design2dEditor;
import com.avereon.product.ProductCard;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.asset.AssetType;
import com.avereon.zerra.BaseModUIT;
import lombok.CustomLog;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@CustomLog
public class BaseCartesiaUIT extends BaseModUIT {

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		initMod( ProductCard.card( CartesiaMod.class ) );

		AssetType assetType = getProgram().getAssetManager().getAssetType( Design2dAssetType.class.getName() );
		assertThat( assetType).withFailMessage( "Asset type not registered: " + Design2dAssetType.class.getName() ).isNotNull();

		List<Class<? extends ProgramTool>> tools = getProgram().getToolManager().getRegisteredTools( assetType );
		assertThat( tools).withFailMessage( Design2dEditor.class.getSimpleName() + " not registered for " + Design2dAssetType.class.getName() ).contains( Design2dEditor.class );

		// Check how many tools are registered to the asset type
		assertThat( tools.size() ).isEqualTo( 2 );
	}

}
