package com.avereon.cartesia.data;

import com.avereon.data.IdNode;
import com.avereon.util.TextUtil;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.settings.SettingOptionProvider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DesignLayerOptionProvider implements SettingOptionProvider {

	private final ProgramProduct product;

	public DesignLayerOptionProvider( ProgramProduct product ) {
		this.product = product;
	}

	@Override
	public List<String> getKeys() {
		// FIXME The option provider is not returning valid results
		Optional<Design> optional = getDesign();
		if( optional.isEmpty() ) return List.of();
		return optional.get().getAllLayers().stream().map( IdNode::getId ).collect( Collectors.toList() );
	}

	@Override
	public String getName( String key ) {
		Optional<Design> optional = getDesign();
		if( optional.isEmpty() ) return TextUtil.EMPTY;

		DesignLayer notfound = new DesignLayer();
		List<DesignLayer> layers = optional.get().getAllLayers();
		DesignLayer layer = layers.stream().filter( l -> l.getId().equals( key ) ).findAny().orElse( notfound );

		return layer == notfound ? TextUtil.EMPTY : layer.getName();
	}

	private Optional<Design> getDesign() {
		//Tool tool = product.getProgram().getWorkspaceManager().getActiveWorkpane().getActiveTool();
		//if( tool instanceof DesignTool ) return Optional.of( tool.getAssetModel() );

		Object model = product.getProgram().getAssetManager().getCurrentAsset().getModel();
		if( model instanceof Design ) return Optional.of( (Design)model );
		return Optional.empty();
	}

}
