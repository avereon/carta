package com.avereon.cartesia.data;

import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.DesignUnit;
import com.avereon.data.IdNode;
import com.avereon.data.NodeComparator;
import com.avereon.data.NodeSet;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class Design extends IdNode {

	public static final DesignUnit DEFAULT_DESIGN_UNIT = DesignUnit.CENTIMETER;

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	public static final String LAYERS = "layers";

	public static final String VIEWS = "views";

	public static final String CURRENT_LAYER = "current-layer";

	private final CommandProcessor commandProcessor;

	public Design() {
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

	public Design setCurrentLayer( DesignLayer layer ) {
		if( layerNode().contains( layer ) ) throw new IllegalArgumentException( "Layer does not belong to this design" );
		setValue( CURRENT_LAYER, Objects.requireNonNull( layer ) );
		return this;
	}

	public DesignLayer getCurrentLayer() {
		return Objects.requireNonNull( getValue( CURRENT_LAYER ) );
	}

	public List<DesignLayer> getLayers() {
		return getValueList( LAYERS, new NodeComparator<>( DesignLayer.ORDER ) );
	}

	public Design addLayer( DesignLayer layer ) {
		addToSet( LAYERS, layer );
		return this;
	}

	public Design removeLayer( DesignLayer layer ) {
		removeFromSet( LAYERS, layer );
		return this;
	}

	@SuppressWarnings( "unchecked" )
	private NodeSet<DesignLayer> layerNode() {
		return (NodeSet<DesignLayer>)Optional.ofNullable( getValue( LAYERS ) ).orElse( NodeSet.of() );
	}

	public Set<DesignView> getViews() {
		return getValues( VIEWS );
	}

	public Design addView( DesignView view ) {
		addToSet( VIEWS, view );
		return this;
	}

	public Design removeView( DesignView view ) {
		removeFromSet( VIEWS, view );
		return this;
	}

}
