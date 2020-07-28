package com.avereon.cartesia;

import com.avereon.data.Node;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Design extends Node {

	public static final DesignUnit DEFAULT_DESIGN_UNIT = DesignUnit.CENTIMETER;

	private static final String ID = "id";

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	public static final String LAYERS = "layers";

	public static final String CURRENT_LAYER = "current-layer";

	private final CommandProcessor commandProcessor;

	public Design() {
		definePrimaryKey( ID );
		addModifyingKeys( NAME, UNIT, LAYERS );
		setDesignUnit( DEFAULT_DESIGN_UNIT );

		this.commandProcessor = new CommandProcessor();
	}

	public String getName() {
		return getValue( NAME );
	}

	public Design setName( String name ) {
		setValue( NAME, name );
		return this;
	}

	public DesignUnit getDesignUnit() {
		return getValue( UNIT );
	}

	public Design setDesignUnit( DesignUnit unit ) {
		setValue( UNIT, unit );
		return this;
	}

	public CommandProcessor getCommandProcessor() {
		return commandProcessor;
	}

	public List<DesignLayer> getLayers() {
		LayerNode layers = getValue( LAYERS );
		if( layers == null ) return List.of();
		Stream<DesignLayer> stream = layers.stream();
		return stream.collect( Collectors.toList() );
	}

	private LayerNode layerNode() {
		return computeIfAbsent( LAYERS, k -> new LayerNode() );
	}

	public Design setCurrentLayer( DesignLayer layer ) {
		if( layerNode().contains( layer ) ) throw new IllegalArgumentException( "Layer does not belong to this design" );
		setValue( CURRENT_LAYER, layer );
		return this;
	}

	public DesignLayer currentLayer() {
		return getValue( CURRENT_LAYER );
	}

	public Design addLayer( DesignLayer layer ) {
		layerNode().addLayer( layer );
		return this;
	}

	public Design removeLayer( DesignLayer layer ) {
		LayerNode layers = getValue( LAYERS );
		if( layers != null ) {
			layers.removeLayer( layer );
			if( layers.isEmpty() ) setValue( LAYERS, null );
		}
		return this;
	}

	// TODO This might be a model for a NodeCollection?
	private static class LayerNode extends Node {

		public <T> Stream<T> stream() {
			return super.stream();
		}

		public boolean isEmpty() {
			return super.isEmpty();
		}

		public boolean contains( DesignLayer layer ) {
			return super.getValues().contains( layer );
		}

		public void addLayer( DesignLayer layer ) {
			setValue( layer.getId(), layer );
		}

		public void removeLayer( DesignLayer layer ) {
			setValue( layer.getId(), null );
		}
	}

}
