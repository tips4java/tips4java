import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.GeneralPath;

/**
 *	Collection of static methods to make working with Shapes easier.
 */
public class ShapeUtils
{
	/**
	 *  The points of the Shape are calculated by specifying the radius of each
	 *  point. You must specify the number of points to be calculated as well
	 *  as an array (varargs) containing the radius for each point. The
	 *  calculation of each Point will use the radius modulo array.length.
	 *  For example you can create Shapes like the following:
	 *
	 *  Shape hexagon = ShapeUtils.radiusShape(6, 10);
	 *  Shape star = ShapeUtils.radiusShape(16, 20, 8);
	 *  Shape doubleStar = ShapeUtils.radiusShape(16, 20, 8, 14, 8);
	 *
	 *  @param point the number of points contained in the Shape
	 *  @param radii one or more radius used to calculate a specific point
	 *
	 *  @returns a Shape containing all the calculated points
	 */
	 public static Shape radiusShape(int points, int... radii)
	 {
		Polygon polygon = new Polygon();

		for (int i = 0; i < points; i++)
		{
			double radians = Math.toRadians(i * 360 / points);
			int radius = radii[i % radii.length];

			double x = Math.cos(radians) * radius;
			double y = Math.sin(radians) * radius;

			polygon.addPoint((int)x, (int)y);
		}

		Rectangle bounds = polygon.getBounds();
		polygon.translate(-bounds.x, -bounds.y);

		return polygon;
	}

	/**
	 *  Rotate a Shape about its center in a clockwise direction by a specified angle
	 *
	 *  @param shape the Shape to rotate
	 *  @param angle the angle in degrees
	 *
	 *  @returns a new rotated Shape object
	 */
	public static Shape rotate(Shape shape, int angle)
	{
		double radians = Math.toRadians( angle );
		Rectangle bounds = shape.getBounds();
		double anchorX = bounds.x + bounds.width / 2;
		double anchorY = bounds.y + bounds.height / 2;
		AffineTransform at = AffineTransform.getRotateInstance(radians, anchorX, anchorY);
		Shape rotated = at.createTransformedShape(shape);

		return rotated;
	}

	/**
	 *  Translate a Shape
	 *
	 *  @param shape the Shape to rotate
	 *  @param point the Point containing the requested translation
	 *
	 *  @returns a new translated Shape object
	 */
	public static Shape translate(Shape shape, Point point)
	{
		AffineTransform at = AffineTransform.getTranslateInstance(point.x, point.y);
		Shape translated = at.createTransformedShape(shape);

		return translated;
	}
}
