package com.avereon.cartesia.data;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.map.DesignUnitMapper;
import com.avereon.cartesia.tool.DesignContext;
import com.avereon.data.IdNode;
import lombok.CustomLog;

import java.util.*;
import java.util.stream.Collectors;

@CustomLog
public abstract class Design extends IdNode {

	public static final DesignUnit DEFAULT_DESIGN_UNIT = DesignUnit.CM;

	public static final String NAME = "name";

	public static final String AUTHOR = "author";

	public static final String DESCRIPTION = "description";

	public static final String UNIT = "unit";

	public static final String LAYERS = "layers";

	public static final String VIEWS = "views";

	public static final String PRINTS = "prints";

	private DesignContext context;

	public Design() {
		defineNaturalKey( NAME );
		addModifyingKeys( NAME, AUTHOR, DESCRIPTION, UNIT, LAYERS, VIEWS );

		// Read-only values
		setValue( LAYERS, new DesignLayer() );
		defineReadOnly( LAYERS );

		// Default values
		setDesignUnit( DEFAULT_DESIGN_UNIT );
	}

	public String getName() {
		return getValue( NAME );
	}

	public Design setName( String name ) {
		setValue( NAME, name );
		return this;
	}

	public String getAuthor() {
		return getValue( AUTHOR );
	}

	public Design setAuthor( String author ) {
		setValue( AUTHOR, author );
		return this;
	}

	public String getDescription() {
		return getValue( DESCRIPTION );
	}

	public Design setDescription( String name ) {
		setValue( DESCRIPTION, name );
		return this;
	}

	public DesignUnit calcDesignUnit() {
		// TODO Cache this object, it is used frequently
		return DesignUnitMapper.map( getDesignUnit() );
	}

	public String getDesignUnit() {
		return getValue( UNIT );
	}

	public Design setDesignUnit( DesignUnit unit ) {
		setDesignUnit( unit.name().toLowerCase() );
		return this;
	}

	public Design setDesignUnit( String unit ) {
		setValue( UNIT, unit );
		return this;
	}

	@Override
	public boolean isModifiedBySelf() {
		return super.isModifiedBySelf();
	}

	@Override
	public boolean isModifiedByValue() {
		return super.isModifiedByValue();
	}

	@Override
	public boolean isModifiedByChild() {
		return super.isModifiedByChild();
	}

	public DesignLayer getLayers() {
		return getValue( LAYERS );
	}

	public DesignLayer getLayerById( String id ) {
		for( DesignLayer layer : getAllLayersAndRoot() ) {
			if( layer.getId().equals( id ) ) return layer;
		}
		return null;
	}

	public Optional<DesignLayer> findLayerById( String id ) {
		return Optional.ofNullable( getLayerById( id ) );
	}

	public Set<DesignLayer> findLayers( String key, Object value ) {
		return getLayers().findLayers( key, value );
	}

	public List<DesignLayer> getAllLayersAndRoot() {
		List<DesignLayer> layers = new ArrayList<>();
		layers.add( getLayers() );
		layers.addAll( getLayers().getAllLayers() );
		return layers;
	}

	public List<DesignLayer> getAllLayers() {
		return getLayers().getAllLayers();
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

	public DesignView getViewById( String id ) {
		return getViews().stream().filter( v -> v.getId().equals( id ) ).findFirst().orElse( null );
	}

	public Optional<DesignView> findViewById( String id ) {
		return Optional.ofNullable( getViewById( id ) );
	}

	public Set<DesignView> findViews( String key, Object value ) {
		return getViews().stream().filter( l -> Objects.equals( l.getValue( key ), value ) ).collect( Collectors.toSet() );
	}

	public Set<DesignPrint> getPrints() {
		return getValues( PRINTS );
	}

	public Design addPrint( DesignPrint print ) {
		addToSet( PRINTS, print );
		return this;
	}

	public Design removePrint( DesignPrint print ) {
		removeFromSet( PRINTS, print );
		return this;
	}

	public Set<DesignPrint> findPrints( String key, Object value ) {
		return getPrints().stream().filter( l -> Objects.equals( l.getValue( key ), value ) ).collect( Collectors.toSet() );
	}

	public Map<String, ?> asMap() {
		return asMap( ID, NAME, AUTHOR, DESCRIPTION, UNIT );
	}

	public Map<String, Object> asDeepMap() {
		Map<String, Object> map = new HashMap<>( asMap() );
		map.put( DesignLayer.LAYERS, getLayers().getLayers().stream().collect( Collectors.toMap( DesignLayer::getId, DesignLayer::asDeepMap ) ) );
		if( !getViews().isEmpty() ) map.put( Design.VIEWS, getViews().stream().collect( Collectors.toMap( DesignView::getId, DesignView::asDeepMap ) ) );
		if( !getPrints().isEmpty() ) map.put( Design.PRINTS, getPrints().stream().collect( Collectors.toMap( DesignPrint::getId, DesignPrint::asDeepMap ) ) );
		return map;
	}

	public Design updateFrom( Map<String, Object> map ) {
		map.computeIfPresent( Design.ID, ( k, v ) -> setId( String.valueOf( v ) ) );
		map.computeIfPresent( Design.NAME, ( k, v ) -> setName( String.valueOf( v ) ) );
		map.computeIfPresent( Design.AUTHOR, ( k, v ) -> setAuthor( String.valueOf( v ) ) );
		map.computeIfPresent( Design.DESCRIPTION, ( k, v ) -> setDescription( String.valueOf( v ) ) );
		map.computeIfPresent( Design.UNIT, ( k, v ) -> setDesignUnit( String.valueOf( v ) ) );
		return this;
	}

}
