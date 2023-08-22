import java.awt.BasicStroke;
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
public class OutlineIcon implements Icon
{
	private Shape shape;
	private Color color;
	private int thickness;
	private boolean antiAliasing = true;

	private BasicStroke stroke;
	private Rectangle strokeBounds;

	/**
	 *  Create a OutlineIcon with a BLACK outline 1 pixel thick
	 *
	 *  @param shape  the Shape
	 */
	public OutlineIcon(Shape shape)
	{
		this(shape, Color.BLACK, 1);
	}

	/**
	 *  Create a OutlineIcon with an outline using the
	 *  specified Color and a thickness of 1 pixel
	 *
	 *  @param shape  the Shape
	 *  @param color  the fill Color of the Shape
	 */
	public OutlineIcon(Shape shape, Color color)
	{
		this(shape, color, 1);
	}

	/**
	 *  Create a OutlineIcon with a BLACK outline using the
	 *  specified thickness.
	 *
	 *  @param shape  the Shape
	 *  @param thickness  the thickness of the Shape outline
	 */
	public OutlineIcon(Shape shape, int thickness)
	{
		this(shape, Color.BLACK, thickness);
	}

	/**
	 *  Create a OutlineIcon with an outline of the specified
	 *  Color and thicness
	 *
	 *  @param shape  the Shape
	 *  @param color  the Color of the Shape outline
	 *  @param thickness  the thickness of the Shape outline
	 */
	public OutlineIcon(Shape shape, Color color, int thickness)
	{
		setShape( shape );
		setColor( color );
		setThickness( thickness );
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
		resetStroke();
	}

	/**
	 *  Get the Shape outline thickness
	 *
	 *  @returns the Shape outline thickness
	 */
	public int getThickness()
	{
		return thickness;
	}

	/**
	 *  Set the Shape outline thickness
	 *
	 *  @param thickness the Shape outline thickness
	 */
	public void setThickness(int thickness)
	{
		this.thickness = thickness;
		resetStroke();
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

	private void resetStroke()
	{
		stroke = new BasicStroke( thickness );
		strokeBounds = stroke.createStrokedShape(shape).getBounds();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public int getIconWidth()
	{
		int width = strokeBounds.width + getAdjustment();

		return width;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public int getIconHeight()
	{
		int height = strokeBounds.height + getAdjustment();

		return height;
	}

	private int getAdjustment()
	{
		//  For odd thicknesses

		if (thickness %2 == 1)
			return -1;

		//  For even thicknesses we also need to check for anti aliasing

		return (isAntiAliasing()) ? 1 : 0;
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

		//  Paint the Shape outline at the top/left position of the icon.
		//  Need to do an adjustment when using an odd Stroke thickness

		int shift = (thickness % 2 == 0) ? 0 : -1;


		//  Handle Icon position within the component and Shape translation
		//  (ie. X/Y positions in bounding rectangle are ignored)

		g2d.translate(x - strokeBounds.x + shift, y - strokeBounds.y + shift);

		//  Fill the Shape with the specified Color

		g2d.setStroke( stroke );
		g2d.setColor(color);
		g2d.draw( shape );

		g2d.dispose();
	}
}
