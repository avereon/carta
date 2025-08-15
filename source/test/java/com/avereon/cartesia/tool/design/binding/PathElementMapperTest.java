package com.avereon.cartesia.tool.design.binding;

import com.avereon.cartesia.data.DesignPath;
import javafx.scene.shape.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class PathElementMapperTest {

	@Test
	void mapMove() {
		PathElementMapper mapper = Mappers.getMapper( PathElementMapper.class );
		DesignPath.Step step = new DesignPath.Step( DesignPath.Command.M, 10, 20 );
		PathElement element = mapper.map( step );
		assertThat( element ).isNotNull();
		assertThat( element ).isInstanceOf( MoveTo.class );
		assertThat( ((MoveTo)element).getX() ).isEqualTo( 10 );
		assertThat( ((MoveTo)element).getY() ).isEqualTo( 20 );
	}

	@Test
	void mapLine() {
		PathElementMapper mapper = Mappers.getMapper( PathElementMapper.class );
		DesignPath.Step step = new DesignPath.Step( DesignPath.Command.L, 10, 20 );
		PathElement element = mapper.map( step );
		assertThat( element ).isNotNull();
		assertThat( element ).isInstanceOf( javafx.scene.shape.LineTo.class );
		assertThat( ((LineTo)element).getX() ).isEqualTo( 10 );
		assertThat( ((LineTo)element).getY() ).isEqualTo( 20 );
	}

	@Test
	void mapArc() {
		PathElementMapper mapper = Mappers.getMapper( PathElementMapper.class );
		DesignPath.Step step = new DesignPath.Step( DesignPath.Command.A, 10, 20, 30, 40, 50, 60, 70 );
		PathElement element = mapper.map( step );
		assertThat( element ).isNotNull();
		assertThat( element ).isInstanceOf( ArcTo.class );
		assertThat( ((ArcTo)element).getRadiusX() ).isEqualTo( 30 );
		assertThat( ((ArcTo)element).getRadiusY() ).isEqualTo( 40 );
		assertThat( ((ArcTo)element).getXAxisRotation() ).isEqualTo( 50 );
		assertThat( ((ArcTo)element).getX() ).isEqualTo( 10 );
		assertThat( ((ArcTo)element).getY() ).isEqualTo( 20 );
		assertThat( ((ArcTo)element).isLargeArcFlag() ).isTrue();
		assertThat( ((ArcTo)element).isSweepFlag() ).isTrue();
	}

	@Test
	void mapQuad() {
		PathElementMapper mapper = Mappers.getMapper( PathElementMapper.class );
		DesignPath.Step step = new DesignPath.Step( DesignPath.Command.Q, 10, 20, 30, 40 );
		PathElement element = mapper.map( step );
		assertThat( element ).isNotNull();
		assertThat( element ).isInstanceOf( QuadCurveTo.class );
		assertThat( ((QuadCurveTo)element).getX() ).isEqualTo( 30 );
		assertThat( ((QuadCurveTo)element).getY() ).isEqualTo( 40 );
		assertThat( ((QuadCurveTo)element).getControlX() ).isEqualTo( 10 );
		assertThat( ((QuadCurveTo)element).getControlY() ).isEqualTo( 20 );
		assertThat( ((QuadCurveTo)element).getX() ).isEqualTo( 30 );
		assertThat( ((QuadCurveTo)element).getY() ).isEqualTo( 40 );
	}

	@Test
	void mapCubic() {
		PathElementMapper mapper = Mappers.getMapper( PathElementMapper.class );
		DesignPath.Step step = new DesignPath.Step( DesignPath.Command.B, 10, 20, 30, 40, 50, 60 );
		PathElement element = mapper.map( step );
		assertThat( element ).isNotNull();
		assertThat( element ).isInstanceOf( CubicCurveTo.class );
		assertThat( ((CubicCurveTo)element).getControlX1() ).isEqualTo( 10 );
		assertThat( ((CubicCurveTo)element).getControlY1() ).isEqualTo( 20 );
		assertThat( ((CubicCurveTo)element).getControlX2() ).isEqualTo( 30 );
		assertThat( ((CubicCurveTo)element).getControlY2() ).isEqualTo( 40 );
		assertThat( ((CubicCurveTo)element).getX() ).isEqualTo( 50 );
		assertThat( ((CubicCurveTo)element).getY() ).isEqualTo( 60 );
	}

	@Test
	void mapClose() {
		PathElementMapper mapper = Mappers.getMapper( PathElementMapper.class );
		DesignPath.Step step = new DesignPath.Step( DesignPath.Command.Z );
		PathElement element = mapper.map( step );
		assertThat( element ).isNotNull();
		assertThat( element ).isInstanceOf( ClosePath.class );
	}

}
