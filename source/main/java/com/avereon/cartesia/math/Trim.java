package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.List;

//   | L | A | C | P |
// L | ✓ | ✓ | ✓ |   |
// A | ✓ | ✓ | ✓ |   |
// C | ✓ | ✓ | ✓ |   |
// P |   |   |   |   |

@CustomLog
public class Trim extends CadEdit{

	/**
	 * Trim the trim shape to the edge shape.
	 *
	 * @param tool The design tool
	 * @param trim The trim shape
	 * @param edge The edge shape
	 * @param trimPoint The point used to select the trim shape, in screen coordinates
	 * @param edgePoint The point used to select the edge shape, in screen coordinates
	 */
	public static void trim( DesignTool tool, DesignShape trim, DesignShape edge, Point3D trimPoint, Point3D edgePoint ) {
		List<Point3D> intersections = CadIntersection.getIntersections( trim, edge );
		Point3D target = CadPoints.getNearestOnScreen( tool, edgePoint, intersections );
		update( tool, trim, trimPoint, target );
	}

}
