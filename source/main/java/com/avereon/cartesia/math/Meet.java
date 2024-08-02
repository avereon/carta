package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

import java.util.List;

//   | L | A | C | P |
// L | ✓ | ✓ | ✓ |   |
// A | - | ✓ | ✓ |   |
// C | - | - | ✓ |   |
// P | - | - | - |   |

public class Meet extends CadEdit {

	public static void meet( DesignTool tool, DesignShape trim, DesignShape edge, Point3D trimPoint, Point3D edgePoint ) {
		List<Point3D> intersections = CadIntersection.getIntersections( trim, edge );
		Point3D target = CadPoints.getNearestOnScreen( tool, edgePoint, intersections );
		update( tool, trim, trimPoint, target );
		update( tool, edge, edgePoint, target );
	}

}
