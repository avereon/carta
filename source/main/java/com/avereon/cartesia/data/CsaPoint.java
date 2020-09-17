package com.avereon.cartesia.data;

import com.avereon.cartesia.math.MathEx;
import com.avereon.cartesia.math.Points;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.cartesia.tool.DesignGeometry;
import com.avereon.cartesia.tool.DesignPane;
import com.avereon.data.NodeSettingsWrapper;
import com.avereon.xenon.BundleKey;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.settings.SettingOptionProvider;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.tool.settings.SettingsPageParser;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CsaPoint extends CsaShape {

	public static final String SIZE = "size";

	public static final String TYPE = "type";

	private static final double DEFAULT_SIZE = 1.0;

	private static final Points.Type DEFAULT_TYPE = Points.Type.CROSS;

	private static final double ZERO_DRAW_WIDTH = 0.0;

	public CsaPoint() {
		addModifyingKeys( ORIGIN, SIZE, TYPE );
	}

	public CsaPoint( Point3D origin ) {
		this();
		setOrigin( origin );
	}

	public double calcSize() {
		String size = getSize();
		if( size != null ) return MathEx.eval( size );
		return DEFAULT_SIZE;
	}

	public String getSize() {
		return getValue( SIZE );
	}

	public CsaPoint setSize( String size ) {
		setValue( SIZE, size );
		return this;
	}

	public Points.Type calcType() {
		return Points.parseType( getType() );
	}

	public String getType() {
		return getValue( TYPE );
	}

	public CsaPoint setType( String type ) {
		setValue( TYPE, type );
		return this;
	}

	@Override
	public double calcDrawWidth() {
		return calcType().isClosed() ? ZERO_DRAW_WIDTH : super.calcDrawWidth();
	}

	@Override
	public Color calcFillColor() {
		return calcDrawColor();
	}

	@Override
	public Color calcSelectFillColor() {
		return calcSelectDrawColor();
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, "point" );
		map.putAll( asMap( SIZE, TYPE ) );
		return map;
	}

	public CsaPoint updateFrom( Map<String, Object> map ) {
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
	public List<Shape> generateGeometry( DesignGeometry geometry) {
		double ox = getOrigin().getX();
		double oy = getOrigin().getY();
		Path path = Points.createPoint( calcType(), ox, oy, getRadius() );
		return List.of( configureShape( path ) );
	}

	@Override
	public List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Path path = (Path)shapes.get( 0 );
		MoveTo m = ((MoveTo)path.getElements().get( 0 ));
		ConstructionPoint o = cp( pane, m.xProperty(), m.yProperty() );

		List<ConstructionPoint> cps = List.of( o );
		path.getProperties().put( CONSTRUCTION_POINTS, cps );
		return cps;
	}

	@Override
	public SettingsPage getPropertiesPage( ProgramProduct product ) throws IOException {
		String pagePath = "/com/avereon/cartesia/design/props/point.xml";
		if( page == null ) {
			page = new SettingsPageParser( product, new NodeSettingsWrapper( this ) ).parse( pagePath, BundleKey.PROPS ).get( "point" );
			page.setOptionProviders( Map.of( "point-type-option-provider", new PointTypeOptionProvider( product ) ) );
		}

		return page;
	}

	private double getRadius() {
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
				Points.Type.CROSS.name().toLowerCase(),
				Points.Type.X.name().toLowerCase(),
				Points.Type.REFERENCE.name().toLowerCase(),
				Points.Type.CIRCLE.name().toLowerCase(),
				Points.Type.DIAMOND.name().toLowerCase(),
				Points.Type.SQUARE.name().toLowerCase()
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
