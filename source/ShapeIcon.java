import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import javax.swing.Icon;

/**
 *  An Icon that will paint a filled Shape with a specified Color.
 */
public class ShapeIcon implements Icon
{
	private Shape shape;
	private Color color;
	private boolean antiAliasing = true;

	/**
	 *  Create a ShapeIcon
	 *
	 *  @param shape  the Shape
	 *  @param color  the fill Color of the Shape
	 */
	public ShapeIcon(Shape shape, Color color)
	{
		this.shape = shape;
		this.color = color;
	}

	/**
	 *  Get the Color of the Shape
	 *
	 *  @returns  the Color of the Shape
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 *  Set the Color of the Shape
	 *
	 *  @param color the Color of the Shape
	 */
	public void setColor(Color color)
	{
		this.color = color;
	}

	/**
	 *  Get the Shape
	 *
	 *  @returns the Shape
	 */
	public Shape getShape()
	{
		return shape;
	}

	/**
	 *  Set the Shape
	 *
	 *  @param shape the Shape to be painted
	 */
	public void setShape(Shape shape)
	{
		this.shape = shape;
	}

	/**
	 *  Use AntiAliasing when painting the shape
	 *
	 *  @returns true for AntiAliasing false otherwise
	 */
	public boolean isAntiAliasing()
	{
		return antiAliasing;
	}

	/**
	 *  Set AntiAliasing property for painting the Shape
	 *
	 *  @param antiAliasing true for AntiAliasing, false otherwise
	 */
	public void setAntiAliasing(boolean antiAliasing)
	{
		this.antiAliasing = antiAliasing;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public int getIconWidth()
	{
		Rectangle bounds = shape.getBounds();
		return bounds.width;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public int getIconHeight()
	{
		Rectangle bounds = shape.getBounds();
		return bounds.height;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		//  Use Graphics2D so we can do antialiasing

		Graphics2D g2d = (Graphics2D)g.create();

		if (antiAliasing)
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//  Handle Icon position within the component and Shape translation
		//  (ie. X/Y positions in bounding rectangle are ignored)

		Rectangle bounds = shape.getBounds();
		g2d.translate(x - bounds.x, y - bounds.y);

		//  Fill the Shape with the specified Color

		g2d.setColor(color);
		g2d.fill( shape );

		g2d.dispose();
	}
}
