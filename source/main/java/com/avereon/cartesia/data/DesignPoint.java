package com.avereon.cartesia.data;

import com.avereon.cartesia.math.Maths;
import com.avereon.data.NodeSettingsWrapper;
import com.avereon.xenon.BundleKey;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.settings.SettingOptionProvider;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.tool.settings.SettingsPageParser;
import javafx.geometry.Point3D;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DesignPoint extends DesignShape {

	public static final String SIZE = "size";

	public static final String TYPE = "type";

	public static final double DEFAULT_SIZE = 1.0;

	private static final DesignPoints.Type DEFAULT_TYPE = DesignPoints.Type.CROSS;

	private static final double ZERO_DRAW_WIDTH = 0.0;

	public DesignPoint() {
		addModifyingKeys( ORIGIN, SIZE, TYPE );
	}

	public DesignPoint( Point3D origin ) {
		this();
		setOrigin( origin );
	}

	public double calcSize() {
		String size = getSize();
		if( size != null ) return Maths.evalNoException( size );
		return DEFAULT_SIZE;
	}

	public String getSize() {
		return getValue( SIZE );
	}

	public DesignPoint setSize( String size ) {
		setValue( SIZE, size );
		return this;
	}

	public DesignPoints.Type calcType() {
		return DesignPoints.parseType( getType() );
	}

	public String getType() {
		return getValue( TYPE );
	}

	public DesignPoint setType( String type ) {
		setValue( TYPE, type );
		return this;
	}

	@Override
	public double calcDrawWidth() {
		return calcType().isClosed() ? ZERO_DRAW_WIDTH : super.calcDrawWidth();
	}

	@Override
	public Paint calcFillPaint() {
		return calcDrawPaint();
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, "point" );
		map.putAll( asMap( SIZE, TYPE ) );
		return map;
	}

	public DesignPoint updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setSize( (String)map.get( SIZE ) );
		setType( (String)map.get( TYPE ) );
		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN );
	}

	@Override
	public SettingsPage getPropertiesPage( ProgramProduct product ) throws IOException {
		String pagePath = "/com/avereon/cartesia/design/props/point.xml";
		if( page == null ) {
			page = new SettingsPageParser( product ).parse( pagePath, BundleKey.PROPS ).get( "point" );
			page.setSettings( new NodeSettingsWrapper( this )  );
			page.setOptionProviders( Map.of( "point-type-option-provider", new PointTypeOptionProvider( product ) ) );
		}

		return page;
	}

	public double getRadius() {
		return 0.5 * calcSize();
	}

	private static class PointTypeOptionProvider implements SettingOptionProvider {

		private static List<String> keys;

		private final ProgramProduct product;

		private PointTypeOptionProvider( ProgramProduct product ) {
			this.product = product;
		}

		static {
			PointTypeOptionProvider.keys = List.of(
				NULL_VALUE_OPTION_KEY,
				DesignPoints.Type.CROSS.name().toLowerCase(),
				DesignPoints.Type.X.name().toLowerCase(),
				DesignPoints.Type.REFERENCE.name().toLowerCase(),
				DesignPoints.Type.CIRCLE.name().toLowerCase(),
				DesignPoints.Type.DIAMOND.name().toLowerCase(),
				DesignPoints.Type.SQUARE.name().toLowerCase()
			);
		}

		@Override
		public List<String> getKeys() {
			return keys;
		}

		@Override
		public String getName( String key ) {
			if( key.equals( NULL_VALUE_OPTION_KEY ) ) key = "default";
			return product.rb().text( BundleKey.PROPS, "point-type-" + key );
		}

	}

}
