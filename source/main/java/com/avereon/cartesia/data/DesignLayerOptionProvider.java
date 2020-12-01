package com.avereon.cartesia.data;

import com.avereon.data.IdNode;
import com.avereon.util.TextUtil;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.settings.SettingOptionProvider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DesignLayerOptionProvider implements SettingOptionProvider {

	private final ProgramProduct product;

	private final boolean showRoot;

	public DesignLayerOptionProvider( ProgramProduct product ) {
		this( product, false );
	}

	public DesignLayerOptionProvider( ProgramProduct product, boolean showRoot ) {
		this.product = product;
		this.showRoot = showRoot;
	}

	@Override
	public List<String> getKeys() {
		Optional<Design> optional = getDesign();
		if( optional.isEmpty() ) return List.of();

		Design design = optional.get();
		List<String> rootKey = List.of();
		if( showRoot ) rootKey = List.of( design.getRootLayer().getId() );

		return Stream.concat( rootKey.stream(), design.getAllLayers().stream().map( IdNode::getId ) ).collect( Collectors.toList() );
	}

	@Override
	public String getName( String key ) {
		Optional<Design> optional = getDesign();
		if( optional.isEmpty() ) return TextUtil.EMPTY;

		Design design = optional.get();
		DesignLayer notfound = new DesignLayer();
		List<DesignLayer> layers = design.getAllLayers();

		DesignLayer layer = Stream.concat( Stream.of( design.getRootLayer() ), layers.stream() ).filter( l -> l.getId().equals( key ) ).findAny().orElse( notfound );

		return layer == notfound ? key : layer.getFullName();
	}

	private Optional<Design> getDesign() {
		Object model = product.getProgram().getAssetManager().getCurrentAsset().getModel();
		if( model instanceof Design ) return Optional.of( (Design)model );
		return Optional.empty();
	}

}
