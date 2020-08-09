package com.avereon.cartesia.data;

import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.DesignUnit;
import com.avereon.data.IdNode;
import com.avereon.data.NodeComparator;
import com.avereon.data.NodeLink;
import com.avereon.util.Log;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class Design extends IdNode {

	public static final DesignUnit DEFAULT_DESIGN_UNIT = DesignUnit.CENTIMETER;

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	public static final String LAYERS = "layers";

	public static final String VIEWS = "views";

	public static final String CURRENT_LAYER = "current-layer";

	private static final System.Logger log = Log.get();

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
		if( !getValues( LAYERS ).contains( layer ) ) throw new IllegalArgumentException( "Layer does not belong to this design" );
		setValue( CURRENT_LAYER, new NodeLink<>( Objects.requireNonNull( layer ) ) );
		return this;
	}

	@SuppressWarnings( "unchecked" )
	public DesignLayer getCurrentLayer() {
		// Current layer is a node link so the layer doesn't get removed from the layer set
		return Objects.requireNonNull( ((NodeLink<DesignLayer>)getValue( CURRENT_LAYER )).getNode() );
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
