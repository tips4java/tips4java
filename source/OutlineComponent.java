import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import javax.swing.JComponent;

/**
 *  A component that paints the outline of a Shape object. Click detection will
 *  be determined by the Shape itself, not the bounding Rectangle of the Shape.
 *
 *  Shape objects can be created with an X/Y offset. These offsets will
 *  be ignored and the Shape outline will always be painted at (0, 0) so the
 *  outline is fully contained within the component.
 */
public class OutlineComponent extends JComponent
{
	private Shape shape;
	private int thickness = 1;
	private boolean antiAliasing = true;

	private BasicStroke stroke;
	private Rectangle strokeBounds;

	/**
	 *  Create a OutlineComponent with a 1 pixel BLACK outline
	 *
	 *  @param shape the shape to be painted
	 */
	public OutlineComponent(Shape shape)
	{
		this(shape, Color.BLACK, 1);
	}

	/**
	 *  Create a OutlineComponent with a 1 pixel outline painted in the
	 *  specified color
	 *
	 *  @param shape the shape to be painted
	 *  @param color the outline color of the shape
	 */
	public OutlineComponent(Shape shape, Color color)
	{
		this(shape, color, 1);
	}

	/**
	 *  Create a OutlineComponent with a BLACK outline painted at the
	 *  specified thickness
	 *
	 *  @param shape the shape to be painted
	 *  @param thickness the thickness of the outline
	 */
	public OutlineComponent(Shape shape, int thickness)
	{
		this(shape, Color.BLACK, thickness);
	}

	/**
	 *  Create a OutlineComponent with the outline painted in the specified
	 *  color and thickness
	 *
	 *  @param shape the Shape outline to be painted
	 *  @param color the color of the outline
	 *  @param thicknes the thickness of the outline
	 */
	public OutlineComponent(Shape shape, Color color, int thickness)
	{
		setShape( shape );
		setForeground( color );
		setThickness( thickness );

		setOpaque( false );
	}

	/**
	 *  Get the Shape of the component
	 *
	 *  @returns the the Shape of the compnent
	 */
	public Shape getShape()
	{
		return shape;
	}

	/**
	 *  Set the Shape for this component
	 *
	 *  @param shape the Shape of the component
	 */
	public void setShape(Shape shape)
	{
		this.shape = shape;
		resetStroke();
		revalidate();
		repaint();
	}

	/**
	 *	Get the thickness of the Shape outline when painted
	 *
	 *  @return the Shape outline thickness
	 */
	public int getThickness()
	{
		return thickness;
	}

	/**
	 *  Set the Shape outline thickness
	 *
	 *  @param thickness the Shape outline in pixels
	 */
	public void setThickness(int thickness)
	{
		this.thickness = thickness;
		resetStroke();
		revalidate();
		repaint();
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
		revalidate();
		repaint();
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
	public Dimension getPreferredSize()
	{
		//  Include Border insets and Shape bounds

		Insets insets = getInsets();
		int adjustment = getAdjustment();

		//  Determine the preferred size

		int width = insets.left + insets.right + strokeBounds.width + adjustment;
		int height = insets.top + insets.bottom + strokeBounds.height + adjustment;

		return new Dimension(width, height);
	}

	private int getAdjustment()
	{
		//  For odd thicknesses

		if (thickness % 2 == 1)
			return -1;

		//  For even thicknesses we also need to check for anti aliasing

		return (isAntiAliasing()) ? 1 : 0;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Dimension getMaximumSize()
	{
		return getPreferredSize();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		//  Graphics2D is required for antialiasing and painting Shapes

		Graphics2D g2d = (Graphics2D)g.create();

		if (isAntiAliasing())
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//  The draw() method will paint the Shape outline (up/left) of the
		//  filled Shape. Since we want all the painting to be done at a zero
		//  offset we must manully translate the Shape (down/right) before it
		//  is painted.

		int shift = getShift();

		//  Shape translation (ie. non-zero X/Y position in bounding rectangle)
		//  and Border insets.

		Insets insets = getInsets();

		//  Do all translations at once

		int translateX = insets.left - strokeBounds.x + shift;
		int translateY = insets.top - strokeBounds.y + shift;
		g2d.translate(translateX, translateY);

		//  Draw the Shape with the specified Color and thickness

		g2d.setStroke( stroke );
		g2d.setColor( getForeground());
		g2d.draw( shape );

		g2d.dispose();
	}

    /**
     *  This method will only determine if the point is in the bounds of the
     *  Shape. The outline of the Shape as painted by the draw() method
     *  is not considered part of the Shape.
     *
     * {@inheritDoc}
     */
	@Override
	public boolean contains(int x, int y)
	{
		Insets insets = getInsets();
		int shift = getShift();

		//  Check to see if the Shape contains the point. Take into account
		//  the Shape X/Y coordinates, Border insets and Shape translation.

		int translateX = x + strokeBounds.x - insets.left;
		int translateY = y + strokeBounds.y - insets.top;

		return shape.contains(translateX, translateY);
	}

	/**
	 *  The painting of the Shape outline may be shifted depending on the
	 *  stroke thickness. The painting is shifted to make sure it starts
	 *  at the top/left of the component.
	 */
	private int getShift()
	{
		int shift = (thickness % 2 == 0) ? 0 : -1;
		return shift;
	}
}
