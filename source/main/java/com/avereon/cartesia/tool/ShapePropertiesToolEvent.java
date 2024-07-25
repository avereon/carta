package com.avereon.cartesia.tool;

import com.avereon.event.Event;
import com.avereon.event.EventType;
import com.avereon.settings.Settings;
import com.avereon.xenon.tool.settings.SettingsPage;
import lombok.Getter;

@Getter
public class ShapePropertiesToolEvent extends Event {

	public static final EventType<ShapePropertiesToolEvent> PROPERTIES = new EventType<>( Event.ANY, "PROPERTIES" );

	public static final EventType<ShapePropertiesToolEvent> ANY = PROPERTIES;

	public static final EventType<ShapePropertiesToolEvent> SHOW = new EventType<>( PROPERTIES, "SHOW" );

	public static final EventType<ShapePropertiesToolEvent> HIDE = new EventType<>( PROPERTIES, "HIDE" );

	private final SettingsPage page;

	private final Settings settings;

	public ShapePropertiesToolEvent( Object source, EventType<? extends ShapePropertiesToolEvent> type ) {
		this( source, type, null, null );
	}

	public ShapePropertiesToolEvent( Object source, EventType<? extends ShapePropertiesToolEvent> type, SettingsPage page, Settings settings ) {
		super( source, type );
		if( type == SHOW && page == null ) throw new IllegalArgumentException( "Show page cannot be null" );
		this.page = page;
		this.settings = settings;
	}

}
